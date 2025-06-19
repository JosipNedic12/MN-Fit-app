package com.example.mnfit.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.example.mnfit.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _fcmState = MutableStateFlow<FCMState>(FCMState.Idle)
    val fcmState: StateFlow<FCMState> = _fcmState.asStateFlow()

    private val _currentUserUid = MutableStateFlow<String?>(null)
    val currentUserUid: StateFlow<String?> = _currentUserUid.asStateFlow()

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = _currentUserUid
        .map { it != null }
        .stateIn(
            scope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
            started = kotlinx.coroutines.flow.SharingStarted.Eagerly,
            initialValue = auth.currentUser != null
        )

    init {
        // Keep _currentUserUid up-to-date
        _currentUserUid.value = auth.currentUser?.uid

        // Listen for auth state changes (user logs in/out from another device)
        auth.addAuthStateListener { firebaseAuth ->
            _currentUserUid.value = firebaseAuth.currentUser?.uid
        }
    }

    fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        role: String
    ) {
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid ?: ""
                    val user = User(
                        uid = uid,
                        email = email,
                        firstName = firstName,
                        lastName = lastName,
                        role = role,
                        createdAt = null
                    )
                    val userMap = hashMapOf(
                        "uid" to user.uid,
                        "email" to user.email,
                        "firstName" to user.firstName,
                        "lastName" to user.lastName,
                        "role" to user.role,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                    firestore.collection("users").document(uid)
                        .set(userMap)
                        .addOnSuccessListener {
                            subscribeToAllUsersTopic()
                            _authState.value = AuthState.Success
                        }
                        .addOnFailureListener { e ->
                            _authState.value = AuthState.Error("Failed to save user: ${e.message}")
                        }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Registration failed")
                }
            }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    subscribeToAllUsersTopic()
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Login failed")
                }
            }
    }

    private fun subscribeToAllUsersTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Subscribed to all_users")
                    _fcmState.value = FCMState.Success
                } else {
                    val errorMsg = task.exception?.localizedMessage ?: "FCM subscription failed"
                    Log.d("FCM", "Subscribe failed: $errorMsg")
                    _fcmState.value = FCMState.Error(errorMsg)
                }
            }
    }

    // Optional: Unsubscribe from topic on logout
    fun logout() {
        auth.signOut()
        FirebaseMessaging.getInstance().unsubscribeFromTopic("all_users")
        resetState()
    }

    fun resetState() {
        _authState.value = AuthState.Idle
        _fcmState.value = FCMState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class FCMState {
    object Idle : FCMState()
    object Success : FCMState()
    data class Error(val message: String) : FCMState()
}
