package com.example.btsallot.data.model

data class DutyForm(
    val meetingName: String = "",
    val startMinutes: Int = 0,
    val endMinutes: Int = 0,
    val btsRequired: Int = 2,
    val location: String = "",
    val notes: String? = null
)
