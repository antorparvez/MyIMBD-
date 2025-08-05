package com.myimbd.data.model

data class MoviesResponseDto(
    val genres: List<String>,
    val movies: List<MovieDto>
)

