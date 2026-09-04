package com.example.btsallot.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DutyEntity::class],
    version = 1
)

abstract class DutyDatabase: RoomDatabase() {
    abstract fun dutyDao(): DutyDao
}