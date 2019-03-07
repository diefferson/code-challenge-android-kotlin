package com.arctouch.codechallenge.data.repository

import com.arctouch.codechallenge.data.api.TmdbApi
import com.arctouch.codechallenge.data.model.Genre
import com.arctouch.codechallenge.data.model.GenreResponse
import com.arctouch.codechallenge.data.model.Movie
import com.arctouch.codechallenge.data.model.MoviesResponse
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.coroutines.cache.core.CoroutinesCache
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals

class MoviesRepositoryTest{

    private lateinit var moviesRepository:MoviesRepository

    private lateinit var apiMock:TmdbApi
    private lateinit var cacheMock :CoroutinesCache
    private lateinit var moviesResponseMock: MoviesResponse
    private lateinit var genresResponseMock: GenreResponse
    private lateinit var genresMock :List<Genre>
    private lateinit var movieMock :Movie


    @Before
    fun setup(){
        cacheMock= CoroutinesCache(mock(), true)
        moviesResponseMock= mock()
        genresResponseMock=mock()
        genresMock= listOf(mock(), mock())
        movieMock  = mock()
        apiMock = mock{
            on { genres() }.thenReturn(CompletableDeferred(genresResponseMock))
            on { upcomingMovies(PAGE) }.thenReturn(CompletableDeferred(moviesResponseMock))
            on { movie(MOVIE_ID) }.thenReturn(CompletableDeferred(movieMock))
        }
        moviesRepository = MoviesRepository(apiMock,cacheMock)
    }

    @Test
    fun testSearchMoviesShouldReturnCorrectValue(){
        val result = runBlocking{
            whenever(  apiMock.searchMovies(QUERY, PAGE)).thenReturn(CompletableDeferred(moviesResponseMock))
            moviesRepository.searchMovies(QUERY, PAGE)
        }
        verify(apiMock).searchMovies(QUERY, PAGE)
        assertEquals(result, moviesResponseMock)
    }

    @Test
    fun testGetGenresShouldReturnCorrectValue(){
        whenever(genresResponseMock.genres).thenReturn(genresMock)
        val result = runBlocking{
            moviesRepository.getGenres()
        }
        verify(apiMock).genres()
        assertEquals(result, genresMock)
    }

    @Test
    fun testGetMoviesShouldReturnCorrectValue(){
        val result = runBlocking{
            moviesRepository.getMovies(PAGE)
        }
        verify(apiMock).upcomingMovies(PAGE)
        assertEquals(result, moviesResponseMock)
    }

    @Test
    fun testGetMovieShouldReturnCorrectValue(){
        val result = runBlocking{
            moviesRepository.getMovie(MOVIE_ID)
        }
        verify(apiMock).movie(MOVIE_ID)
        assertEquals(result, movieMock)
    }

    companion object {
        const val QUERY = "myQuery"
        const val PAGE = 3L
        const val MOVIE_ID = 3L
    }
}