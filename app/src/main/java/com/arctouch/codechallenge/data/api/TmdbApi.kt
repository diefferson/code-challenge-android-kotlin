package com.arctouch.codechallenge.data.api

import com.arctouch.codechallenge.data.model.GenreResponse
import com.arctouch.codechallenge.data.model.Movie
import com.arctouch.codechallenge.data.model.UpcomingMoviesResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("genre/movie/list")
    fun genres(): Deferred<GenreResponse>

    @GET("movie/popular")
    fun upcomingMovies(
        @Query("page") page: Long,
        @Query("region") region: String
    ): Deferred<UpcomingMoviesResponse>

    @GET("movie/{id}")
    fun movie(@Path("id") id: Long): Deferred<Movie>
}
