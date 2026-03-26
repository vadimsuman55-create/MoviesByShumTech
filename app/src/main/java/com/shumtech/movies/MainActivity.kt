package com.shumtech.movies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shumtech.movies.ui.theme.MoviesByShumTechTheme
import com.shumtech.movies.viewmodel.MainViewModel
import com.shumtech.movies.view.*
import android.content.Context
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
                }
            }
        }
    }
}

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