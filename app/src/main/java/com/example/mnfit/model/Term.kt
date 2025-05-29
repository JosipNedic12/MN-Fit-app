package com.example.mnfit.model

data class Term(
    val termId: String = "",
    val title: String = "",
    val description: String = "",
    val trainerId: String = "",
    val date: Long = 0L, // Store as timestamp (milliseconds since epoch)
    val location: String = "",
    val maxParticipants: Int = 0,
    val participants: List<String> = emptyList()
)
