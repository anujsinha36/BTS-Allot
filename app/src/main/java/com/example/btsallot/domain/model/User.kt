package com.example.btsallot.domain.model

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val role: String, // e.g., "VOLUNTEER", "ADMIN"
    val photoUrl: String?
)
