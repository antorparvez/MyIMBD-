package com.myimbd.domain.usecase

import com.myimbd.domain.repository.MovieRepository
import com.myimbd.domain.model.MovieDomainEntity
import javax.inject.Inject

class GetMovieListUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke():  List<MovieDomainEntity> {
        return repository.getMovies()
    }
}
