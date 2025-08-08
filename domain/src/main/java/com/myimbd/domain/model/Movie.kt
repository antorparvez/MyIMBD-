package com.myimbd.domain.model

data class MovieDomainEntity(
    val id: Int,
    val title: String,
    val year: String,
    val runtime: String?,
    val director: String?,
    val actors: String?,
    val genres: List<String>,
    val plot: String?,
    val posterUrl: String?,
    val isWishlisted: Boolean = false
)
