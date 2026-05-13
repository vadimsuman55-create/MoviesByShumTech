package com.shumtech.movies.domain.usecase

import com.shumtech.movies.domain.MovieRepository

class GetMovieTrailerUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(tmdbId: Int): String? {
        return repository.getMovieTrailer(tmdbId)
    }
}