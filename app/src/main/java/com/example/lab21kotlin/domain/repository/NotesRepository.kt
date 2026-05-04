package com.example.lab21kotlin.domain.repository

import com.example.lab21kotlin.domain.model.Note

/**
 * Интерфейс репозитория — контракт для работы с заметками.
 * MVVM: ViewModel зависит от этой абстракции, а не от конкретной реализации.
 */
interface NotesRepository {
    suspend fun getNotes(): Result<List<Note>>
    suspend fun addNote(note: Note): Result<Unit>
    suspend fun deleteNote(id: Long): Result<Unit>
}