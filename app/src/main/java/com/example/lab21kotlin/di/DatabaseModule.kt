package com.example.lab21kotlin.di

import android.content.Context
import androidx.room.Room
import com.example.lab21kotlin.data.local.AppDatabase
import com.example.lab21kotlin.data.local.dao.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "notes_mvvm_db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideNoteDao(database: AppDatabase): NoteDao = database.noteDao()
}