package com.myimbd.app.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myimbd.app.base.BaseViewModel
import com.myimbd.app.ui.main.adapter.ViewType
import com.myimbd.domain.model.MovieDomainEntity
import com.myimbd.domain.usecase.GetMovieListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getMoviesUseCase: GetMovieListUseCase
) : BaseViewModel() {

    private val _movies = MutableLiveData<List<MovieDomainEntity>>()
    val movies: LiveData<List<MovieDomainEntity>> = _movies

    private val _currentViewType = MutableLiveData(ViewType.LIST)
    val currentViewType: LiveData<ViewType> = _currentViewType

    private val allMovies = mutableListOf<MovieDomainEntity>()
    private var currentPage = 1
    private var isLoadingMore = false

    private var currentSearchQuery: String = ""
    private var currentFilters: Set<String> = emptySet()

    fun toggleViewType() {
        _currentViewType.value = if (_currentViewType.value == ViewType.LIST) {
            ViewType.GRID
        } else {
            ViewType.LIST
        }
    }

    fun isLoading(): Boolean = isLoadingMore

    fun loadMovies() {
        if (isLoadingMore) return

        viewModelScope.launch {
            try {
                setLoading(true)
                isLoadingMore = true
                setError(null)

                val newMovies = getMoviesUseCase()

                // Prevent duplicates
                val uniqueMovies = newMovies.filterNot { fetched ->
                    allMovies.any { it.id == fetched.id }
                }

                if (uniqueMovies.isNotEmpty()) {
                    allMovies.addAll(uniqueMovies)
                    currentPage++
                }

                applyCurrentFiltersAndSearch()
            } catch (e: Exception) {
                setError(e.message ?: "Unknown error")
            } finally {
                setLoading(false)
                isLoadingMore = false
            }
        }
    }

    fun filterMovies(query: String) {
        currentSearchQuery = query
        applyCurrentFiltersAndSearch()
    }

    fun applyFilters(filters: Set<String>) {
        currentFilters = filters
        applyCurrentFiltersAndSearch()
    }

    private fun applyCurrentFiltersAndSearch() {
        val filteredList = allMovies.filter { movie ->
            val matchesSearch = movie.title.contains(currentSearchQuery, ignoreCase = true)
            val matchesFilter =
                currentFilters.isEmpty() || currentFilters.contains(movie.title) // replace with movie.type
            matchesSearch && matchesFilter
        }

        _movies.postValue(filteredList)
    }

    fun toggleWishlist(movie: MovieDomainEntity) {
        val index = allMovies.indexOfFirst { it.id == movie.id }
        if (index != -1) {
            val updatedMovie = allMovies[index].copy(isWishlisted = !movie.isWishlisted)
            allMovies[index] = updatedMovie
            applyCurrentFiltersAndSearch()
        }
    }
}
