package com.example.lab21kotlin.domain.model

/**
 * Доменная модель заметки.
 * Чистый Kotlin — без аннотаций Room/Gson.
 * MVVM: Model — данные и бизнес-правила.
 */
data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)