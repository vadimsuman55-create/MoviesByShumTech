package com.shumtech.movies.model

data class MovieDetailsResponse(
    val Title: String,
    val Year: String,
    val Genre: String,
    val Poster: String,
    val imdbID: String,
    val Response: String,
    val Error: String?
)
