package com.shumtech.movies.model

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ru-RU"
    ): TmdbVideoResponse
}

data class TmdbVideoResponse(
    val results: List<TmdbVideo>
)

data class TmdbVideo(
    val id: String,
    val key: String,        // YouTube video ID
    val name: String,
    val site: String,       // "YouTube"
    val type: String,       // "Trailer", "Teaser" и т.д.
    val official: Boolean
)