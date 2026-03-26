package com.shumtech.movies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shumtech.movies.ui.theme.MoviesByShumTechTheme
import com.shumtech.movies.viewmodel.MainViewModel
import com.shumtech.movies.viewmodel.MainViewModelFactory
import com.shumtech.movies.view.*

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