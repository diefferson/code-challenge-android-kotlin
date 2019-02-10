package com.arctouch.codechallenge.ui.home

import com.arctouch.codechallenge.R
import com.chad.library.adapter.base.loadmore.LoadMoreView

class HomeLoadMoreView : LoadMoreView() {

    override fun getLayoutId() = R.layout.home_load_more

    override fun getLoadingViewId() = R.id.load_more_loading_view

    override fun getLoadFailViewId() = R.id.load_more_load_fail_view

    override fun getLoadEndViewId() = R.id.load_more_load_end_view
}
