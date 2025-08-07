package com.myimbd.data.repository

import com.myimbd.data.local.MovieDao
import com.myimbd.data.mapper.toDomainEntity
import com.myimbd.data.mapper.toEntity
import com.myimbd.data.remote.MovieApiService
import com.myimbd.domain.model.MovieDomainEntity
import com.myimbd.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService,
    private val movieDao: MovieDao
) : MovieRepository {

    override suspend fun getMovies(page: Int, pageSize: Int): List<MovieDomainEntity> {
        val offset = (page - 1) * pageSize
        return movieDao.getMoviesByGenre(null, pageSize, offset).map { it.toDomainEntity() }
    }

    override suspend fun searchMovies(query: String, page: Int, pageSize: Int): List<MovieDomainEntity> {
        val offset = (page - 1) * pageSize
        return movieDao.searchMovies(query, pageSize, offset).map { it.toDomainEntity() }
    }

    override suspend fun getMovieById(id: Int): MovieDomainEntity? {
        return movieDao.getMovieById(id)?.toDomainEntity()
    }

    override suspend fun getWishlistedMovies(): List<MovieDomainEntity> {
        return movieDao.getWishlistedMovies().map { it.toDomainEntity() }
    }

    override suspend fun addToWishlist(movieId: Int) {
        movieDao.updateWishlistStatus(movieId, true)
    }

    override suspend fun removeFromWishlist(movieId: Int) {
        movieDao.updateWishlistStatus(movieId, false)
    }

    override suspend fun hasLocalData(): Boolean {
        return movieDao.getMovieCount() > 0
    }

    override suspend fun saveMovies(movies: List<MovieDomainEntity>) {
        val movieEntities = movies.map { it.toEntity() }
        movieDao.insertMovies(movieEntities)
    }

    override fun getWishlistCount(): Flow<Int> {
        return movieDao.getWishlistCount()
    }

    override suspend fun getMoviesByGenre(genre: String?, page: Int, pageSize: Int): List<MovieDomainEntity> {
        val offset = (page - 1) * pageSize
        return movieDao.getMoviesByGenre(genre, pageSize, offset).map { it.toDomainEntity() }
    }

    suspend fun fetchAndSaveMoviesFromRemote(): List<MovieDomainEntity> {
        val response = apiService.getMovies()
        val movies = response.movies.map { it.toDomainEntity() }
        saveMovies(movies)
        return movies
    }
}
