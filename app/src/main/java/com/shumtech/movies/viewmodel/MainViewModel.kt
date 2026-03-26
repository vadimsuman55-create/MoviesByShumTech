package com.shumtech.movies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shumtech.movies.model.Movie
import com.shumtech.movies.model.MovieRepository
import com.shumtech.movies.mvi.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    // Состояния
    private val _mainState = MutableStateFlow(MainState())
    val mainState: StateFlow<MainState> = _mainState.asStateFlow()

    private val _addState = MutableStateFlow(AddState())
    val addState: StateFlow<AddState> = _addState.asStateFlow()

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _currentScreen = MutableStateFlow(Screen.MAIN)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    enum class Screen { MAIN, ADD, SEARCH }

    init {
        loadMovies()
    }

    // Единый метод обработки интентов
    fun processIntent(intent: Any) {
        when (intent) {
            // Главный экран
            is MainIntent.LoadMovies -> loadMovies()
            is MainIntent.ToggleSelection -> toggleSelection(intent.movie)
            is MainIntent.DeleteSelected -> deleteSelected()
            is MainIntent.AddMovieClicked -> navigateToAdd()

            // Экран добавления
            is AddIntent.UpdateTitle -> updateTitle(intent.title)
            is AddIntent.UpdateYear -> updateYear(intent.year)
            is AddIntent.UpdatePoster -> updatePoster(intent.posterUrl)
            is AddIntent.OpenSearch -> navigateToSearch()
            is AddIntent.SaveMovie -> saveMovie()
            is AddIntent.NavigateBack -> navigateBackFromAdd()

            // Экран поиска
            is SearchIntent.UpdateQuery -> updateQuery(intent.query)
            is SearchIntent.PerformSearch -> performSearch()
            is SearchIntent.SelectMovie -> selectMovie(intent.movie)
            is SearchIntent.ClearResults -> clearResults()
            is SearchIntent.NavigateBack -> navigateBackFromSearch()
        }
    }

    // --- Навигация ---
    private fun navigateToAdd(movie: Movie? = null) {
        _addState.update {
            it.copy(
                isEditMode = movie != null,
                editingMovieId = movie?.id,
                title = movie?.title ?: "",
                year = movie?.year ?: "",
                posterUrl = movie?.posterUrl ?: ""
            )
        }
        _currentScreen.value = Screen.ADD
    }

    private fun navigateToSearch() {
        _currentScreen.value = Screen.SEARCH
    }

    private fun navigateBackFromAdd() {
        _currentScreen.value = Screen.MAIN
        _addState.update { AddState() }
    }

    private fun navigateBackFromSearch() {
        _currentScreen.value = Screen.ADD
        // Не сбрасываем результаты, чтобы вернуться к редактированию
    }

    // --- Главный экран ---
    private fun loadMovies() {
        viewModelScope.launch {
            repository.allMovies.collect { movies ->
                val selected = movies.count { it.isSelected }
                _mainState.update { it.copy(movies = movies, selectedCount = selected) }
            }
        }
    }

    private fun toggleSelection(movie: Movie) {
        viewModelScope.launch {
            repository.updateMovie(movie.copy(isSelected = !movie.isSelected))
        }
    }

    private fun deleteSelected() {
        viewModelScope.launch {
            repository.deleteSelectedMovies()
        }
    }

    // --- Экран добавления ---
    private fun updateTitle(title: String) {
        _addState.update { it.copy(title = title) }
    }

    private fun updateYear(year: String) {
        _addState.update { it.copy(year = year) }
    }

    private fun updatePoster(posterUrl: String) {
        _addState.update { it.copy(posterUrl = posterUrl) }
    }

    private fun saveMovie() {
        val state = _addState.value
        if (state.title.isBlank()) return

        viewModelScope.launch {
            if (state.isEditMode && state.editingMovieId != null) {
                val existing = repository.getMovieById(state.editingMovieId)
                existing?.let {
                    repository.updateMovie(
                        it.copy(
                            title = state.title,
                            year = state.year,
                            posterUrl = state.posterUrl
                        )
                    )
                }
            } else {
                val movie = Movie(
                    title = state.title,
                    year = state.year,
                    posterUrl = state.posterUrl,
                    imdbID = "",
                    isSelected = false
                )
                repository.insertMovie(movie)
            }
            _currentScreen.value = Screen.MAIN
            _addState.update { AddState() }
        }
    }

    // --- Экран поиска ---
    private fun updateQuery(query: String) {
        _searchState.update { it.copy(query = query) }
    }

    private fun performSearch() {
        val query = _searchState.value.query
        if (query.isBlank()) return
        viewModelScope.launch {
            _searchState.update { it.copy(isLoading = true, error = null) }
            try {
                val results = repository.searchMovies(query)
                _searchState.update { it.copy(results = results, isLoading = false) }
                if (results.isEmpty()) {
                    _searchState.update { it.copy(error = "Фильмы не найдены") }
                }
            } catch (e: Exception) {
                _searchState.update { it.copy(error = "Ошибка: ${e.message}", isLoading = false) }
            }
        }
    }

    private fun selectMovie(movie: Movie) {
        navigateToAdd(movie)
        clearResults()
    }

    private fun clearResults() {
        _searchState.update { it.copy(results = emptyList(), error = null, query = "") }
    }
}