package com.example.btsallot.data.room.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val email: String,
    val role: String,
    val photoUrl: String?

)
