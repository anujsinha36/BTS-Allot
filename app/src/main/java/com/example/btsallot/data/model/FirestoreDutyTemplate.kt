package com.example.btsallot.data.model

data class FirestoreDutyTemplate(
    val id: String = "",
    val dayOfWeek: String = "",
    val duty: DutyForm = DutyForm()
)
