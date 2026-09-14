package com.example.btsallot.domain.model

data class Duty(
    val id: String,
    val date: String,
    val meetingName: String,
    val startMinutes: Int,
    val endMinutes: Int,
    val btsRequired:Int,
    val btsReservedCount: Int = 0,
    val location: String,
    val notes: String?
)
