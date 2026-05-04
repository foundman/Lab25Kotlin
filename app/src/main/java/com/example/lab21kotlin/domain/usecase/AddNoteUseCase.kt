package com.example.lab21kotlin.domain.usecase

import com.example.lab21kotlin.domain.model.Note
import com.example.lab21kotlin.domain.repository.NotesRepository
import javax.inject.Inject

/**
 * UseCase для добавления заметки с бизнес-правилом.
 */
class AddNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(note: Note): Result<Unit> {
        // Бизнес-правило: заголовок не может быть пустым
        if (note.title.isBlank()) {
            return Result.failure(IllegalArgumentException("Заголовок не может быть пустым"))
        }
        return repository.addNote(note)
    }
}