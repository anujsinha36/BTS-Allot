package com.example.btsallot.data.mappers

import com.example.btsallot.data.model.DutyForm
import com.example.btsallot.data.model.FirestoreDuty
import com.example.btsallot.data.model.FirestoreDutyApplication
import com.example.btsallot.data.model.FirestoreDutyTemplate
import com.example.btsallot.data.model.FirestoreUser
import com.example.btsallot.data.room.duty.DutyEntity
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.domain.model.DutyApplication
import com.example.btsallot.domain.model.DutyTemplate
import com.example.btsallot.domain.model.User

fun FirestoreDuty.toEntity() = DutyEntity(
    id = id,
    date = date,
    meetingName = duty.meetingName,
    startMinutes = duty.startMinutes,
    endMinutes = duty.endMinutes,
    btsRequired = duty.btsRequired,
    btsReservedCount = btsReservedCount,
    location = duty.location,
    notes = duty.notes
)

fun DutyEntity.toDomainDuty() = Duty(
    id = id,
    date = date,
    meetingName = meetingName,
    startMinutes = startMinutes,
    endMinutes = endMinutes,
    btsRequired = btsRequired,
    btsReservedCount = btsReservedCount,
    location = location,
    notes = notes
)

fun Duty.toFireStoreDuty() = FirestoreDuty(
    id = id,
    date = date,
    btsReservedCount = btsReservedCount,
    duty = DutyForm(
        meetingName = meetingName,
        startMinutes = startMinutes,
        endMinutes = endMinutes,
        btsRequired = btsRequired,
        location = location,
        notes = notes
    )
)

fun DutyTemplate.toFirestoreDutyTemplate() = FirestoreDutyTemplate(
    id = id,
    dayOfWeek = dayOfWeek,
    duty = DutyForm(
        meetingName = meetingName,
        startMinutes = startMinutes,
        endMinutes = endMinutes,
        btsRequired = btsRequired,
        location = location,
        notes = notes
    )
)

fun DutyApplication.toFirestoreDutyApplication()= FirestoreDutyApplication(
    dutyId = dutyId,
    userID = userId,
    userName = userName
)

fun FirestoreUser.toDomainUser() = User(
    uid = uid,
    name = name,
    email = email,
    role = role,
    photoUrl = photoUrl
)