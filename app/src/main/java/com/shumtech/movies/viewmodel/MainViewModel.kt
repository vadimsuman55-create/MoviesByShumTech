package com.shumtech.movies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shumtech.movies.model.Movie
import com.shumtech.movies.model.MovieRepository
import com.shumtech.movies.ui.*
import com.shumtech.movies.ui.theme.AddUiState
import com.shumtech.movies.ui.theme.MainUiState
import com.shumtech.movies.ui.theme.SearchUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _mainUiState = MutableStateFlow(MainUiState())
    val mainUiState: StateFlow<MainUiState> = _mainUiState.asStateFlow()

    private val _addUiState = MutableStateFlow(AddUiState())
    val addUiState: StateFlow<AddUiState> = _addUiState.asStateFlow()

    private val _searchUiState = MutableStateFlow(SearchUiState())
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    private val _currentScreen = MutableStateFlow(Screen.MAIN)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Навигация
    enum class Screen { MAIN, ADD, SEARCH }

    fun navigateToAdd(movie: Movie? = null) {
        _addUiState.update {
            it.copy(
                isEditMode = movie != null,
                editingMovie = movie,
                title = movie?.title ?: "",
                year = movie?.year ?: "",
                posterUrl = movie?.posterUrl ?: ""
            )
        }
        _currentScreen.value = Screen.ADD
    }

    fun navigateToSearch() {
        _currentScreen.value = Screen.SEARCH
    }

    fun navigateBack() {
        when (_currentScreen.value) {
            Screen.SEARCH -> _currentScreen.value = Screen.ADD
            Screen.ADD -> {
                _currentScreen.value = Screen.MAIN
                _addUiState.update { AddUiState() } // сброс
            }
            else -> _currentScreen.value = Screen.MAIN
        }
    }

    // --- Данные главного экрана ---
    init {
        viewModelScope.launch {
            repository.allMovies.collect { movies ->
                val selected = movies.count { it.isSelected }
                _mainUiState.update {
                    it.copy(movies = movies, selectedCount = selected)
                }
            }
        }
    }

    fun toggleMovieSelection(movie: Movie) {
        viewModelScope.launch {
            repository.updateMovie(movie.copy(isSelected = !movie.isSelected))
        }
    }

    fun deleteSelectedMovies() {
        viewModelScope.launch {
            repository.deleteSelectedMovies()
        }
    }

    // --- Добавление/редактирование ---
    fun updateAddFields(title: String, year: String, posterUrl: String) {
        _addUiState.update { it.copy(title = title, year = year, posterUrl = posterUrl) }
    }

    fun addMovie() {
        val state = _addUiState.value
        if (state.title.isNotBlank()) {
            val movie = if (state.isEditMode && state.editingMovie != null) {
                state.editingMovie!!.copy(
                    title = state.title,
                    year = state.year,
                    posterUrl = state.posterUrl
                )
            } else {
                Movie(
                    title = state.title,
                    year = state.year,
                    posterUrl = state.posterUrl,
                    imdbID = "",
                    isSelected = false
                )
            }
            viewModelScope.launch {
                repository.insertMovie(movie)
                navigateBack()
            }
        }
    }

    // --- Поиск ---
    fun updateSearchQuery(query: String) {
        _searchUiState.update { it.copy(query = query) }
    }

    fun performSearch() {
        val query = _searchUiState.value.query
        if (query.isBlank()) return
        viewModelScope.launch {
            _searchUiState.update { it.copy(isLoading = true, error = null) }
            try {
                val results = repository.searchMovies(query)
                _searchUiState.update { it.copy(results = results, isLoading = false) }
                if (results.isEmpty()) {
                    _searchUiState.update { it.copy(error = "Фильмы не найдены") }
                }
            } catch (e: Exception) {
                _searchUiState.update { it.copy(error = "Ошибка: ${e.message}", isLoading = false) }
            }
        }
    }

    fun clearSearchResults() {
        _searchUiState.update { it.copy(results = emptyList(), error = null, query = "") }
    }

    fun selectSearchResult(movie: Movie) {
        navigateToAdd(movie)
        clearSearchResults()
    }

    // --- Очистка ресурсов ---
    override fun onCleared() {
        super.onCleared()
    }
}