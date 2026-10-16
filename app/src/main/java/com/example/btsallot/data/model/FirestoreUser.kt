package com.example.btsallot.data.model

data class FirestoreUser(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "BTS",
    val photoUrl: String? = null
)
