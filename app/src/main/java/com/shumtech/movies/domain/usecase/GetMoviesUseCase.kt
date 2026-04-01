package com.shumtech.movies.domain.usecase

import com.shumtech.movies.domain.MovieRepository
import com.shumtech.movies.model.Movie
import kotlinx.coroutines.flow.Flow

class GetMoviesUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(): Flow<List<Movie>> = repository.getAllMovies()
}