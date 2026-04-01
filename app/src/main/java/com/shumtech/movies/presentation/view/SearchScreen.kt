package com.shumtech.movies.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shumtech.movies.model.Movie
import com.shumtech.movies.presentation.mvi.SearchIntent
import com.shumtech.movies.presentation.mvi.SearchState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit
) {
    var showContextMenu by remember { mutableStateOf(false) }
    var selectedMovie by remember { mutableStateOf<Movie?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            onIntent(SearchIntent.ClearResults)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Поиск фильмов") },
                navigationIcon = {
                    IconButton(onClick = { onIntent(SearchIntent.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = { onIntent(SearchIntent.UpdateQuery(it)) },
                    modifier = Modifier.Companion.weight(1f),
                    placeholder = { Text("Введите название фильма") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                )

                Button(
                    onClick = { onIntent(SearchIntent.PerformSearch) },
                    enabled = state.query.isNotBlank() && !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.Companion.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Найти")
                    }
                }
            }

            HorizontalDivider()

            Box(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.Companion.fillMaxSize(),
                            contentAlignment = Alignment.Companion.Center
                        ) {
                            Column(horizontalAlignment = Alignment.Companion.CenterHorizontally) {
                                CircularProgressIndicator(modifier = Modifier.Companion.size(48.dp))
                                Spacer(modifier = Modifier.Companion.height(16.dp))
                                Text(
                                    text = "Поиск фильмов...",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    state.error != null -> {
                        Column(
                            modifier = Modifier.Companion
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.Companion.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.Companion.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.Companion.height(16.dp))
                            Text(
                                text = state.error,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.Companion.height(8.dp))
                            Text(
                                text = "Попробуйте другой запрос",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    state.results.isEmpty() && !state.isLoading -> {
                        Column(
                            modifier = Modifier.Companion
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.Companion.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.Companion.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.Companion.height(16.dp))
                            Text(
                                text = "Введите название фильма",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.Companion.height(8.dp))
                            Text(
                                text = "Например: Avatar, Inception, Titanic",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.Companion.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(
                                items = state.results,
                                key = { index, movie -> "${movie.imdbID}_$index" }
                            ) { _, movie ->
                                SearchResultItem(
                                    movie = movie,
                                    onLongClick = {
                                        selectedMovie = movie
                                        showContextMenu = true
                                    },
                                    onClick = {
                                        onIntent(SearchIntent.SelectMovie(movie))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showContextMenu && selectedMovie != null) {
        AlertDialog(
            onDismissRequest = { showContextMenu = false },
            title = { Text(selectedMovie!!.title) },
            text = {
                Column {
                    Text("Год: ${selectedMovie!!.year}")
                    Text("Жанр: ${selectedMovie!!.genre ?: "Не указан"}")
                    Text("IMDb ID: ${selectedMovie!!.imdbID}")
                    Spacer(modifier = Modifier.Companion.height(8.dp))
                    Text("Выберите действие:")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onIntent(SearchIntent.SelectMovie(selectedMovie!!))
                        showContextMenu = false
                    }
                ) {
                    Text("Редактировать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showContextMenu = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun SearchResultItem(
    movie: Movie,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Text(
                    text = movie.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Companion.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.Companion.weight(1f)
                )

                IconButton(onClick = onLongClick) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Детали",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.Companion.height(8.dp))

            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.Companion.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.Companion.width(4.dp))
                    Text(
                        text = movie.year,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.Companion.padding(start = 8.dp)
                ) {
                    Text(
                        text = movie.genre ?: "Драма",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.Companion.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.Companion.height(8.dp))

            Text(
                text = "IMDb: ${movie.imdbID}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}