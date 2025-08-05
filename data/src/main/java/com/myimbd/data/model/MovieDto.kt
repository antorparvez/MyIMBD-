package com.myimbd.data.model

import com.google.gson.annotations.SerializedName

data class MovieDto(
    val id: Int,
    val title: String,
    val year: String,
    val runtime: String,
    val genres: List<String>,
    val director: String,
    val actors: String,
    val plot: String,
    @SerializedName("posterUrl")
    val posterUrl: String
)
