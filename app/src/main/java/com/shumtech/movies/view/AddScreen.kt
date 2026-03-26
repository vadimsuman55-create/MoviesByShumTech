package com.shumtech.movies.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.shumtech.movies.mvi.AddIntent
import com.shumtech.movies.mvi.AddState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    state: AddState,
    onIntent: (AddIntent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Редактировать фильм" else "Добавить фильм") },
                navigationIcon = {
                    IconButton(onClick = { onIntent(AddIntent.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { onIntent(AddIntent.OpenSearch) }) {
                        Icon(Icons.Default.Search, contentDescription = "Поиск фильмов")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Постер
            Card(
                modifier = Modifier
                    .size(200.dp, 250.dp)
                    .padding(bottom = 24.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (state.posterUrl.isNotBlank() && state.posterUrl != "N/A") {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(state.posterUrl)
                                .crossfade(true)
                                .scale(Scale.FILL)
                                .build()
                        ),
                        contentDescription = "Постер фильма",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🎬", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Нет постера",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = state.title,
                onValueChange = { onIntent(AddIntent.UpdateTitle(it)) },
                label = { Text("Название фильма") },
                placeholder = { Text("Введите название") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.title.isBlank(),
                supportingText = {
                    if (state.title.isBlank()) {
                        Text("Обязательное поле", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.year,
                onValueChange = { onIntent(AddIntent.UpdateYear(it)) },
                label = { Text("Год выпуска") },
                placeholder = { Text("Например: 2024") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onIntent(AddIntent.SaveMovie) },
                enabled = state.title.isNotBlank() && !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = if (state.isEditMode) "СОХРАНИТЬ" else "ДОБАВИТЬ ФИЛЬМ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!state.isEditMode) {
                Text(
                    text = "Или нажмите на иконку поиска 🔍 чтобы найти фильм",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}