package com.arctouch.codechallenge.ui.home

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.Menu
import android.view.MenuItem
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.ui.base.BaseActivity
import com.arctouch.codechallenge.ui.custom.SearchAnimationToolbar
import com.arctouch.codechallenge.ui.details.DetailsActivity
import com.arctouch.codechallenge.util.hideKeyboard
import com.arctouch.codechallenge.util.inflateView
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class HomeActivity : BaseActivity(){

    override val viewModel by viewModel<HomeViewModel>()
    override val activityLayout = R.layout.home_activity
    private lateinit var adapter: HomeAdapter
    private var jobSearch: Job?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAdapter()
        setupToolbar()
        observeViewModel()
    }

    override fun onBackPressed() {
        if(!hideKeyboard()){
            if(!vToolbar.onBackPressed()){
                super.onBackPressed()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_search ->{
                vToolbar.onSearchIconClick()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar(){
        vToolbar.setSupportActionBar(this)
        vToolbar.setOnSearchQueryChangedListener(onSearchQueryChangedListener)
    }

    private fun setupAdapter(){

        swipeRefreshLayout.setOnRefreshListener(onRefreshListener)

        adapter = HomeAdapter(viewModel.movies).apply {
            setOnLoadMoreListener(requestLoadMoreListener, recyclerView)
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
                if(viewModel.currentPage == 1L){
                    adapter.notifyDataSetChanged()
                }else{
                    adapter.loadMoreComplete()
                }
            }
        })

        viewModel.enableLoadMore.observe(this, Observer {
            it?.let { enableLoadMore ->
                adapter.setEnableLoadMore(enableLoadMore)
            }
        })
    }

    private val onRefreshListener = SwipeRefreshLayout.OnRefreshListener{
        adapter.emptyView = inflateView(R.layout.home_activity_loading, recyclerView)
        viewModel.refreshMovies()
    }

    private val requestLoadMoreListener = BaseQuickAdapter.RequestLoadMoreListener {
        viewModel.loadMoreMovies()
    }

    private val onSearchQueryChangedListener = object : SearchAnimationToolbar.OnSearchQueryChangedListener{

        override fun onSearchExpanded() {
            swipeRefreshLayout.isEnabled = false
        }

        override fun onSearchCollapsed() {
            swipeRefreshLayout.isEnabled = true
            viewModel.currentQuery = ""
            viewModel.refreshMovies()
        }

        override fun onSearchQueryChanged(query: String){
            jobSearch?.cancel()
            jobSearch = launch {
                delay(500)
                viewModel.searchMovies(query,1)
            }
        }

        override fun onSearchSubmitted(query: String) {
            viewModel.searchMovies(query,1)
        }
    }

    override fun errorHandler(message: String?) {
        super.errorHandler(message)
        adapter.loadMoreFail()
        adapter.emptyView = inflateView(R.layout.error_view, recyclerView)
        swipeRefreshLayout.isRefreshing = false
    }
}
