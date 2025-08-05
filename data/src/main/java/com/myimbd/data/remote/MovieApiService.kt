package com.myimbd.data.remote

import com.myimbd.data.model.MoviesResponseDto
import com.myimbd.domain.model.MovieDomainEntity
import retrofit2.http.GET

interface MovieApiService {
    @GET("db.json")
    suspend fun getMovies(): MoviesResponseDto
}
