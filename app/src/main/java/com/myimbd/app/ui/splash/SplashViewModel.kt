package com.myimbd.app.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myimbd.app.base.BaseViewModel
import com.myimbd.data.repository.MovieRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: MovieRepositoryImpl
) : BaseViewModel() {

    private val _isDataLoaded = MutableLiveData<Boolean>()
    val isDataLoaded: LiveData<Boolean> = _isDataLoaded

    fun checkAndLoadData() {
        viewModelScope.launch {
            try {
                setLoading(true)
                setError(null)

                // Check if we have local data
                val hasLocalData = repository.hasLocalData()
                
                if (!hasLocalData) {
                    // Fetch data from remote
                    repository.fetchAndSaveMoviesFromRemote()
                }
                
                _isDataLoaded.value = true
            } catch (e: Exception) {
                setError(e.message ?: "Unknown error occurred")
            } finally {
                setLoading(false)
            }
        }
    }
} 