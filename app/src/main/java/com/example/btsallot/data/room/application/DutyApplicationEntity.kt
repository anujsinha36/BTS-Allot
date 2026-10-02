package com.example.btsallot.data.room.application

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "duty_applications")
data class DutyApplicationEntity(
    @PrimaryKey val id: String,
    val dutyId: String,
    val userId: String,
    val userName: String
)
