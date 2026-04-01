package com.shumtech.movies.domain.usecase

import com.shumtech.movies.domain.MovieRepository
import com.shumtech.movies.model.Movie

class SearchMoviesUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(query: String): List<Movie> {
        return repository.searchMovies(query)
    }
}