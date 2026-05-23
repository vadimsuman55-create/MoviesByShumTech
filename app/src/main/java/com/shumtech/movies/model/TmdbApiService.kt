package com.shumtech.movies.model

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    // Получение трейлеров фильма
    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ru-RU"
    ): TmdbVideoResponse

    // Получение подробной информации о фильме
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ru-RU"
    ): TmdbMovieDetails
}

// Ответы API: Трейлеры

data class TmdbVideoResponse(
    val results: List<TmdbVideo>
)

data class TmdbVideo(
    val id: String,
    val key: String,           // YouTube video ID (например: "wPiyAoTY8Ys")
    val name: String,          // Название трейлера
    val site: String,          // "YouTube"
    val type: String,          // "Trailer", "Teaser" и т.д.
    val official: Boolean,     // Официальный ли трейлер
    val published_at: String? = null
)

// Ответы API: Детали фильма

data class TmdbMovieDetails(
    val id: Int,
    val title: String,                    // Название фильма
    val overview: String,                 // Описание (сюжет)
    val poster_path: String?,             // Путь к постеру
    val backdrop_path: String?,           // Путь к фону
    val release_date: String,             // Дата выхода (формат: "YYYY-MM-DD")
    val vote_average: Double,             // Рейтинг (0.0 - 10.0)
    val vote_count: Int,                  // Количество голосов
    val runtime: Int?,                    // Длительность в минутах
    val genres: List<Genre>?,             // Жанры
    val original_language: String,        // Язык оригинала
    val status: String,                   // Статус (Released, etc.)
    val tagline: String?                  // Слоган
) {
    data class Genre(
        val id: Int,
        val name: String
    )
}