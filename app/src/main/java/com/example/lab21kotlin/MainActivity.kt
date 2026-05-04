package com.example.lab21kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.lab21kotlin.presentation.notes.NotesScreen
import com.example.lab21kotlin.presentation.theme.Lab21KotlinTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity с аннотацией @AndroidEntryPoint.
 * Разрешает внедрение зависимостей через Hilt.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Lab21KotlinTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Экран получает ViewModel через hiltViewModel()
                    // Все зависимости (UseCase, Repository) внедряются автоматически
                    NotesScreen()
                }
            }
        }
    }
}