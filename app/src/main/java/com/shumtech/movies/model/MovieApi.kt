package com.shumtech.movies.model

import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {
    @GET("/")
    suspend fun searchMovies(
        @Query("apikey") apiKey: String,
        @Query("s") searchQuery: String,
        @Query("type") type: String = "movie"
    ): MovieResponse

    @GET("/")
    suspend fun getMovieDetails(
        @Query("apikey") apiKey: String,
        @Query("i") imdbID: String,
        @Query("plot") plot: String = "short"
    ): MovieDetailsResponse
}