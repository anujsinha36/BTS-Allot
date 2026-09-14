package com.example.btsallot.domain.model

data class DutyTemplate(
    val id: String = "",
    val dayOfWeek: String,
    val meetingName: String,
    val startMinutes: Int,
    val endMinutes: Int,
    val btsRequired: Int,
    val location: String,
    val notes: String?
)
