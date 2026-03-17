package com.shumtech.movies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.shumtech.movies.controller.MainController
import com.shumtech.movies.model.Movie
import com.shumtech.movies.model.MovieRepository
import com.shumtech.movies.view.AddScreen
import com.shumtech.movies.view.MainScreen
import com.shumtech.movies.view.MovieView
import com.shumtech.movies.view.SearchScreen

class MainActivity : ComponentActivity(), MovieView {

    private var movies by mutableStateOf(emptyList<Movie>())
    private var searchResults by mutableStateOf(emptyList<Movie>())
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private var selectedCount by mutableStateOf(0)

    private var currentScreen by mutableStateOf(Screen.MAIN)
    private var selectedMovieForEdit by mutableStateOf<Movie?>(null)

    private lateinit var controller: MainController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = MovieRepository(applicationContext)
        controller = MainController(repository, this)

        setContent {
            MaterialTheme {
                when (currentScreen) {
                    Screen.MAIN -> MainScreen(
                        movies = movies,
                        selectedCount = selectedCount,
                        onAddClick = { controller.onAddClicked() },
                        onMovieToggle = { controller.toggleMovieSelection(it) },
                        onDeleteSelected = { controller.deleteSelectedMovies() }
                    )
                    Screen.ADD -> AddScreen(
                        onBack = { controller.onBackClicked() },
                        onOpenSearch = { controller.onSearchClicked() },
                        onAddMovie = { movie ->
                            controller.addMovie(movie)
                            currentScreen = Screen.MAIN
                            selectedMovieForEdit = null
                        },
                        selectedMovie = selectedMovieForEdit
                    )
                    Screen.SEARCH -> SearchScreen(
                        searchResults = searchResults,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onSearch = { query -> controller.searchMovies(query) },
                        onBack = { controller.onBackClicked() },
                        onMovieSelected = { movie ->
                            selectedMovieForEdit = movie
                            currentScreen = Screen.ADD
                        },
                        onClearResults = { controller.clearSearchResults() }
                    )
                }
            }
        }

        controller.loadMoviesFromDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.onDestroy()
    }

    // Реализация MovieView
    override fun showMovies(movies: List<Movie>) { this.movies = movies }
    override fun showSearchResults(results: List<Movie>) { this.searchResults = results }
    override fun showLoading(isLoading: Boolean) { this.isLoading = isLoading }
    override fun showError(message: String?) { this.errorMessage = message }
    override fun updateSelectedCount(count: Int) { this.selectedCount = count }

    override fun navigateToAdd(movie: Movie?) {
        selectedMovieForEdit = movie
        currentScreen = Screen.ADD
    }

    override fun navigateToSearch() {
        currentScreen = Screen.SEARCH
    }

    override fun navigateBack() {
        when (currentScreen) {
            Screen.SEARCH -> currentScreen = Screen.ADD
            Screen.ADD -> {
                currentScreen = Screen.MAIN
                selectedMovieForEdit = null
            }
            else -> {
                currentScreen = Screen.MAIN
                selectedMovieForEdit = null
            }
        }
    }
}

enum class Screen { MAIN, ADD, SEARCH }