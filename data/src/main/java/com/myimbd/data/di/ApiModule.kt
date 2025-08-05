package com.myimbd.data.di

import com.myimbd.data.remote.MovieApiService
import com.myimbd.data.repository.MovieRepositoryImpl
import com.myimbd.domain.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/erik-sytnyk/movies-list/master/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideMovieApiService(retrofit: Retrofit): MovieApiService =
        retrofit.create(MovieApiService::class.java)

    @Provides
    @Singleton
    fun provideMovieRepository(
        apiService: MovieApiService
    ): MovieRepository = MovieRepositoryImpl(apiService)
}
