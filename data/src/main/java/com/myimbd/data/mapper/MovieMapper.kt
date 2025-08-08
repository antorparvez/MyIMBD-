package com.myimbd.data.mapper

import com.myimbd.data.local.MovieEntity
import com.myimbd.data.model.MovieDto
import com.myimbd.domain.model.MovieDomainEntity

fun MovieDto.toDomainEntity(): MovieDomainEntity {
    return MovieDomainEntity(
        id = id,
        title = title,
        year = year,
        runtime = runtime,
        director = director,
        actors = actors,
        genres = genres,
        plot = plot,
        posterUrl = posterUrl,
        isWishlisted = false
    )
}

fun MovieDto.toEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        year = year,
        runtime = runtime,
        director = director,
        actors = actors,
        genres = genres,
        plot = plot,
        poster = posterUrl,
        isWishlisted = false
    )
}

fun MovieEntity.toDomainEntity(): MovieDomainEntity {
    return MovieDomainEntity(
        id = id,
        title = title,
        year = year,
        runtime = runtime,
        director = director,
        actors = actors,
        genres = genres,
        plot = plot,
        posterUrl = poster,
        isWishlisted = isWishlisted
    )
}

fun MovieDomainEntity.toEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        year = year,
        runtime = runtime,
        director = director,
        actors = actors,
        genres = genres,
        plot = plot,
        poster = posterUrl,
        isWishlisted = isWishlisted
    )
}
