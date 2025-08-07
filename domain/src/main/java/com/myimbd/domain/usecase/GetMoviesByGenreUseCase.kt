package com.myimbd.domain.usecase

import com.myimbd.domain.model.MovieDomainEntity
import com.myimbd.domain.repository.MovieRepository
import javax.inject.Inject

class GetMoviesByGenreUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(genre: String?, page: Int = 1, pageSize: Int = 10): List<MovieDomainEntity> {
        return repository.getMoviesByGenre(genre, page, pageSize)
    }
} 