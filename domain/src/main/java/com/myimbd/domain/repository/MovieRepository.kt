package com.myimbd.domain.repository

import com.myimbd.domain.model.MovieDomainEntity
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getMovies(page: Int = 1, pageSize: Int = 10): List<MovieDomainEntity>
    suspend fun searchMovies(query: String, page: Int = 1, pageSize: Int = 10): List<MovieDomainEntity>
    suspend fun getMovieById(id: Int): MovieDomainEntity?
    suspend fun getWishlistedMovies(): List<MovieDomainEntity>
    suspend fun addToWishlist(movieId: Int)
    suspend fun removeFromWishlist(movieId: Int)
    suspend fun hasLocalData(): Boolean
    suspend fun saveMovies(movies: List<MovieDomainEntity>)
    fun getWishlistCount(): Flow<Int>
    suspend fun getMoviesByGenre(genre: String?, page: Int = 1, pageSize: Int = 10): List<MovieDomainEntity>
}
