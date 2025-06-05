package com.example.mnfit.model

import com.google.firebase.Timestamp
data class User(
    val uid: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val role: String = "user",
    val createdAt: Timestamp? = null,
    val photoUrl: String? = null
)
