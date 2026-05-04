package com.example.lab21kotlin.test.data

import com.example.lab21kotlin.data.local.entity.NoteEntity
import com.example.lab21kotlin.data.local.dao.NoteDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Fake DAO для тестирования репозитория без реальной БД.
 * Задание 3: изоляция репозитория от Room.
 */
class FakeNoteDao : NoteDao {

    private val notes = mutableListOf<NoteEntity>()

    fun addNotes(vararg note: NoteEntity) {
        notes.addAll(note)
    }

    fun clear() {
        notes.clear()
    }

    override fun getAll(): Flow<List<NoteEntity>> {
        return flowOf(notes.toList().sortedByDescending { it.createdAt })
    }

    override suspend fun insert(note: NoteEntity): Long {
        val id = note.id.takeIf { it != 0L } ?: (notes.maxOfOrNull { it.id } ?: 0) + 1
        val newNote = note.copy(id = id)
        notes.add(newNote)
        return id
    }

    override suspend fun deleteById(id: Long) {
        notes.removeIf { it.id == id }
    }
    override suspend fun deleteAll() { notes.clear() }
}