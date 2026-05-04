package com.example.lab21kotlin.data.local.mapper

import com.example.lab21kotlin.data.local.entity.NoteEntity
import com.example.lab21kotlin.domain.model.Note

fun NoteEntity.toDomain() = Note(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt
)

fun Note.toEntity() = NoteEntity(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt
)