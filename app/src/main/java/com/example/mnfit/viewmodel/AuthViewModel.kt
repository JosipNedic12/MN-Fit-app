package com.example.mnfit.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.mnfit.model.User
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    var authState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        role: String
    ) {
        authState = AuthState.Loading
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
                        createdAt = null // Will be set by Firestore server timestamp
                    )
                    // Use a map to set server timestamp
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
                            authState = AuthState.Success
                        }
                        .addOnFailureListener { e ->
                            authState = AuthState.Error("Failed to save user: ${e.message}")
                        }
                } else {
                    authState = AuthState.Error(task.exception?.message ?: "Registration failed")
                }
            }
    }

    fun login(email: String, password: String) {
        authState = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                authState = if (task.isSuccessful) AuthState.Success
                else AuthState.Error(task.exception?.message ?: "Login failed")
            }
    }

    fun resetState() {
        authState = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
