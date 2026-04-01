package com.shumtech.movies.presentation.mvi

data class AddState(
    val title: String = "",
    val year: String = "",
    val posterUrl: String = "",
    val isEditMode: Boolean = false,
    val editingMovieId: Int? = null,
    val isSaving: Boolean = false
)