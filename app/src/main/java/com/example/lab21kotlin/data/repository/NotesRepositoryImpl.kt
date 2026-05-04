package com.example.lab21kotlin.data.repository

import com.example.lab21kotlin.data.local.dao.NoteDao
import com.example.lab21kotlin.data.local.mapper.toDomain
import com.example.lab21kotlin.data.local.mapper.toEntity
import com.example.lab21kotlin.domain.model.Note
import com.example.lab21kotlin.domain.repository.NotesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Реализация репозитория с использованием Room.
 * MVVM: Data-слой реализует контракт из Domain.
 */
class NotesRepositoryImpl @Inject constructor(
    private val dao: NoteDao
) : NotesRepository {

    override suspend fun getNotes(): Result<List<Note>> = try {
        // Для MVVM используем first() для получения списка один раз
        // В реальном приложении можно вернуть Flow и подписаться в ViewModel
        Result.success(dao.getAll().first().map { it.toDomain() })
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun addNote(note: Note): Result<Unit> = try {
        dao.insert(note.toEntity())
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteNote(id: Long): Result<Unit> = try {
        dao.deleteById(id)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}