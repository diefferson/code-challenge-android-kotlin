package com.arctouch.codechallenge.ui.home

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.ui.base.BaseActivity
import com.arctouch.codechallenge.ui.details.DetailsActivity
import com.arctouch.codechallenge.util.inflateView
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.home_activity.*
import org.koin.android.viewmodel.ext.android.viewModel

class HomeActivity : BaseActivity(), BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener{

    override val viewModel by viewModel<HomeViewModel>()
    override val activityLayout = R.layout.home_activity
    private lateinit var adapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAdapter()
        observeViewModel()
    }

    override fun onLoadMoreRequested() {
        viewModel.loadMoreMovies()
    }

    override fun onRefresh() {
        adapter.emptyView = inflateView(R.layout.home_activity_loading, recyclerView)
        viewModel.refreshMovies()
    }

    private fun setupAdapter(){

        swipeRefreshLayout.setOnRefreshListener(this)

        adapter = HomeAdapter(viewModel.movies).apply {
            setOnLoadMoreListener(this@HomeActivity, recyclerView)
            openLoadAnimation(BaseQuickAdapter.ALPHAIN)
            setLoadMoreView(HomeLoadMoreView())
            setEnableLoadMore(true)

            setOnItemClickListener { adapter, view, position ->
                val movie = data[position]
                DetailsActivity.launch(this@HomeActivity, movie.id)
            }
        }

        recyclerView.adapter = adapter
        adapter.emptyView = inflateView(R.layout.home_activity_loading, recyclerView)
    }

    private fun observeViewModel(){

        viewModel.updatedMovies.observe(this, Observer {
            if(it!= null && it){
                swipeRefreshLayout.isRefreshing = false
                adapter.loadMoreComplete()
            }
        })

        viewModel.enableLoadMore.observe(this, Observer {
            it?.let { enableLoadMore ->
                adapter.setEnableLoadMore(enableLoadMore)
            }
        })
    }

    override fun errorHandler(message: String?) {
        super.errorHandler(message)
        adapter.loadMoreFail()
        adapter.emptyView = inflateView(R.layout.error_view, recyclerView)
        swipeRefreshLayout.isRefreshing = false
    }

    companion object {
        fun launch(context: Context){
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }
}
