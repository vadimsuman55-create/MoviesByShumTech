package com.shumtech.movies.presentation.mvi

import com.shumtech.movies.model.Movie

sealed class MainIntent {
    object LoadMovies : MainIntent()
    data class ToggleSelection(val movie: Movie) : MainIntent()
    object DeleteSelected : MainIntent()
    object AddMovieClicked : MainIntent()
    data class LoadTrailer(val tmdbId: Int) : MainIntent()
    object NavigateBack : MainIntent()
}