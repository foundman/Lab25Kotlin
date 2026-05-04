package com.example.lab21kotlin.test.data.repository

import com.example.lab21kotlin.data.local.mapper.toDomain
import com.example.lab21kotlin.data.repository.NotesRepositoryImpl
import com.example.lab21kotlin.domain.model.Note
import com.example.lab21kotlin.test.data.FakeNoteDao
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit-тесты для NotesRepositoryImpl с фейковым DAO.
 * Задание 3: тестирование репозитория без реальной БД.
 */
class NotesRepositoryImplTest {

    private lateinit var fakeDao: FakeNoteDao
    private lateinit var repository: NotesRepositoryImpl

    @Before
    fun setup() {
        fakeDao = FakeNoteDao()
        repository = NotesRepositoryImpl(fakeDao)
    }

    @Test
    fun `addNote inserts note and getNotes returns it`() = runTest {
        // Arrange
        val note = Note(id = 1L, title = "Test", content = "Content")

        // Act: добавляем через репозиторий
        repository.addNote(note)

        // Assert: читаем через репозиторий
        val result = repository.getNotes()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.firstOrNull()?.title).isEqualTo("Test")
        assertThat(result.getOrNull()?.firstOrNull()?.content).isEqualTo("Content")
    }

    @Test
    fun `deleteNote removes note from storage`() = runTest {
        // Arrange: добавляем две заметки
        val note1 = Note(id = 1L, title = "Keep", content = "...")
        val note2 = Note(id = 2L, title = "Delete", content = "...")
        repository.addNote(note1)
        repository.addNote(note2)

        // Act: удаляем одну
        repository.deleteNote(2L)

        // Assert: осталась только первая
        val result = repository.getNotes()
        assertThat(result.getOrNull()?.size).isEqualTo(1)
        assertThat(result.getOrNull()?.firstOrNull()?.id).isEqualTo(1L)
    }
}