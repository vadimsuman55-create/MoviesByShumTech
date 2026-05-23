package com.shumtech.movies.domain.usecase

import com.shumtech.movies.domain.MovieRepository
import com.shumtech.movies.model.TmdbMovieDetails

class GetMovieDetailsUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(tmdbId: Int): TmdbMovieDetails? {
        return repository.getMovieDetails(tmdbId)
    }
}