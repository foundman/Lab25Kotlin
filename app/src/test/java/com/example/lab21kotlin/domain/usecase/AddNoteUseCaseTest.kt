package com.example.lab21kotlin.test.domain.usecase

import com.example.lab21kotlin.domain.model.Note
import com.example.lab21kotlin.domain.usecase.AddNoteUseCase
import com.example.lab21kotlin.test.domain.FakeNotesRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit-тесты для AddNoteUseCase с проверкой бизнес-правил.
 * Задание 2: тестирование валидации в UseCase.
 */
class AddNoteUseCaseTest {

    private lateinit var fakeRepository: FakeNotesRepository
    private lateinit var addNoteUseCase: AddNoteUseCase

    @Before
    fun setup() {
        fakeRepository = FakeNotesRepository()
        addNoteUseCase = AddNoteUseCase(fakeRepository)
    }

    @Test
    fun `addNote succeeds with valid data`() = runTest {
        // Arrange
        val validNote = Note(id = 1L, title = "Valid Title", content = "Valid content")

        // Act
        val result = addNoteUseCase(validNote)

        // Assert
        assertThat(result.isSuccess).isTrue()
        assertThat(fakeRepository.getNotes().getOrNull()).contains(validNote)
    }

    @Test
    fun `addNote fails when title is blank`() = runTest {
        // Arrange: заметка с пустым заголовком
        val invalidNote = Note(id = 1L, title = "   ", content = "Content")

        // Act
        val result = addNoteUseCase(invalidNote)

        // Assert: UseCase должен отклонить заметку до обращения к репозиторию
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
        assertThat(fakeRepository.getNotes().getOrNull()).isEmpty()
    }

    @Test
    fun `addNote fails when title is empty string`() = runTest {
        // Arrange
        val invalidNote = Note(id = 1L, title = "", content = "Content")

        // Act
        val result = addNoteUseCase(invalidNote)

        // Assert
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("пустым")
    }
}