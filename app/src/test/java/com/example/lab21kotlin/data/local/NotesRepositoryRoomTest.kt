package com.example.lab21kotlin.test.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.lab21kotlin.data.local.AppDatabase
import com.example.lab21kotlin.data.local.dao.NoteDao
import com.example.lab21kotlin.data.local.mapper.toDomain
import com.example.lab21kotlin.data.repository.NotesRepositoryImpl
import com.example.lab21kotlin.domain.model.Note
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Интеграционный тест репозитория с реальной in-memory БД Room.
 * Задание 4: проверка совместной работы репозитория + Room.
 *
 * Отличие от unit-теста с FakeDao:
 * - Проверяется реальная работа SQL-запросов Room
 * - Проверяется маппинг Entity ↔ Domain
 * - Проверяется поведение @PrimaryKey(autoGenerate)
 */
@RunWith(RobolectricTestRunner::class)
class NotesRepositoryRoomTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: NoteDao
    private lateinit var repository: NotesRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Создаём in-memory БД для тестов
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries() // Для тестов можно, в проде — нет!
            .build()

        dao = db.noteDao()
        repository = NotesRepositoryImpl(dao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `addAndGetNotes_withRealRoom_worksCorrectly`() = runTest {
        // Arrange: создаём заметку
        val note = Note(id = 0L, title = "Room Test", content = "Integration test content")

        // Act: добавляем через репозиторий
        repository.addNote(note)

        // Act: читаем через репозиторий
        val result = repository.getNotes()

        // Assert
        assertThat(result.isSuccess).isTrue()
        val notes = result.getOrNull()
        assertThat(notes).isNotEmpty()
        assertThat(notes?.firstOrNull()?.title).isEqualTo("Room Test")
        assertThat(notes?.firstOrNull()?.content).isEqualTo("Integration test content")
        // Проверяем, что ID был сгенерирован автоматически
        assertThat(notes?.firstOrNull()?.id).isGreaterThan(0L)
    }

    @Test
    fun `deleteNote_withRealRoom_removesFromDatabase`() = runTest {
        // Arrange: добавляем две заметки
        val note1 = Note(id = 0L, title = "Keep", content = "...")
        val note2 = Note(id = 0L, title = "Delete", content = "...")
        repository.addNote(note1)
        repository.addNote(note2)

        // Act: удаляем по ID (берём ID из БД)
        val allNotes = repository.getNotes().getOrNull()!!
        val idToDelete = allNotes.first { it.title == "Delete" }.id
        repository.deleteNote(idToDelete)

        // Assert
        val remaining = repository.getNotes().getOrNull()!!
        assertThat(remaining.size).isEqualTo(1)
        assertThat(remaining.firstOrNull()?.title).isEqualTo("Keep")
    }
}