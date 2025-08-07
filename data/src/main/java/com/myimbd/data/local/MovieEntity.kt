package com.myimbd.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.myimbd.data.local.converter.StringListConverter

@Entity(tableName = "movies")
@TypeConverters(StringListConverter::class)
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val year: String,
    val runtime: String?,
    val director: String?,
    val actors: String?,
    val genres: List<String>,
    val plot: String?,
    val poster: String?,
    val isWishlisted: Boolean = false
) 