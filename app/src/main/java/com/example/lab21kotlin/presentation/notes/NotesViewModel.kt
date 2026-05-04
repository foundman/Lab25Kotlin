package com.example.lab21kotlin.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab21kotlin.domain.model.Note
import com.example.lab21kotlin.domain.usecase.AddNoteUseCase
import com.example.lab21kotlin.domain.usecase.GetNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана заметок.
 * MVVM:
 * - Хранит состояние экрана (NotesUiState)
 * - Вызывает UseCase для работы с данными
 * - Не знает о Compose/UI-компонентах
 * - Предоставляет методы для обработки событий от View
 */
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel() {

    // Приватный MutableStateFlow для изменения состояния внутри ViewModel
    private val _uiState = MutableStateFlow(NotesUiState())

    // Публичный StateFlow для наблюдения из View (только чтение)
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    /**
     * Загрузка списка заметок.
     * Вызывается при открытии экрана или по запросу пользователя.
     */
    fun loadNotes() {
        // Обновляем состояние: показываем индикатор загрузки
        _uiState.update { it.copy(isLoading = true, error = null) }

        // Запускаем корутину в scope ViewModel
        viewModelScope.launch {
            getNotesUseCase()
                .onSuccess { notes ->
                    // Успех: обновляем список, скрываем индикатор
                    _uiState.update {
                        it.copy(notes = notes, isLoading = false, error = null)
                    }
                }
                .onFailure { error ->
                    // Ошибка: показываем сообщение, скрываем индикатор
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Неизвестная ошибка"
                        )
                    }
                }
        }
    }

    /**
     * Добавление новой заметки.
     * Вызывается при нажатии кнопки "Сохранить" в диалоге.
     */
    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            val note = Note(
                id = System.currentTimeMillis(), // В реальном приложении ID генерирует БД
                title = title.trim(),
                content = content.trim()
            )

            addNoteUseCase(note)
                .onSuccess {
                    // Успех: перезагружаем список и закрываем диалог
                    loadNotes()
                    _uiState.update { it.copy(isAddDialogOpen = false) }
                }
                .onFailure { error ->
                    // Ошибка: показываем сообщение
                    _uiState.update {
                        it.copy(error = error.message ?: "Ошибка добавления")
                    }
                }
        }
    }

    /**
     * Удаление заметки.
     */
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            // В реальном приложении: repository.deleteNote(note.id)
            // Для демо просто обновляем локальный список
            _uiState.update {
                it.copy(notes = it.notes.filter { n -> n.id != note.id })
            }
        }
    }

    /**
     * Управление состоянием диалога добавления.
     */
    fun toggleAddDialog(show: Boolean) {
        _uiState.update { it.copy(isAddDialogOpen = show) }
    }

    /**
     * Очистка сообщения об ошибке.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}