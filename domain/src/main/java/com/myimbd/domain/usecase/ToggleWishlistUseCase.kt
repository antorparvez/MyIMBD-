package com.myimbd.domain.usecase

import com.myimbd.domain.repository.MovieRepository
import javax.inject.Inject

class ToggleWishlistUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int, isWishlisted: Boolean) {
        if (isWishlisted) {
            repository.removeFromWishlist(movieId)
        } else {
            repository.addToWishlist(movieId)
        }
    }
} 