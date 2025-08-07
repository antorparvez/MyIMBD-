package com.myimbd.domain.usecase

import com.myimbd.domain.model.MovieDomainEntity
import com.myimbd.domain.repository.MovieRepository
import javax.inject.Inject

class GetWishlistedMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(): List<MovieDomainEntity> {
        return repository.getWishlistedMovies()
    }
} 