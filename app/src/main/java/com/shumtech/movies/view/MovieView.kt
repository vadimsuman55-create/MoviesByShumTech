package com.shumtech.movies.view

import com.shumtech.movies.model.Movie

interface MovieView {
    fun showMovies(movies: List<Movie>)
    fun showSearchResults(results: List<Movie>)
    fun showLoading(isLoading: Boolean)
    fun showError(message: String?)
    fun updateSelectedCount(count: Int)
    fun navigateToAdd(movie: Movie?)
    fun navigateToSearch()
    fun navigateBack()
}