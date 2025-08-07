package com.myimbd.domain.usecase

import com.myimbd.domain.model.MovieDomainEntity
import com.myimbd.domain.repository.MovieRepository
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1, pageSize: Int = 10): List<MovieDomainEntity> {
        return repository.searchMovies(query, page, pageSize)
    }
} 