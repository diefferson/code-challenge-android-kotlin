package com.arctouch.codechallenge.ui.details

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.data.model.Movie
import com.arctouch.codechallenge.data.repository.MoviesRepository
import com.arctouch.codechallenge.ui.base.ViewState
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class DetailsViewModelTest{

    @get:Rule
     val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var movieMock :Movie
    private lateinit var moviesRepositoryMock :MoviesRepository
    private lateinit var observer:Observer<Movie>
    private lateinit var observerState:Observer<ViewState>

    lateinit var detailsViewModel: DetailsViewModel

    @Before
    fun setup(){
        movieMock  = mock()
        moviesRepositoryMock =mock()
        observer = mock()
        observerState = mock()
        detailsViewModel = DetailsViewModel(moviesRepositoryMock)
    }

    @Test
    fun testGetMovieSuccess(){
        detailsViewModel.movie.observeForever(observer)
        runBlocking {
            whenever(moviesRepositoryMock.getMovie(MOVIE_ID)).thenReturn(movieMock)
            detailsViewModel.getMovie(MOVIE_ID)
        }

        verify(observer).onChanged(movieMock)
    }

    @Test
    fun testShouldNotReloadOnRotateScreen(){
        detailsViewModel.movie.postValue(movieMock)
        runBlocking {
            whenever(moviesRepositoryMock.getMovie(MOVIE_ID)).thenReturn(movieMock)
            detailsViewModel.getMovie(MOVIE_ID)
            verifyZeroInteractions(moviesRepositoryMock)
        }
    }

    @Test
    fun testShouldShowExpectedError(){
        detailsViewModel.movie.observeForever(observer)
        detailsViewModel.getViewStateObservable().observeForever(observerState)
        runBlocking {
            whenever(moviesRepositoryMock.getMovie(MOVIE_ID_ERROR)).thenReturn(null)
            detailsViewModel.getMovie(MOVIE_ID_ERROR)
            delay(200)
        }
        verifyZeroInteractions(observer)

        verify(observerState, times(3))
    }

    companion object {
        const val MOVIE_ID   = 2L
        const val MOVIE_ID_ERROR   = 3L
    }

}