package com.shumtech.movies.mvi

import com.shumtech.movies.model.Movie

sealed class SearchIntent {
    data class UpdateQuery(val query: String) : SearchIntent()
    object PerformSearch : SearchIntent()
    data class SelectMovie(val movie: Movie) : SearchIntent()
    object ClearResults : SearchIntent()
    object NavigateBack : SearchIntent()
}