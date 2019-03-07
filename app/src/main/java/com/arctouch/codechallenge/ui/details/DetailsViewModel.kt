package com.arctouch.codechallenge.ui.details

import android.arch.lifecycle.MutableLiveData
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.data.repository.MoviesRepository
import com.arctouch.codechallenge.data.model.Movie
import com.arctouch.codechallenge.ui.base.BaseViewModel
import com.arctouch.codechallenge.util.asyncCatching
import com.arctouch.codechallenge.util.onFailure
import com.arctouch.codechallenge.util.onSuccess

class DetailsViewModel(private val moviesRepository: MoviesRepository) :BaseViewModel(){

    val movie = MutableLiveData<Movie>()

    fun getMovie(movieId:Long){
        if(movie.value  == null){
            showLoading()
            asyncCatching {
                moviesRepository.getMovie(movieId)
            }.onSuccess {
                dismissLoading()
                movie.postValue(it)
            }.onFailure {
                dismissLoading()
                showError(R.string.error_get_movie)

            }
        }
    }
}