package com.example.btsallot.data.model

data class DutyForm(
    val meetingName: String = "",
    val customMeetingName: String = "",
    val startMinutes: Int = 0,
    val endMinutes: Int = 0,
    val volunteersRequired: Int = 2,
    val location: String = "",
    val customLocationName: String = ""
)
