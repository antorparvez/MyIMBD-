package com.myimbd.data.repository
import com.myimbd.data.mapper.toDomainEntity
import com.myimbd.data.remote.MovieApiService
import com.myimbd.domain.model.MovieDomainEntity
import com.myimbd.domain.repository.MovieRepository

class MovieRepositoryImpl(
    private val apiService: MovieApiService
) : MovieRepository {

    override suspend fun getMovies(): List<MovieDomainEntity> {
        val response = apiService.getMovies()
        return response.movies.map { it.toDomainEntity() }
    }

  /*  override suspend fun searchMovies(query: String, page: Int, pageSize: Int): List<MovieDomainEntity> {
        throw NotImplementedError("Search not implemented")
    }

    override suspend fun getMovieById(id: Int): MovieDomainEntity? {
        throw NotImplementedError("GetMovieById not implemented")
    }

    override suspend fun getWishlistedMovies(): List<MovieDomainEntity> {
        throw NotImplementedError("Wishlisted movies not implemented")
    }

    override suspend fun addToWishlist(movieId: Int) {
        throw NotImplementedError("AddToWishlist not implemented")
    }

    override suspend fun removeFromWishlist(movieId: Int) {
        throw NotImplementedError("RemoveFromWishlist not implemented")
    }

    override suspend fun hasLocalData(): Boolean {
        throw NotImplementedError("HasLocalData not implemented")
    }

    override suspend fun saveMovies(movies: List<MovieDomainEntity>) {
        throw NotImplementedError("SaveMovies not implemented")
    }

    override fun getWishlistCount(): kotlinx.coroutines.flow.Flow<Int> {
        throw NotImplementedError("GetWishlistCount not implemented")
    }*/
}
