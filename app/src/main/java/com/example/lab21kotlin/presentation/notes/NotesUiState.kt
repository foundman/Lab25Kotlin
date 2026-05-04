package com.example.lab21kotlin.presentation.notes

import com.example.lab21kotlin.domain.model.Note

/**
 * Состояние экрана заметок.
 * MVVM: ViewModel хранит состояние, View отображает его.
 */
data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAddDialogOpen: Boolean = false
)