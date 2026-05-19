package com.shumtech.movies.presentation.mvi

import com.shumtech.movies.model.Movie

data class MainState(
    val movies: List<Movie> = emptyList(),
    val selectedCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val trailerVideoId: String? = null,
    val trailerError: String? = null
)