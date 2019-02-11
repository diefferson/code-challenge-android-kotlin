package com.arctouch.codechallenge.ui.home

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import com.arctouch.codechallenge.data.model.Genre
import com.arctouch.codechallenge.data.model.Movie
import com.arctouch.codechallenge.data.model.MoviesResponse
import com.arctouch.codechallenge.data.repository.MoviesRepository
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.MockitoAnnotations

class HomeViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    lateinit var homeViewModel :HomeViewModel

    lateinit var genresMock:List<Genre>
    lateinit var moviesResponseMock: MoviesResponse
    lateinit var observer: Observer<Boolean>
    lateinit var moviesRepositoryMock :MoviesRepository


    @Before
    fun setup(){
        genresMock= listOf(mock(), mock(),mock())
        moviesResponseMock= mock()
        observer = mock()
        moviesRepositoryMock = mock()
        homeViewModel = HomeViewModel(moviesRepositoryMock)
    }

    @Test
    fun testRefreshMovieShouldCallGetMovieWithValueOne(){
        val viewModelTest= spy(homeViewModel)
        viewModelTest.refreshMovies()
        verify(viewModelTest).getMovies(1)
    }

    @Test
    fun testLoadMoreGetMoviesShouldCallGetMoviesIncreasingPage(){
        val viewModelTest= spy(homeViewModel)
        viewModelTest.currentPage = 1
        viewModelTest.totalPages = 5
        viewModelTest.currentQuery = ""
        viewModelTest.loadMoreMovies()
        verify(viewModelTest).getMovies(2)
    }

    @Test
    fun testLoadMoreSearchMoviesShouldCallGetMoviesIncreasingPage(){
        val viewModelTest= spy(homeViewModel)
        viewModelTest.currentPage = 1
        viewModelTest.totalPages = 5
        viewModelTest.currentQuery = "query"
        viewModelTest.loadMoreMovies()
        verify(viewModelTest).searchMovies("query",2)
    }

    @Test
    fun testLoadMoreShouldNotCallGetMoviesWhenIsLastPage(){
        homeViewModel.enableLoadMore.observeForever(observer)
        homeViewModel.currentPage = 5
        homeViewModel.totalPages = 5
        homeViewModel.currentQuery = ""
        homeViewModel.loadMoreMovies()
        verify(observer).onChanged(false)
    }

    @Test
    fun testGetGendersSuccessCase(){
        runBlocking {
            whenever(moviesRepositoryMock.getGenres()).thenReturn(genresMock)
            homeViewModel.getGenres()
        }

    }


    @Test
    fun testGetGendersShouldNotReloadOnRotateScreen(){
        homeViewModel.genres.addAll(genresMock)
        runBlocking {
            whenever(moviesRepositoryMock.getGenres()).thenReturn(genresMock)
            homeViewModel.getGenres()
            verifyZeroInteractions(moviesRepositoryMock)
        }
    }

    @Test
    fun testGetMoviesSuccessCase() {
        homeViewModel.updatedMovies.observeForever(observer)
        runBlocking {
            whenever(moviesResponseMock.page).thenReturn(NEW_PAGE)
            whenever(moviesResponseMock.totalPages).thenReturn(TOTAL_PAGES)
            whenever(moviesRepositoryMock.getMovies(PAGE)).thenReturn(moviesResponseMock)
            homeViewModel.getMovies(PAGE)

        }
        verify(observer).onChanged(true)
        Assertions.assertThat(homeViewModel.currentPage).isEqualTo(NEW_PAGE)
        Assertions.assertThat(homeViewModel.totalPages).isEqualTo(TOTAL_PAGES)
    }




    companion object {
        const val PAGE = 1L
        const val NEW_PAGE = 2L
        const val TOTAL_PAGES = 10L
    }
}