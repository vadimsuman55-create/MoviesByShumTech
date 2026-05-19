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
import com.shumtech.movies.presentation.theme.MoviesByShumTechTheme
import com.shumtech.movies.presentation.viewmodel.MainViewModel
import com.shumtech.movies.presentation.view.AddScreen
import com.shumtech.movies.presentation.view.MainScreen
import com.shumtech.movies.presentation.view.SearchScreen
import com.shumtech.movies.presentation.view.TrailerScreen
import com.shumtech.movies.model.TmdbRetrofitClient
import com.shumtech.movies.model.TmdbApiService
import com.shumtech.movies.domain.usecase.GetMovieTrailerUseCase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MoviesByShumTechTheme {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(applicationContext)
                )
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
                        // Берем ID из состояния. Если его нет — возвращаемся назад.
                        val videoId = mainState.trailerVideoId
                        if (videoId != null) {
                            TrailerScreen(
                                youtubeVideoId = videoId,
                                onBack = {
                                    // При возврате сбрасываем состояние и уходим на главный
                                    viewModel.processIntent(MainIntent.NavigateBack) // или просто меняем Screen
                                }
                            )
                        } else {
                            // Если видео нет, возвращаемся на главный
                            // (Это страховка на случай, если экран открылся, а данные не подгрузились)
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
            val database = MovieDatabase.getDatabase(context)
            val movieDao = database.movieDao()
            val api = RetrofitClient.instance

            // Создаём TMDB API клиент
            val tmdbApi = TmdbRetrofitClient.instance

            // Передаём tmdbApi в репозиторий
            val repository = MovieRepositoryImpl(
                movieDao = movieDao,
                api = api,
                tmdbApi = tmdbApi
            )

            // Существующие UseCase
            val getMoviesUseCase = GetMoviesUseCase(repository)
            val addMovieUseCase = AddMovieUseCase(repository)
            val toggleSelectionUseCase = ToggleMovieSelectionUseCase(repository)
            val deleteSelectedUseCase = DeleteSelectedMoviesUseCase(repository)
            val searchMoviesUseCase = SearchMoviesUseCase(repository)
            val getMovieByIdUseCase = GetMovieByIdUseCase(repository)
            val updateMovieUseCase = UpdateMovieUseCase(repository)

            // Создаём UseCase для трейлера
            val getMovieTrailerUseCase = GetMovieTrailerUseCase(repository)

            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                getMoviesUseCase,
                addMovieUseCase,
                toggleSelectionUseCase,
                deleteSelectedUseCase,
                searchMoviesUseCase,
                getMovieByIdUseCase,
                updateMovieUseCase,
                getMovieTrailerUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}