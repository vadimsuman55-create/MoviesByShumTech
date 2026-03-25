package com.shumtech.movies.ui.theme

import com.shumtech.movies.model.Movie

data class MainUiState(
    val movies: List<Movie> = emptyList(),
    val selectedCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class AddUiState(
    val title: String = "",
    val year: String = "",
    val posterUrl: String = "",
    val isEditMode: Boolean = false,
    val editingMovie: Movie? = null
)

data class SearchUiState(
    val query: String = "",
    val results: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)