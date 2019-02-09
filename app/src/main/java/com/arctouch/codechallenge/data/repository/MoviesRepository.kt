package com.arctouch.codechallenge.data.repository

import android.content.Context
import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.model.Genre
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.UpcomingMoviesResponse
import io.coroutines.cache.core.CachePolicy
import io.coroutines.cache.core.CoroutinesCache
import java.util.concurrent.TimeUnit

class MoviesRepository(private val api:TmdbApi, context: Context): CoroutinesCache(context){

    suspend fun getGenres(update:Boolean) : List<Genre>{
        return asyncCache({api.genres(BuildConfig.API_KEY, BuildConfig.DEFAULT_LANGUAGE)},
                GENRES_KEY,
                CachePolicy.EvictProvider(update) ).await().genres
    }

    suspend fun getMovies(page:Long): UpcomingMoviesResponse {
        return asyncCache({api.upcomingMovies(BuildConfig.API_KEY, BuildConfig.DEFAULT_LANGUAGE, page, BuildConfig.DEFAULT_REGION)},
                MOVIES_KEY+page,
                CachePolicy.LifeCache(5, TimeUnit.MINUTES)).await()
    }

    suspend fun getMovie(movieId:Long):Movie{
        return asyncCache({api.movie(movieId,BuildConfig.API_KEY, BuildConfig.DEFAULT_LANGUAGE )},
                MOVIE_KEY+movieId,
                CachePolicy.LifeCache(5, TimeUnit.MINUTES)).await()
    }

    companion object {
        private const val GENRES_KEY = "GENRES_KEY"
        private const val MOVIES_KEY = "MOVIES_KEY"
        private const val MOVIE_KEY = "MOVIE_KEY"
    }

}