package com.example.mnfit.model

import com.google.firebase.Timestamp

data class User(
    val uid: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val createdAt: Timestamp? = null
)
