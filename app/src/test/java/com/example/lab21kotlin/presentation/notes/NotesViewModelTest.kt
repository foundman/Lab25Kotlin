package com.example.lab21kotlin.test.presentation.notes

import com.example.lab21kotlin.domain.model.Note
import com.example.lab21kotlin.domain.usecase.AddNoteUseCase
import com.example.lab21kotlin.domain.usecase.GetNotesUseCase
import com.example.lab21kotlin.presentation.notes.NotesUiState
import com.example.lab21kotlin.presentation.notes.NotesViewModel
import com.example.lab21kotlin.test.domain.FakeNotesRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Базовые тесты для NotesViewModel.
 * Задание 5: проверка изменения состояния в ответ на вызовы методов.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeRepository: FakeNotesRepository
    private lateinit var getNotesUseCase: GetNotesUseCase
    private lateinit var addNoteUseCase: AddNoteUseCase
    private lateinit var viewModel: NotesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeRepository = FakeNotesRepository()
        getNotesUseCase = GetNotesUseCase(fakeRepository)
        addNoteUseCase = AddNoteUseCase(fakeRepository)
        viewModel = NotesViewModel(getNotesUseCase, addNoteUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadNotes updates state from loading to success with notes`() = runTest {
        // Arrange: добавляем тестовые данные в фейковый репозиторий
        fakeRepository.addNotes(
            Note(id = 1L, title = "Test 1", content = "Content 1"),
            Note(id = 2L, title = "Test 2", content = "Content 2")
        )

        // Act: вызываем loadNotes
        viewModel.loadNotes()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: проверяем состояние
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.notes.size).isEqualTo(2)
        assertThat(state.notes.first().title).isEqualTo("Test 1")
    }

    @Test
    fun `loadNotes updates state with error when repository fails`() = runTest {
        // Arrange: создаём UseCase с репозиторием, который всегда падает
        val errorRepository = object : com.example.lab21kotlin.domain.repository.NotesRepository {
            override suspend fun getNotes(): Result<List<Note>> =
                Result.failure(RuntimeException("Network error"))
            override suspend fun addNote(note: Note): Result<Unit> = Result.success(Unit)
            override suspend fun deleteNote(id: Long): Result<Unit> = Result.success(Unit)
        }
        val vm = NotesViewModel(
            GetNotesUseCase(errorRepository),
            AddNoteUseCase(errorRepository)
        )

        // Act
        vm.loadNotes()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = vm.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isEqualTo("Network error")
    }

    @Test
    fun `addNote with valid data reloads notes and closes dialog`() = runTest {
        // Act: добавляем заметку через ViewModel
        viewModel.addNote("New Note", "New content")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: диалог закрыт, заметка добавлена
        val state = viewModel.uiState.value
        assertThat(state.isAddDialogOpen).isFalse()
        assertThat(state.notes.any { it.title == "New Note" }).isTrue()
    }

    @Test
    fun `addNote with blank title does not add note`() = runTest {
        // Act: пытаемся добавить заметку с пустым заголовком
        viewModel.addNote("", "Content")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: заметка не добавлена, ошибка не показана (валидация в UseCase)
        val state = viewModel.uiState.value
        assertThat(state.notes.isEmpty()).isTrue()
        assertThat(state.error).isNull()
    }

    @Test
    fun `toggleAddDialog updates dialog state`() = runTest {
        // Act & Assert
        assertThat(viewModel.uiState.value.isAddDialogOpen).isFalse()

        viewModel.toggleAddDialog(true)
        assertThat(viewModel.uiState.value.isAddDialogOpen).isTrue()

        viewModel.toggleAddDialog(false)
        assertThat(viewModel.uiState.value.isAddDialogOpen).isFalse()
    }
}