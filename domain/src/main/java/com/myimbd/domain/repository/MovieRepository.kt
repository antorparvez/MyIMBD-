package com.myimbd.domain.repository

import com.myimbd.domain.model.MovieDomainEntity

interface MovieRepository {
    suspend fun getMovies():  List<MovieDomainEntity>
}
