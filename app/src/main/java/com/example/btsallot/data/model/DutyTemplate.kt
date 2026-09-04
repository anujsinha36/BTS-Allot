package com.example.btsallot.data.model

data class DutyTemplate(
    val id: String = "",
    val dayOfWeek: String = "",
    val duty: DutyForm = DutyForm()
)
