package com.myimbd.app.ui.splash.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for SplashActivity
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
   /* private val hasLocalDataUseCase: HasLocalDataUseCase,
    private val saveMoviesUseCase: SaveMoviesUseCase,
    private val movieRepositoryImpl: MovieRepositoryImpl*/
) : ViewModel() {
    
    private val _uiState = MutableLiveData<SplashUiState>(SplashUiState.Loading)
    val uiState: LiveData<SplashUiState> = _uiState
    
    fun checkDataAndLoadIfNeeded() {
        viewModelScope.launch {
            try {
                _uiState.value = SplashUiState.Loading
                
             /*   val hasData = hasLocalDataUseCase()
                
                if (!hasData) {
                    // Load data from remote
                    loadDataFromRemote()
                } else {
                    _uiState.value = SplashUiState.Success
                }*/
            } catch (e: Exception) {
                _uiState.value = SplashUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    private suspend fun loadDataFromRemote() {
        try {
           /* val movies = movieRepositoryImpl.fetchAndSaveMovies()
            if (movies.isNotEmpty()) {
                _uiState.value = SplashUiState.Success
            } else {
                _uiState.value = SplashUiState.Error("Failed to load movies")
            }*/
        } catch (e: Exception) {
            _uiState.value = SplashUiState.Error(e.message ?: "Network error")
        }
    }
}

sealed class SplashUiState {
    object Loading : SplashUiState()
    object Success : SplashUiState()
    data class Error(val message: String) : SplashUiState()
} 