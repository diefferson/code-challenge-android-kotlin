package com.arctouch.codechallenge.data.repository

import android.content.Context
import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.data.api.TmdbApi
import com.arctouch.codechallenge.data.model.Genre
import com.arctouch.codechallenge.data.model.Movie
import com.arctouch.codechallenge.data.model.MoviesResponse
import io.coroutines.cache.core.CachePolicy
import io.coroutines.cache.core.CoroutinesCache
import java.util.concurrent.TimeUnit

class MoviesRepository(private val api:TmdbApi, context: Context): CoroutinesCache(context){

    suspend fun getGenres() : List<Genre>{
        return asyncCache({ api.genres() },
                GENRES_KEY,
                CachePolicy.LifeCache(1, TimeUnit.DAYS)).await().genres
    }

    suspend fun getMovies(page:Long): MoviesResponse {

        return asyncCache({ api.upcomingMovies(page) },
                MOVIES_KEY+page,
                CachePolicy.LifeCache(10, TimeUnit.MINUTES)).await()
    }

    suspend fun searchMovies(query:String, page:Long): MoviesResponse {
        return api.searchMovies(query,page).await()
    }

    suspend fun getMovie(movieId:Long):Movie{
        return asyncCache({ api.movie(movieId) },
                MOVIE_KEY+movieId,
                CachePolicy.LifeCache(10, TimeUnit.MINUTES)).await()
    }

    companion object {
        private const val GENRES_KEY = "GENRES_KEY"
        private const val MOVIES_KEY = "MOVIES_KEY"
        private const val MOVIE_KEY = "MOVIE_KEY"
    }
}