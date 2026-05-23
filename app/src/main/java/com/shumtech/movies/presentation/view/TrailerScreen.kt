package com.shumtech.movies.presentation.view

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailerScreen(
    youtubeVideoId: String,
    movieTitle: String? = null,
    movieOverview: String? = null,
    posterUrl: String? = null,
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
                            Icons.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!posterUrl.isNullOrBlank() && posterUrl != "N/A") {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(posterUrl)
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = "Poster",
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎬", fontSize = 64.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "ID видео: $youtubeVideoId",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

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

private fun openYouTubeVideo(context: android.content.Context, videoId: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://www.youtube.com/watch?v=$videoId")
        setPackage("com.google.android.youtube")
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        val browserIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://www.youtube.com/watch?v=$videoId")
        }
        context.startActivity(browserIntent)
        Toast.makeText(
            context,
            "YouTube не найден",
            Toast.LENGTH_SHORT
        ).show()
    }
}