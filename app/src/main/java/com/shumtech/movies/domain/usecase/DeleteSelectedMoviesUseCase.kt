package com.shumtech.movies.domain.usecase

import com.shumtech.movies.domain.MovieRepository

class DeleteSelectedMoviesUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke() {
        repository.deleteSelectedMovies()
    }
}