package com.example.btsallot.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "duties")
data class DutyEntity(
    @PrimaryKey val id: String,
    val date: String,
    val meetingName: String,
    val startMinutes: Int,
    val endMinutes: Int,
    val btsRequired:Int,
    val location: String,
    val notes: String?

)
