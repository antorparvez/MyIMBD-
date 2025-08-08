package com.myimbd.app.ui.wishlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myimbd.app.base.BaseViewModel
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
) : BaseViewModel() {

    private val _wishlistedMovies = MutableLiveData<List<MovieDomainEntity>>()
    val wishlistedMovies: LiveData<List<MovieDomainEntity>> = _wishlistedMovies

    fun loadWishlistedMovies() {
        viewModelScope.launch {
            try {
                setLoading(true)
                setError(null)
                val movies = getWishlistedMoviesUseCase()
                _wishlistedMovies.value = movies
            } catch (e: Exception) {
                setError(e.message ?: "Failed to load wishlist")
            } finally {
                setLoading(false)
            }
        }
    }

    fun removeFromWishlist(movieId: Int) {
        viewModelScope.launch {
            try {
                toggleWishlistUseCase(movieId, true)
                loadWishlistedMovies() // Reload the list
            } catch (e: Exception) {
                setError(e.message ?: "Failed to remove from wishlist")
            }
        }
    }
} 