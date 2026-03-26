package com.shumtech.movies.mvi

import com.shumtech.movies.model.Movie

sealed class MainIntent {
    object LoadMovies : MainIntent()
    data class ToggleSelection(val movie: Movie) : MainIntent()
    object DeleteSelected : MainIntent()
    object AddMovieClicked : MainIntent()
}