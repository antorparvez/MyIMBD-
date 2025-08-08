package com.myimbd.app.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myimbd.app.base.BaseViewModel
import com.myimbd.domain.model.MovieDomainEntity
import com.myimbd.domain.usecase.GetMovieByIdUseCase
import com.myimbd.domain.usecase.ToggleWishlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieByIdUseCase: GetMovieByIdUseCase,
    private val toggleWishlistUseCase: ToggleWishlistUseCase
) : BaseViewModel() {

    private val _movie = MutableLiveData<MovieDomainEntity?>()
    val movie: LiveData<MovieDomainEntity?> = _movie

    fun loadMovie(movieId: Int) {
        viewModelScope.launch {
            try {
                setLoading(true)
                setError(null)
                val movie = getMovieByIdUseCase(movieId)
                _movie.value = movie
            } catch (e: Exception) {
                setError(e.message ?: "Failed to load movie details")
            } finally {
                setLoading(false)
            }
        }
    }

    fun toggleWishlist(movieId: Int) {
        viewModelScope.launch {
            try {
                val currentMovie = _movie.value
                if (currentMovie != null) {
                    toggleWishlistUseCase(movieId, currentMovie.isWishlisted)
                    // Reload the movie to get updated wishlist status
                    loadMovie(movieId)
                }
            } catch (e: Exception) {
                setError(e.message ?: "Failed to update wishlist")
            }
        }
    }
} 