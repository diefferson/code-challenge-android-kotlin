package com.arctouch.codechallenge.data.model

import com.squareup.moshi.Json

data class GenreResponse(val genres: List<Genre>)

data class Genre(val id: Long, val name: String)

data class MoviesResponse(
        val page: Long,
        var results: List<Movie>,
        @Json(name = "total_pages") val totalPages: Long,
        @Json(name = "total_results") val totalResults: Long
)

data class Movie(
    val id: Long,
    val title: String,
    val overview: String? = null,
    val genres: List<Genre>? = null,
    @Json(name = "genre_ids") val genreIds: List<Long>? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "backdrop_path") val backdropPath: String? = null,
    @Json(name = "release_date") val releaseDate: String? = null
)
