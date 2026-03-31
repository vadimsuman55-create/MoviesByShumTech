package com.shumtech.movies.domain

import com.shumtech.movies.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getAllMovies(): Flow<List<Movie>>
    suspend fun insertMovie(movie: Movie)
    suspend fun updateMovie(movie: Movie)
    suspend fun deleteSelectedMovies()
    suspend fun getMovieById(id: Int): Movie?
    suspend fun searchMovies(query: String): List<Movie>
}