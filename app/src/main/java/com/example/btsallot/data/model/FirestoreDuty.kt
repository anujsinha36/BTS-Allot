package com.example.btsallot.data.model

data class FirestoreDuty(
    val id: String = "",
    val date: String = "",
    val btsReservedCount: Int = 0,
    val duty: DutyForm = DutyForm()
)
