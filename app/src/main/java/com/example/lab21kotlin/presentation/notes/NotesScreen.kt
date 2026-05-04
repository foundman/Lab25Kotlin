package com.example.lab21kotlin.presentation.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lab21kotlin.domain.model.Note

/**
 * Экран списка заметок (View в MVVM).
 *
 * MVVM:
 * - Подписывается на состояние ViewModel через collectAsStateWithLifecycle()
 * - Отображает данные в зависимости от UiState
 * - Отправляет события пользователю через методы ViewModel
 * - Не содержит бизнес-логики, не обращается к репозиторию напрямую
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel = hiltViewModel()
) {
    // Подписка на StateFlow из ViewModel
    // collectAsStateWithLifecycle() автоматически управляет подпиской по жизненному циклу
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Загружаем данные при первом появлении экрана
    LaunchedEffect(Unit) {
        viewModel.loadNotes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Заметки (MVVM)", fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.toggleAddDialog(true) },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить заметку")
            }
        }
    ) { padding ->
        // Обработка состояний экрана
        when {
            // Состояние: загрузка
            uiState.isLoading && uiState.notes.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Состояние: ошибка
            uiState.error != null -> {
                ErrorDialog(
                    message = uiState.error!!,
                    onDismiss = { viewModel.clearError() },
                    onRetry = { viewModel.loadNotes() }
                )
            }

            // Состояние: список заметок
            else -> {
                if (uiState.notes.isEmpty()) {
                    EmptyState(modifier = Modifier.padding(padding))
                } else {
                    NotesList(
                        notes = uiState.notes,
                        onDelete = { viewModel.deleteNote(it) },
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }

        // Диалог добавления новой заметки
        if (uiState.isAddDialogOpen) {
            AddNoteDialog(
                onConfirm = { title, content -> viewModel.addNote(title, content) },
                onDismiss = { viewModel.toggleAddDialog(false) }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Вспомогательные Composable-функции (SRP: каждая отвечает за своё)

/**
 * Экран "пустого состояния" — когда нет заметок.
 */
@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📭 Нет заметок", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Нажмите +, чтобы добавить первую заметку")
        }
    }
}

/**
 * Список заметок.
 */
@Composable
private fun NotesList(
    notes: List<Note>,
    onDelete: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(notes, key = { it.id }) { note ->
            NoteCard(note = note, onDelete = { onDelete(note) })
        }
    }
}

/**
 * Карточка одной заметки.
 */
@Composable
private fun NoteCard(
    note: Note,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3
            )
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDelete) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

/**
 * Диалог добавления новой заметки.
 */
@Composable
private fun AddNoteDialog(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая заметка") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Заголовок") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Содержание") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, content)
                        title = ""
                        content = ""
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

/**
 * Диалог отображения ошибки.
 */
@Composable
private fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ошибка", color = MaterialTheme.colorScheme.error) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onRetry) {
                Text("Повторить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}