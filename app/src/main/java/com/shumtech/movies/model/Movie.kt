package com.shumtech.movies.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val year: String,
    val posterUrl: String,
    val imdbID: String,
    val tmdbId: Int? = null,
    var genre: String? = null,
    var isSelected: Boolean = false
)
