package com.shumtech.movies.presentation.mvi

sealed class AddIntent {
    data class UpdateTitle(val title: String) : AddIntent()
    data class UpdateYear(val year: String) : AddIntent()
    data class UpdatePoster(val posterUrl: String) : AddIntent()
    object OpenSearch : AddIntent()
    object SaveMovie : AddIntent()
    object NavigateBack : AddIntent()
}