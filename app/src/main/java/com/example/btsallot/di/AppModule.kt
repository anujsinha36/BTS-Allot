package com.example.btsallot.di

import android.content.Context
import androidx.room.Room
import com.example.btsallot.data.repository.AuthRepositoryImpl
import com.example.btsallot.domain.repository.DutyRepository
import com.example.btsallot.data.repository.DutyRepositoryImpl
import com.example.btsallot.data.room.duty.DutyDao
import com.example.btsallot.data.room.duty.DutyDatabase
import com.example.btsallot.domain.repository.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
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
    fun providesFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun providesAuthRepository(
        @ApplicationContext context: Context,
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository{
        return AuthRepositoryImpl(context, auth, firestore)
    }

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
    fun provideDutyRepository(dutyDao: DutyDao, firebaseDB: FirebaseFirestore): DutyRepository{
        return DutyRepositoryImpl(dutyDao,firebaseDB)
    }

}