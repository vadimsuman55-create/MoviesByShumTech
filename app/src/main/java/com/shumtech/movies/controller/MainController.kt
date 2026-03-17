package com.shumtech.movies.controller

import kotlinx.coroutines.*
import com.shumtech.movies.model.Movie
import com.shumtech.movies.model.MovieRepository
import com.shumtech.movies.view.MovieView

class MainController(
    private val repository: MovieRepository,
    private val view: MovieView
) {
    private var movies: List<Movie> = emptyList()
    private var searchResults: List<Movie> = emptyList()
    private var isLoading = false
    private var errorMessage: String? = null
    private var selectedCount = 0

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    fun loadMoviesFromDb() {
        coroutineScope.launch {
            repository.allMovies.collect { moviesList ->
                this@MainController.movies = moviesList
                selectedCount = moviesList.count { it.isSelected }
                view.showMovies(moviesList)
                view.updateSelectedCount(selectedCount)
            }
        }
    }

    fun searchMovies(query: String) {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            view.showLoading(true)

            try {
                val results = repository.searchMovies(query)
                searchResults = results
                view.showSearchResults(results)
                if (results.isEmpty()) {
                    errorMessage = "Фильмы не найдены"
                    view.showError(errorMessage)
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка: ${e.message}"
                view.showError(errorMessage)
            } finally {
                isLoading = false
                view.showLoading(false)
            }
        }
    }

    fun addMovie(movie: Movie) {
        coroutineScope.launch {
            repository.insertMovie(movie)
            loadMoviesFromDb()
        }
    }

    fun toggleMovieSelection(movie: Movie) {
        coroutineScope.launch {
            repository.updateMovie(movie.copy(isSelected = !movie.isSelected))
            loadMoviesFromDb()
        }
    }

    fun deleteSelectedMovies() {
        coroutineScope.launch {
            repository.deleteSelectedMovies()
            loadMoviesFromDb()
        }
    }

    fun clearAllSelections() {
        coroutineScope.launch {
            repository.clearAllSelections()
            loadMoviesFromDb()
        }
    }

    fun clearSearchResults() {
        searchResults = emptyList()
        errorMessage = null
        view.showSearchResults(emptyList())
        view.showError(null)
    }

    fun onAddClicked() {
        view.navigateToAdd(null)
    }

    fun onSearchClicked() {
        view.navigateToSearch()
    }

    fun onBackClicked() {
        view.navigateBack()
    }

    fun onMovieSelectedForEdit(movie: Movie) {
        view.navigateToAdd(movie)
    }

    fun onDestroy() {
        coroutineScope.cancel()
    }
}