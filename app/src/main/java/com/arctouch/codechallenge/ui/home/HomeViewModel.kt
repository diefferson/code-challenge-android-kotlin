package com.arctouch.codechallenge.ui.home

import android.arch.lifecycle.MutableLiveData
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.data.repository.MoviesRepository
import com.arctouch.codechallenge.data.model.Genre
import com.arctouch.codechallenge.data.model.Movie
import com.arctouch.codechallenge.data.model.MoviesResponse
import com.arctouch.codechallenge.ui.base.BaseViewModel
import com.arctouch.codechallenge.ui.base.SingleLiveEvent
import com.arctouch.codechallenge.util.asyncCatching
import com.arctouch.codechallenge.util.onFailure
import com.arctouch.codechallenge.util.onSuccess

class HomeViewModel(private val moviesRepository: MoviesRepository) : BaseViewModel(){

    var currentQuery = ""
    var currentPage = 1L
    var totalPages = 0L
    val genres = ArrayList<Genre>()
    val movies = ArrayList<Movie>()
    val enableLoadMore = MutableLiveData<Boolean>()
    val updatedMovies = SingleLiveEvent<Boolean>()
    var searching = false

    init {
        getGenres()
    }

    fun refreshMovies() {
        getMovies(1)
    }

    fun loadMoreMovies(){
        if(currentPage< totalPages){
            currentPage++
            if(currentQuery.isEmpty()){
                getMovies(currentPage)
            }else{
                searchMovies(currentQuery, currentPage)
            }
        }else{
            enableLoadMore.value = false
        }
    }

    private fun getGenres() {
        asyncCatching {
            moviesRepository.getGenres()
        }.onSuccess {
            genres.clear()
            genres.addAll(it)
            getMovies(1)
        }.onFailure {
            showError(R.string.error_get_genres)
        }
    }

    private fun getMovies(page:Long) {
        asyncCatching {
            moviesRepository.getMovies(page)
        }.onSuccess {result->
            handleMoviesResult(result)
        }.onFailure{
            showError(R.string.error_get_movies)
        }
    }

    fun searchMovies(query:String, page: Long) {
        if(query.isNotEmpty()){
            currentQuery = query
            if(page == 1L){
                movies.clear()
                searching = true
                updatedMovies.value = true
            }
            asyncCatching {
                moviesRepository.searchMovies(query, page)
            }.onSuccess {result->
                searching = false
                handleMoviesResult(result)
            }.onFailure {
                showError(R.string.error_get_movies)
            }
        }
    }

    private fun handleMoviesResult(result: MoviesResponse) {
        currentPage = result.page
        totalPages = result.totalPages
        enableLoadMore.value = currentPage != totalPages

        val moviesResult = result.results.map { movie ->
            movie.copy(genres = genres.filter { movie.genreIds?.contains(it.id) == true })
        }

        if (currentPage == 1L) {
            movies.clear()
            movies.addAll(moviesResult)
        } else {
            movies.addAll(moviesResult)
        }

        updatedMovies.value = true
    }
}