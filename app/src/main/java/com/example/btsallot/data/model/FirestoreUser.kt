package com.example.btsallot.data.model

data class FirestoreUser(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "VOLUNTEER",
    val photoUrl: String? = null
)
