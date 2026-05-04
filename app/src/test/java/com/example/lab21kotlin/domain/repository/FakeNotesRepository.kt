package com.example.lab21kotlin.test.domain

import com.example.lab21kotlin.domain.model.Note
import com.example.lab21kotlin.domain.repository.NotesRepository

/**
 * Fake-реализация репозитория для юнит-тестов.
 * Хранит данные в памяти, не требует БД/сети.
 * Задание 2: изоляция тестов от внешних зависимостей.
 */
class FakeNotesRepository : NotesRepository {

    private val notes = mutableListOf<Note>()

    // Методы для настройки тестовых данных
    fun addNotes(vararg note: Note) {
        notes.addAll(note)
    }

    fun clear() {
        notes.clear()
    }

    // Реализация интерфейса
    override suspend fun getNotes(): Result<List<Note>> {
        return Result.success(notes.toList())
    }

    override suspend fun addNote(note: Note): Result<Unit> {
        notes.add(note)
        return Result.success(Unit)
    }

    override suspend fun deleteNote(id: Long): Result<Unit> {
        val removed = notes.removeIf { it.id == id }
        return if (removed) Result.success(Unit) else Result.failure(Exception("Not found"))
    }
}