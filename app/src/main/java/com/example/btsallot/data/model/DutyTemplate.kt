package com.example.btsallot.data.model

data class DutyTemplate(
    val id: String = "",
    val dayOfWeek: String = "",
    val duty: DutyForm = DutyForm()
)


// set-up hilt, room, modify repository functionality from direct firestore to firestore - > room -> viewmodel
// update calendar screen for bts view screen