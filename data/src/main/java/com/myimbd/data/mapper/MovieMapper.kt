package com.myimbd.data.mapper

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
        poster = posterUrl,
        isWishlisted = false // API doesn't provide wishlist info
    )
}
