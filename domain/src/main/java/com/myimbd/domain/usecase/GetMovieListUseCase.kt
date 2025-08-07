package com.myimbd.domain.usecase

import com.myimbd.domain.model.MovieDomainEntity
import com.myimbd.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieListUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(page: Int = 1, pageSize: Int = 10): List<MovieDomainEntity> {
        return repository.getMovies(page, pageSize)
    }
} 