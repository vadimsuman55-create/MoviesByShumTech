package com.shumtech.movies.domain.usecase

import com.shumtech.movies.domain.MovieRepository
import com.shumtech.movies.model.Movie

class UpdateMovieUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movie: Movie) {
        repository.updateMovie(movie)
    }
}