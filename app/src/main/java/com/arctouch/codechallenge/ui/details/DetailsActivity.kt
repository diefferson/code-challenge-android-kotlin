package com.arctouch.codechallenge.ui.details

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.ui.base.BaseActivity
import com.arctouch.codechallenge.util.MovieImageUrlBuilder
import com.arctouch.codechallenge.util.formatDate
import com.arctouch.codechallenge.util.inflateView
import com.arctouch.codechallenge.util.loadUrlImage
import kotlinx.android.synthetic.main.activity_details.*
import org.koin.android.viewmodel.ext.android.viewModel

class DetailsActivity :BaseActivity(){

    override val viewModel by viewModel<DetailsViewModel>()
    override val activityLayout = R.layout.activity_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()
        observeViewModel()
        viewModel.getMovie(intent?.getLongExtra(MOVIE_ID,0)?:0)
    }

    private fun setupToolbar() {
        setSupportActionBar(vToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = ""
    }

    private fun observeViewModel(){
        viewModel.movie.observe(this, Observer {
            it?.let { movie->
                val movieImageUrlBuilder = MovieImageUrlBuilder()
                backdropImageView.loadUrlImage(movie.backdropPath?.let{ movieImageUrlBuilder.buildBackdropUrl(it)})
                posterImageView.loadUrlImage(movie.posterPath?.let{ movieImageUrlBuilder.buildPosterUrl(it)})
                titleTextView.text = movie.title
                releaseDateTextView.text = movie.releaseDate?.formatDate()
                genresTextView.text = movie.genres?.joinToString(separator = ", "){ it.name }
                overviewTextView.text = movie.overview
            }
        })
    }

    override fun loadingHandler(isLoading: Boolean) {
        if(isLoading){
            if(findViewById<View>(R.id.loading_root)== null){
                val loadingView = inflateView(R.layout.activity_details_loading,
                        this.findViewById<ViewGroup>(android.R.id.content) )

                findViewById<ViewGroup>(android.R.id.content).addView(loadingView)
            }
        }else{
            val root = findViewById<ViewGroup>(android.R.id.content)
            root.removeView(findViewById(R.id.loading_root))
        }
    }

    companion object {
        private const val MOVIE_ID = "MOVIE_ID"
        fun launch(context: Context, movieId:Long){
            context.startActivity(Intent(context, DetailsActivity::class.java).apply {
                putExtra(MOVIE_ID, movieId)
            })
        }
    }
}