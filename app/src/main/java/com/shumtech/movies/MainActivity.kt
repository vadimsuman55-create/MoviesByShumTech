package com.shumtech.movies

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shumtech.movies.viewmodel.MainViewModel
import com.shumtech.movies.ui.theme.MoviesByShumTechTheme
import com.shumtech.movies.view.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shumtech.movies.model.MovieRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MoviesByShumTechTheme {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(applicationContext)
                )
                val currentScreen by viewModel.currentScreen.collectAsState()
                val mainState by viewModel.mainUiState.collectAsState()
                val addState by viewModel.addUiState.collectAsState()
                val searchState by viewModel.searchUiState.collectAsState()

                when (currentScreen) {
                    MainViewModel.Screen.MAIN -> MainScreen(
                        movies = mainState.movies,
                        selectedCount = mainState.selectedCount,
                        onAddClick = { viewModel.navigateToAdd() },
                        onMovieToggle = { viewModel.toggleMovieSelection(it) },
                        onDeleteSelected = { viewModel.deleteSelectedMovies() }
                    )
                    MainViewModel.Screen.ADD -> AddScreen(
                        title = addState.title,
                        year = addState.year,
                        posterUrl = addState.posterUrl,
                        isEditMode = addState.isEditMode,
                        onTitleChange = { viewModel.updateAddFields(it, addState.year, addState.posterUrl) },
                        onYearChange = { viewModel.updateAddFields(addState.title, it, addState.posterUrl) },
                        onPosterChange = { viewModel.updateAddFields(addState.title, addState.year, it) },
                        onBack = { viewModel.navigateBack() },
                        onOpenSearch = { viewModel.navigateToSearch() },
                        onAddMovie = { viewModel.addMovie() }
                    )
                    MainViewModel.Screen.SEARCH -> SearchScreen(
                        searchResults = searchState.results,
                        isLoading = searchState.isLoading,
                        errorMessage = searchState.error,
                        searchQuery = searchState.query,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onSearch = { viewModel.performSearch() },
                        onBack = { viewModel.navigateBack() },
                        onMovieSelected = { viewModel.selectSearchResult(it) },
                        onClearResults = { viewModel.clearSearchResults() }
                    )
                }
            }
        }
    }
}

// Фабрика для передачи репозитория
class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            val repository = MovieRepository(context.applicationContext)
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}