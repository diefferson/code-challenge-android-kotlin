package com.arctouch.codechallenge.ui.home

import android.arch.lifecycle.MutableLiveData
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.data.repository.MoviesRepository
import com.arctouch.codechallenge.model.Genre
import com.arctouch.codechallenge.model.UpcomingMoviesResponse
import com.arctouch.codechallenge.ui.base.BaseViewModel
import com.arctouch.codechallenge.util.asyncCatching
import com.arctouch.codechallenge.util.onFailure
import com.arctouch.codechallenge.util.onSuccess

class HomeViewModel(private val moviesRepository: MoviesRepository) : BaseViewModel(){

    val movies = MutableLiveData<UpcomingMoviesResponse>()
    val genres = MutableLiveData<List<Genre>>()

    init {
        getGenres(true)
    }

    private fun getGenres(update:Boolean){
        asyncCatching {
            moviesRepository.getGenres(update)
        }.onSuccess {
            genres.value =it
            getMovies(1)
        }.onFailure {
            showError(R.string.error_get_genres)
        }
    }

    fun getMovies(page:Long){

        asyncCatching {
            moviesRepository.getMovies(page)
        }.onSuccess {

            val result = it

            result.results.map { movie ->
                movie.copy(genres = genres.value?.filter { movie.genreIds?.contains(it.id) == true })
            }

            movies.value = result

        }.onFailure {
            showError(R.string.error_get_movies)
        }
    }
}