package com.myimbd.app.ui.main.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myimbd.domain.model.MovieDomainEntity
import com.myimbd.domain.usecase.GetMovieListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getMoviesUseCase: GetMovieListUseCase
) : ViewModel() {

    private val _movies = MutableLiveData<List<MovieDomainEntity>>()
    val movies: LiveData<List<MovieDomainEntity>> = _movies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentPage = 1
    private var isLoadingMore = false



    fun loadMovies() {
        if (isLoadingMore) {
            println("loadMovies: Already loading more, skipping call.")
            return
        }

        viewModelScope.launch {
            try {
                println("loadMovies: Starting to load movies for page $currentPage")
                _isLoading.value = true
                _error.value = null
                isLoadingMore = true

                val result = getMoviesUseCase.invoke()
                println("loadMovies: Received ${result.size} movies")

                result.let { moviesList ->
                    val updatedList = _movies.value.orEmpty() + moviesList
                    _movies.value = updatedList
                    println("loadMovies: Updated movies list size = ${updatedList.size}")
                    currentPage++
                }

            } catch (e: Exception) {
                println("loadMovies: Error occurred - ${e.message}")
                _error.value = e.message ?: "Unknown error"
            } finally {
                println("loadMovies: Finished loading movies")
                _isLoading.value = false
                isLoadingMore = false
            }
        }
    }

}

