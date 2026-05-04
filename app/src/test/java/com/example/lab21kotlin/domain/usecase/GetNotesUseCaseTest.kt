package com.example.lab21kotlin.test.domain.usecase

import com.example.lab21kotlin.domain.model.Note
import com.example.lab21kotlin.domain.usecase.GetNotesUseCase
import com.example.lab21kotlin.test.domain.FakeNotesRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit-тесты для GetNotesUseCase.
 * Задание 2: тестирование UseCase в изоляции с фейковым репозиторием.
 */
class GetNotesUseCaseTest {

    private lateinit var fakeRepository: FakeNotesRepository
    private lateinit var getNotesUseCase: GetNotesUseCase

    @Before
    fun setup() {
        fakeRepository = FakeNotesRepository()
        getNotesUseCase = GetNotesUseCase(fakeRepository)
    }

    @Test
    fun `getNotes returns empty list when repository is empty`() = runTest {
        // Arrange: репозиторий пуст
        fakeRepository.clear()

        // Act
        val result = getNotesUseCase()

        // Assert
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `getNotes returns notes when repository has data`() = runTest {
        // Arrange: добавляем тестовые заметки
        val testNotes = listOf(
            Note(id = 1L, title = "Заметка 1", content = "Текст 1"),
            Note(id = 2L, title = "Заметка 2", content = "Текст 2")
        )
        fakeRepository.addNotes(*testNotes.toTypedArray())

        // Act
        val result = getNotesUseCase()

        // Assert
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).containsExactlyElementsIn(testNotes)
    }

    @Test
    fun `getNotes returns failure when repository throws exception`() = runTest {
        // Arrange: создаём репозиторий, который всегда выбрасывает ошибку
        val errorRepository = object : com.example.lab21kotlin.domain.repository.NotesRepository {
            override suspend fun getNotes(): Result<List<Note>> = Result.failure(RuntimeException("Network error"))
            override suspend fun addNote(note: Note): Result<Unit> = Result.success(Unit)
            override suspend fun deleteNote(id: Long): Result<Unit> = Result.success(Unit)
        }
        val useCase = GetNotesUseCase(errorRepository)

        // Act
        val result = useCase()

        // Assert
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Network error")
    }
}