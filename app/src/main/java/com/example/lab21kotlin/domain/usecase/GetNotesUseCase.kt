package com.example.lab21kotlin.domain.usecase

import com.example.lab21kotlin.domain.model.Note
import com.example.lab21kotlin.domain.repository.NotesRepository
import javax.inject.Inject

/**
 * UseCase для получения списка заметок.
 * MVVM: ViewModel вызывает UseCase, не зная об источнике данных.
 */
class GetNotesUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(): Result<List<Note>> = repository.getNotes()
}