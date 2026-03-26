package com.shumtech.movies.mvi

import com.shumtech.movies.model.Movie

data class SearchState(
    val query: String = "",
    val results: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)