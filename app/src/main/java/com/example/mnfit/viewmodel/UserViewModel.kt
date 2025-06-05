package com.example.mnfit.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mnfit.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class UserViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // User state
    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = _currentUser.asStateFlow()

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName.asStateFlow()

    private val _role = MutableStateFlow("")
    val role: StateFlow<String> = _role.asStateFlow()

    private val _photoUrl = MutableStateFlow<String?>(null)
    val photoUrl: StateFlow<String?> = _photoUrl.asStateFlow()

    private var userListener: ListenerRegistration? = null

    init {
        listenForUserData()
    }

    private fun listenForUserData() {
        val user = auth.currentUser
        if (user != null) {
            userListener?.remove()
            userListener = db.collection("users").document(user.uid)
                .addSnapshotListener { document, _ ->
                    if (document != null && document.exists()) {
                        _firstName.value = document.getString("firstName") ?: ""
                        _lastName.value = document.getString("lastName") ?: ""
                        _role.value = document.getString("role") ?: ""
                        _photoUrl.value = document.getString("photoUrl")
                    }
                }
        } else {
            _firstName.value = ""
            _lastName.value = ""
            _role.value = ""
            _photoUrl.value = null
            userListener?.remove()
            userListener = null
        }
    }

    fun refreshCurrentUser() {
        _currentUser.value = auth.currentUser
        listenForUserData()
    }

    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e("UPLOAD_DEBUG", "InputStream is null for URI: $uri")
                return null
            }
            val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
            val outputStream = FileOutputStream(tempFile)
            val bytesCopied = inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            Log.d("UPLOAD_DEBUG", "Temp file created: ${tempFile.absolutePath}, size: ${tempFile.length()}, bytesCopied: $bytesCopied")
            tempFile
        } catch (e: Exception) {
            Log.e("UPLOAD_DEBUG", "Exception in getFileFromUri: ${e.message}", e)
            null
        }
    }
    fun uploadProfilePhoto(
        context: Context,
        imageUri: Uri?,
        imageBitmap: Bitmap?,
        onResult: (Boolean, String?) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        Log.d("UPLOAD_DEBUG", "Starting uploadProfilePhoto")
        Log.d("UPLOAD_DEBUG", "userId: $userId")
        Log.d("UPLOAD_DEBUG", "imageUri: $imageUri")
        Log.d("UPLOAD_DEBUG", "imageBitmap is null: ${imageBitmap == null}")

        if (userId == null) {
            Log.e("UPLOAD_DEBUG", "User ID is null, aborting")
            onResult(false, "User not logged in")
            return
        }

        viewModelScope.launch {
            try {
                val storageRef = storage.reference.child("profile_photos/$userId.jpg")
                Log.d("UPLOAD_DEBUG", "Storage reference created: $storageRef")
                if (imageUri != null) {
                    val file = getFileFromUri(context, imageUri)
                    Log.d("UPLOAD_DEBUG", "Temp file from URI: $file")
                    if (file != null && file.exists() && file.length() > 0) {
                        Log.d("UPLOAD_DEBUG", "Uploading file: ${file.absolutePath}, size: ${file.length()}")
                        storageRef.putFile(Uri.fromFile(file)).await()
                        Log.d("UPLOAD_DEBUG", "Upload from file complete")
                    } else {
                        Log.e("UPLOAD_DEBUG", "File is null, does not exist, or is empty")
                        onResult(false, "Failed to read image file")
                        return@launch
                    }
                } else if (imageBitmap != null) {
                    Log.d("UPLOAD_DEBUG", "Uploading from Bitmap")
                    val baos = ByteArrayOutputStream()
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
                    val data = baos.toByteArray()
                    storageRef.putBytes(data).await()
                    Log.d("UPLOAD_DEBUG", "Upload from Bitmap complete")
                } else {
                    Log.e("UPLOAD_DEBUG", "No image to upload")
                    onResult(false, "No image to upload")
                    return@launch
                }

                val downloadUrl = storageRef.downloadUrl.await().toString()
                Log.d("UPLOAD_DEBUG", "Download URL: $downloadUrl")

                try {
                    db.collection("users").document(userId)
                        .set(mapOf("photoUrl" to downloadUrl), SetOptions.merge())
                        .await()
                    Log.d("UPLOAD_DEBUG", "Firestore set() success")
                    onResult(true, downloadUrl)
                } catch (firestoreEx: Exception) {
                    Log.e("UPLOAD_DEBUG", "Firestore set() failed: ${firestoreEx.message}", firestoreEx)
                    onResult(false, "Failed to update user photo URL: ${firestoreEx.message}")
                }
            } catch (e: Exception) {
                Log.e("UPLOAD_DEBUG", "Exception in uploadProfilePhoto: ${e.message}", e)
                onResult(false, e.message)
            }
        }
    }

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    // Fetch all users from Firestore
    fun fetchAllUsers() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users").get().await()
                val userList = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
                Log.d("USERS_DEBUG", "Fetched ${userList.size} users")
                _users.value = userList
            } catch (e: Exception) {
                Log.e("USERS_DEBUG", "Error fetching users: ${e.message}", e)
            }
        }
    }
    // Update a user's role
    fun updateUserRole(userId: String, newRole: String, onResult: (Boolean) -> Unit = {}) {
        db.collection("users").document(userId)
            .update("role", newRole)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
    private var usersListener: ListenerRegistration? = null

    fun listenForAllUsers() {
        usersListener?.remove()
        usersListener = db.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val userList = snapshot?.documents?.mapNotNull { it.toObject(User::class.java) } ?: emptyList()
                _users.value = userList
            }
    }
    override fun onCleared() {
        super.onCleared()
        userListener?.remove()
    }
}

