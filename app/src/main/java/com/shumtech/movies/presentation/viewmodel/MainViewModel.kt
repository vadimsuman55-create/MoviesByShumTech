package com.shumtech.movies.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shumtech.movies.domain.usecase.AddMovieUseCase
import com.shumtech.movies.domain.usecase.DeleteSelectedMoviesUseCase
import com.shumtech.movies.domain.usecase.GetMovieByIdUseCase
import com.shumtech.movies.domain.usecase.GetMovieTrailerUseCase
import com.shumtech.movies.domain.usecase.GetMoviesUseCase
import com.shumtech.movies.domain.usecase.SearchMoviesUseCase
import com.shumtech.movies.domain.usecase.ToggleMovieSelectionUseCase
import com.shumtech.movies.domain.usecase.UpdateMovieUseCase
import com.shumtech.movies.model.Movie
import com.shumtech.movies.presentation.mvi.AddIntent
import com.shumtech.movies.presentation.mvi.AddState
import com.shumtech.movies.presentation.mvi.MainIntent
import com.shumtech.movies.presentation.mvi.MainState
import com.shumtech.movies.presentation.mvi.SearchIntent
import com.shumtech.movies.presentation.mvi.SearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val addMovieUseCase: AddMovieUseCase,
    private val toggleSelectionUseCase: ToggleMovieSelectionUseCase,
    private val deleteSelectedUseCase: DeleteSelectedMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val getMovieByIdUseCase: GetMovieByIdUseCase,
    private val updateMovieUseCase: UpdateMovieUseCase,
    private val getMovieTrailerUseCase: GetMovieTrailerUseCase
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

    enum class Screen { MAIN, ADD, SEARCH, TRAILER }

    init {
        viewModelScope.launch {
            getMoviesUseCase().collect { movies ->
                val selected = movies.count { it.isSelected }
                _mainState.update { it.copy(movies = movies, selectedCount = selected) }
            }
        }
    }

    fun processIntent(intent: Any) {
        when (intent) {
            is MainIntent.ToggleSelection -> toggleSelection(intent.movie)
            is MainIntent.DeleteSelected -> deleteSelected()
            is MainIntent.AddMovieClicked -> navigateToAdd()

            is AddIntent.UpdateTitle -> updateTitle(intent.title)
            is AddIntent.UpdateYear -> updateYear(intent.year)
            is AddIntent.UpdatePoster -> updatePoster(intent.posterUrl)
            is AddIntent.OpenSearch -> navigateToSearch()
            is AddIntent.SaveMovie -> saveMovie()
            is AddIntent.NavigateBack -> navigateBackFromAdd()

            is SearchIntent.UpdateQuery -> updateQuery(intent.query)
            is SearchIntent.PerformSearch -> performSearch()
            is SearchIntent.SelectMovie -> selectMovie(intent.movie)
            is SearchIntent.ClearResults -> clearResults()
            is SearchIntent.NavigateBack -> navigateBackFromSearch()

            is MainIntent.LoadTrailer -> {
                viewModelScope.launch {
                    _mainState.update { it.copy(trailerError = null) }
                    val videoId = getMovieTrailerUseCase(intent.tmdbId)
                    if (videoId != null) {
                        _mainState.update { it.copy(trailerVideoId = videoId) }
                        _currentScreen.value = Screen.TRAILER
                    } else {
                        _mainState.update { it.copy(trailerError = "Трейлер не найден") }
                    }
                }
            }
        }
    }

    // Главный экран
    private fun toggleSelection(movie: Movie) {
        viewModelScope.launch {
            toggleSelectionUseCase(movie)
        }
    }

    private fun deleteSelected() {
        viewModelScope.launch {
            deleteSelectedUseCase()
        }
    }

    // Экран добавления
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
                val existing = getMovieByIdUseCase(state.editingMovieId)
                existing?.let {
                    val updated = it.copy(
                        title = state.title,
                        year = state.year,
                        posterUrl = state.posterUrl
                    )
                    updateMovieUseCase(updated)
                }
            } else {
                val movie = Movie(
                    title = state.title,
                    year = state.year,
                    posterUrl = state.posterUrl,
                    imdbID = "",
                    isSelected = false
                )
                addMovieUseCase(movie)
            }
            _currentScreen.value = Screen.MAIN
            _addState.update { AddState() }
        }
    }

    // Экран поиска
    private fun updateQuery(query: String) {
        _searchState.update { it.copy(query = query) }
    }

    private fun performSearch() {
        val query = _searchState.value.query
        if (query.isBlank()) return
        viewModelScope.launch {
            _searchState.update { it.copy(isLoading = true, error = null) }
            try {
                val results = searchMoviesUseCase(query)
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
        _addState.update {
            it.copy(
                title = movie.title,
                year = movie.year,
                posterUrl = movie.posterUrl,
                isEditMode = false,
                editingMovieId = null
            )
        }
        _currentScreen.value = Screen.ADD
        clearResults()
    }

    private fun clearResults() {
        _searchState.update { it.copy(results = emptyList(), error = null, query = "") }
    }

    // Навигация
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
    }
}