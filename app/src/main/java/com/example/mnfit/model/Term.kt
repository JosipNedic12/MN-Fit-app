package com.example.mnfit.model

data class Term(
    val termId: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val date: Long = 0L,
    val maxParticipants: Int = 0,
    val participants: List<String> = emptyList(),
    val trainerId: String = ""
)

