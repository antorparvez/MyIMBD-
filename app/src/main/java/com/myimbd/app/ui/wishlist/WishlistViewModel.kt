package com.myimbd.app.ui.wishlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myimbd.domain.model.MovieDomainEntity
import com.myimbd.domain.usecase.GetWishlistedMoviesUseCase
import com.myimbd.domain.usecase.ToggleWishlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val getWishlistedMoviesUseCase: GetWishlistedMoviesUseCase,
    private val toggleWishlistUseCase: ToggleWishlistUseCase
) : ViewModel() {

    private val _wishlistedMovies = MutableLiveData<List<MovieDomainEntity>>()
    val wishlistedMovies: LiveData<List<MovieDomainEntity>> = _wishlistedMovies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadWishlistedMovies() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val movies = getWishlistedMoviesUseCase()
                _wishlistedMovies.value = movies
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFromWishlist(movieId: Int) {
        viewModelScope.launch {
            try {
                toggleWishlistUseCase(movieId, true)
                loadWishlistedMovies() // Reload the list
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
} 