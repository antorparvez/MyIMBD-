package com.myimbd.app.ui.main.viewmodel

import androidx.lifecycle.viewModelScope
import com.myimbd.app.base.BaseViewModel
import com.myimbd.domain.model.MovieDomainEntity
import com.myimbd.domain.usecase.GetMovieListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getMoviesUseCase: GetMovieListUseCase
) : BaseViewModel() {

    private val _movies = androidx.lifecycle.MutableLiveData<List<MovieDomainEntity>>()
    val movies: androidx.lifecycle.LiveData<List<MovieDomainEntity>> = _movies

    private var currentPage = 1
    private var isLoadingMore = false

    fun loadMovies() {
        if (isLoadingMore) return

        viewModelScope.launch {
            try {
                setLoading(true)
                setError(null)
                isLoadingMore = true

                val result = getMoviesUseCase.invoke()
                val updatedList = _movies.value.orEmpty() + result
                _movies.value = updatedList
                currentPage++
            } catch (e: Exception) {
                setError(e.message ?: "Unknown error")
            } finally {
                setLoading(false)
                isLoadingMore = false
            }
        }
    }
}
