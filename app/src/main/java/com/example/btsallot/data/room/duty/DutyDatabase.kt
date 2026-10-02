package com.example.btsallot.data.room.duty

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.btsallot.data.room.application.ApplicationDao
import com.example.btsallot.data.room.application.DutyApplicationEntity

@Database(
    entities = [DutyEntity::class, DutyApplicationEntity::class],
    version = 2
)

abstract class DutyDatabase: RoomDatabase() {
    abstract fun dutyDao(): DutyDao
    abstract fun applicationDao(): ApplicationDao
}