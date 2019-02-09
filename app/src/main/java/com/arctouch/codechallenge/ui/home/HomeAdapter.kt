package com.arctouch.codechallenge.ui.home

import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.MovieImageUrlBuilder
import com.arctouch.codechallenge.util.loadUrlImage
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeAdapter(private val movies: List<Movie>) : BaseQuickAdapter<Movie, BaseViewHolder>(R.layout.movie_item,movies) {

    private val movieImageUrlBuilder = MovieImageUrlBuilder()

    override fun convert(helper: BaseViewHolder, item: Movie) {
        helper.setText(R.id.titleTextView, item.title)
        helper.setText(R.id.genresTextView, item.genres?.joinToString(separator = ", ") { it.name })
        helper.setText(R.id.releaseDateTextView, item.releaseDate)
        helper.loadUrlImage(R.id.posterImageView,
                item.posterPath?.let { movieImageUrlBuilder.buildPosterUrl(it)},
                placeHolder = R.drawable.ic_image_placeholder)

    }
}
