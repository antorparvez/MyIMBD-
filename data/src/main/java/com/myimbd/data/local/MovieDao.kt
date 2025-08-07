package com.myimbd.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY year DESC")
    fun getAllMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isWishlisted = 1 ORDER BY year DESC")
    suspend fun getWishlistedMovies(): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: Int): MovieEntity?

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%' OR plot LIKE '%' || :query || '%' ORDER BY year DESC LIMIT :limit OFFSET :offset")
    suspend fun searchMovies(query: String, limit: Int, offset: Int): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE (:genres IS NULL OR genres LIKE '%' || :genres || '%') ORDER BY year DESC LIMIT :limit OFFSET :offset")
    suspend fun getMoviesByGenre(genres: String?, limit: Int, offset: Int): List<MovieEntity>

    @Query("SELECT COUNT(*) FROM movies WHERE isWishlisted = 1")
    fun getWishlistCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Query("UPDATE movies SET isWishlisted = :isWishlisted WHERE id = :movieId")
    suspend fun updateWishlistStatus(movieId: Int, isWishlisted: Boolean)

    @Query("DELETE FROM movies")
    suspend fun deleteAllMovies()
} 