package com.arctouch.codechallenge.ui.home

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.ui.base.BaseActivity
import kotlinx.android.synthetic.main.home_activity.*
import org.koin.android.viewmodel.ext.android.viewModel

class HomeActivity : BaseActivity() {

    override val viewModel by viewModel<HomeViewModel>()
    override val activityLayout = R.layout.home_activity
    private lateinit var adapter:HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupAdapter()
        observeViewModel()
        //viewModel.getMovies(1)
    }

    private fun setupAdapter(){
        adapter = HomeAdapter(ArrayList())
        recyclerView.adapter = adapter
    }

    private fun observeViewModel(){
        viewModel.movies.observe(this, Observer {
            adapter.setNewData(it?.results)
            progressBar.visibility = View.GONE
        })
    }

    companion object {
        fun launch(context: Context){
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }
}
