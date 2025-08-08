package com.myimbd.app.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myimbd.app.base.BaseViewModel
import com.myimbd.app.ui.main.adapter.ViewType
import com.myimbd.domain.model.MovieDomainEntity
import com.myimbd.domain.usecase.GetMovieListUseCase
import com.myimbd.domain.usecase.GetMoviesByGenreUseCase
import com.myimbd.domain.usecase.SearchMoviesUseCase
import com.myimbd.domain.usecase.ToggleWishlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getMoviesUseCase: GetMovieListUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val getMoviesByGenreUseCase: GetMoviesByGenreUseCase,
    private val toggleWishlistUseCase: ToggleWishlistUseCase
) : BaseViewModel() {

    private val _movies = MutableLiveData<List<MovieDomainEntity>>()
    val movies: LiveData<List<MovieDomainEntity>> = _movies

    private val _currentViewType = MutableLiveData(ViewType.LIST)
    val currentViewType: LiveData<ViewType> = _currentViewType

    private val _wishlistCount = MutableLiveData<Int>()
    val wishlistCount: LiveData<Int> = _wishlistCount

    private val _hasMoreData = MutableLiveData<Boolean>()
    val hasMoreData: LiveData<Boolean> = _hasMoreData

    private val allMovies = mutableListOf<MovieDomainEntity>()
    private var currentPage = 1
    private var isLoadingMore = false
    private val pageSize = 20 // Initial load 20 items
    private val paginationThreshold = 18 // Load more when user reaches item 18

    private var currentSearchQuery: String = ""
    private var currentGenreFilter: String? = null
    private var searchJob: Job? = null

    fun toggleViewType() {
        _currentViewType.value = if (_currentViewType.value == ViewType.LIST) {
            ViewType.GRID
        } else {
            ViewType.LIST
        }
    }

    fun isLoading(): Boolean = isLoadingMore

    fun loadMovies(isRefresh: Boolean = false) {
        if (isLoadingMore) return

        if (isRefresh) {
            currentPage = 1
            allMovies.clear()
        }

        viewModelScope.launch {
            try {
                if (currentPage == 1) {
                    setLoading(true)
                }
                isLoadingMore = true
                setError(null)

                val newMovies = if (currentSearchQuery.isNotEmpty()) {
                    searchMoviesUseCase(currentSearchQuery, currentPage, pageSize)
                } else if (currentGenreFilter != null) {
                    getMoviesByGenreUseCase(currentGenreFilter, currentPage, pageSize)
                } else {
                    getMoviesUseCase(currentPage, pageSize)
                }

                if (newMovies.isNotEmpty()) {
                    allMovies.addAll(newMovies)
                    currentPage++
                    _hasMoreData.value = newMovies.size >= pageSize
                } else {
                    _hasMoreData.value = false
                }

                _movies.postValue(allMovies.toList())
                updateWishlistCount()
            } catch (e: Exception) {
                setError(e.message ?: "Unknown error")
            } finally {
                setLoading(false)
                isLoadingMore = false
            }
        }
    }

    fun searchMovies(query: String) {
        // Cancel previous search job
        searchJob?.cancel()
        
        searchJob = viewModelScope.launch {
            // Debounce search for 500ms
            delay(500)
            
            currentSearchQuery = query.trim()
            currentPage = 1
            allMovies.clear()
            
            if (currentSearchQuery.isEmpty()) {
                // Load all movies when search is empty
                loadMovies()
            } else {
                // Perform search
                try {
                    setLoading(true)
                    isLoadingMore = true
                    setError(null)

                    val searchResults = searchMoviesUseCase(currentSearchQuery, currentPage, pageSize)
                    
                    if (searchResults.isNotEmpty()) {
                        allMovies.addAll(searchResults)
                        currentPage++
                        _hasMoreData.value = searchResults.size >= pageSize
                    } else {
                        _hasMoreData.value = false
                    }

                    _movies.postValue(allMovies.toList())
                    updateWishlistCount()
                } catch (e: Exception) {
                    setError(e.message ?: "Search failed")
                } finally {
                    setLoading(false)
                    isLoadingMore = false
                }
            }
        }
    }

    fun filterByGenre(genre: String?) {
        currentGenreFilter = genre
        currentPage = 1
        allMovies.clear()
        loadMovies()
    }

    fun toggleWishlist(movie: MovieDomainEntity) {
        viewModelScope.launch {
            try {
                toggleWishlistUseCase(movie.id, movie.isWishlisted)
                // Update the movie in the list
                val index = allMovies.indexOfFirst { it.id == movie.id }
                if (index != -1) {
                    allMovies[index] = movie.copy(isWishlisted = !movie.isWishlisted)
                    _movies.postValue(allMovies.toList())
                    updateWishlistCount()
                }
            } catch (e: Exception) {
                setError(e.message ?: "Failed to update wishlist")
            }
        }
    }

    private fun updateWishlistCount() {
        val count = allMovies.count { it.isWishlisted }
        _wishlistCount.postValue(count)
    }

    fun refreshMovies() {
        currentPage = 1
        allMovies.clear()
        loadMovies(true)
    }

    fun loadMoreMovies() {
        if (!isLoadingMore && _hasMoreData.value == true) {
            loadMovies()
        }
    }

    fun shouldLoadMore(position: Int): Boolean {
        return !isLoadingMore && 
               _hasMoreData.value == true && 
               position >= paginationThreshold &&
               position >= allMovies.size - 2 // Load when 2 items away from end
    }

    fun clearSearch() {
        currentSearchQuery = ""
        currentPage = 1
        allMovies.clear()
        loadMovies()
    }
}
