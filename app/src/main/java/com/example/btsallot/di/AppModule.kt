package com.example.btsallot.di

import android.content.Context
import androidx.room.Room
import com.example.btsallot.data.DutyRepository
import com.example.btsallot.data.repository.DutyRepositoryImpl
import com.example.btsallot.data.room.DutyDao
import com.example.btsallot.data.room.DutyDatabase
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DutyDatabase{
        return Room.databaseBuilder(
            context = context,
            klass = DutyDatabase::class.java,
            name = "duties"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDao(database: DutyDatabase): DutyDao{
        return database.dutyDao()
    }

    @Provides
    @Singleton
    fun provideRepository(dutyDao: DutyDao, firebaseDB: FirebaseFirestore): DutyRepository{
        return DutyRepositoryImpl(dutyDao,firebaseDB)
    }

}