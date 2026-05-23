package com.shumtech.movies.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shumtech.movies.data.MovieRepositoryImpl
import com.shumtech.movies.domain.usecase.*
import com.shumtech.movies.model.MovieDatabase
import com.shumtech.movies.model.RetrofitClient
import com.shumtech.movies.model.TmdbRetrofitClient
import com.shumtech.movies.presentation.mvi.MainIntent
import com.shumtech.movies.presentation.theme.MoviesByShumTechTheme
import com.shumtech.movies.presentation.view.AddScreen
import com.shumtech.movies.presentation.view.MainScreen
import com.shumtech.movies.presentation.view.SearchScreen
import com.shumtech.movies.presentation.view.TrailerScreen
import com.shumtech.movies.presentation.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MoviesByShumTechTheme {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(applicationContext)
                )

                // Состояния экранов
                val currentScreen by viewModel.currentScreen.collectAsState()
                val mainState by viewModel.mainState.collectAsState()
                val addState by viewModel.addState.collectAsState()
                val searchState by viewModel.searchState.collectAsState()

                when (currentScreen) {
                    MainViewModel.Screen.MAIN -> MainScreen(
                        state = mainState,
                        onIntent = { viewModel.processIntent(it) }
                    )

                    MainViewModel.Screen.ADD -> AddScreen(
                        state = addState,
                        onIntent = { viewModel.processIntent(it) }
                    )

                    MainViewModel.Screen.SEARCH -> SearchScreen(
                        state = searchState,
                        onIntent = { viewModel.processIntent(it) }
                    )

                    MainViewModel.Screen.TRAILER -> {
                        val videoId = mainState.trailerVideoId
                        if (videoId != null) {
                            TrailerScreen(
                                youtubeVideoId = videoId,
                                movieTitle = mainState.movieDetails?.title,
                                movieOverview = mainState.movieDetails?.overview,
                                onBack = { viewModel.processIntent(MainIntent.NavigateBack) }
                            )
                        }
                    }
                }
            }
        }
    }
}

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            // Инициализация базы данных
            val database = MovieDatabase.getDatabase(context)
            val movieDao = database.movieDao()

            // Инициализация API клиентов
            val api = RetrofitClient.instance              // OMDb API
            val tmdbApi = TmdbRetrofitClient.instance      // TMDB API

            // Создание репозитория
            val repository = MovieRepositoryImpl(
                movieDao = movieDao,
                api = api,
                tmdbApi = tmdbApi
            )

            // Создание UseCase
            val getMoviesUseCase = GetMoviesUseCase(repository)
            val addMovieUseCase = AddMovieUseCase(repository)
            val toggleSelectionUseCase = ToggleMovieSelectionUseCase(repository)
            val deleteSelectedUseCase = DeleteSelectedMoviesUseCase(repository)
            val searchMoviesUseCase = SearchMoviesUseCase(repository)
            val getMovieByIdUseCase = GetMovieByIdUseCase(repository)
            val updateMovieUseCase = UpdateMovieUseCase(repository)
            val getMovieTrailerUseCase = GetMovieTrailerUseCase(repository)
            val getMovieDetailsUseCase = GetMovieDetailsUseCase(repository)

            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                getMoviesUseCase = getMoviesUseCase,
                addMovieUseCase = addMovieUseCase,
                toggleSelectionUseCase = toggleSelectionUseCase,
                deleteSelectedUseCase = deleteSelectedUseCase,
                searchMoviesUseCase = searchMoviesUseCase,
                getMovieByIdUseCase = getMovieByIdUseCase,
                updateMovieUseCase = updateMovieUseCase,
                getMovieTrailerUseCase = getMovieTrailerUseCase,
                getMovieDetailsUseCase = getMovieDetailsUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}