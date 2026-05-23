package com.shumtech.movies.presentation.view

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailerScreen(
    youtubeVideoId: String,
    movieTitle: String? = null,
    movieOverview: String? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Трейлер") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    // Кнопка "Открыть в YouTube" - используем иконку Share
                    IconButton(
                        onClick = {
                            openYouTubeVideo(context, youtubeVideoId)
                        }
                    ) {
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = "Открыть в YouTube",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Иконка Play (видео)
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Название фильма (если есть)
            if (!movieTitle.isNullOrBlank()) {
                Text(
                    text = movieTitle,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = "Трейлер",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Кнопка открытия
            Button(
                onClick = {
                    openYouTubeVideo(context, youtubeVideoId)
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Открыть в YouTube", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ID видео (для отладки / отчёта)
            Text(
                text = "ID видео: $youtubeVideoId",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            // Описание фильма (если есть)
            if (!movieOverview.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "О фильме",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = movieOverview,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

// Вспомогательная функция для открытия видео
private fun openYouTubeVideo(context: android.content.Context, videoId: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://www.youtube.com/watch?v=$videoId")
        setPackage("com.google.android.youtube") // Приоритет: приложение YouTube
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback: если приложения YouTube нет — открываем в браузере
        val browserIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://www.youtube.com/watch?v=$videoId")
        }
        context.startActivity(browserIntent)
        Toast.makeText(
            context,
            "YouTube не найден, открываем в браузере",
            Toast.LENGTH_SHORT
        ).show()
    }
}