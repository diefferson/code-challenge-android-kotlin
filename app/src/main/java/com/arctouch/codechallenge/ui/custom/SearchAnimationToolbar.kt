package com.arctouch.codechallenge.ui.custom

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.arctouch.codechallenge.R

class SearchAnimationToolbar : FrameLayout, TextWatcher {

    init {
        inflateAndBindViews()
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        bindAttributes(attrs)
    }

    lateinit var toolbar: Toolbar
        private set

    lateinit var searchToolbar: Toolbar
        private set

    var isSearchExpanded = false

    private lateinit var txtSearch: EditText
    private lateinit var searchMenuItem: MenuItem
    private var onSearchQueryChangedListener: OnSearchQueryChangedListener? = null
    private var currentQuery = ""


    private fun bindAttributes(attrs: AttributeSet?) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchAnimationToolbar)
        val toolbarTitle = typedArray.getString(R.styleable.SearchAnimationToolbar_title)
        val toolbarTitleColor = typedArray.getColor(R.styleable.SearchAnimationToolbar_titleTextColor, Color.WHITE)
        val toolbarSubtitle = typedArray.getString(R.styleable.SearchAnimationToolbar_subtitle)
        val toolbarSubtitleColor = typedArray.getColor(R.styleable.SearchAnimationToolbar_subtitleTextColor, Color.WHITE)
        val searchHint = typedArray.getString(R.styleable.SearchAnimationToolbar_searchHint)
        val searchBackgroundColor = typedArray.getColor(R.styleable.SearchAnimationToolbar_searchBackgroundColor, Color.WHITE)
        val searchTextColor = typedArray.getColor(R.styleable.SearchAnimationToolbar_searchTextColor, Color.BLACK)
        val searchHintColor = typedArray.getColor(R.styleable.SearchAnimationToolbar_searchHintColor, Color.GRAY)

        toolbar.title = toolbarTitle
        setTitleTextColor(toolbarTitleColor)
        setSearchTextColor(searchTextColor)
        setSearchHintColor(searchHintColor)
        setSearchBackgroundColor(searchBackgroundColor)

        if (!TextUtils.isEmpty(toolbarSubtitle)) {
            setToolbarSubtitle(toolbarSubtitle)
            setToolbarSubtitleColor(toolbarSubtitleColor)
        }

        if (!TextUtils.isEmpty(searchHint)) {
            setSearchHint(searchHint)
        }

        typedArray.recycle()
    }


    private fun inflateAndBindViews() {
        View.inflate(context, R.layout.view_search_toolbar, this@SearchAnimationToolbar)

        toolbar = findViewById<View>(R.id.lib_search_animation_toolbar) as Toolbar
        searchToolbar = findViewById<View>(R.id.lib_search_animation_search_toolbar) as Toolbar

        searchToolbar.inflateMenu(R.menu.lib_search_toolbar_menu_search)
        searchToolbar.setNavigationOnClickListener { collapse() }

        searchMenuItem = searchToolbar.menu.findItem(R.id.lib_search_toolbar_action_filter_search)

        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                collapse()
                return true
            }
        })

        val searchView = searchMenuItem.actionView as SearchView

        // Enable/Disable Submit button in the keyboard
        searchView.isSubmitButtonEnabled = false

        // Change search close button image

        val closeButton = searchView.findViewById<View>(R.id.search_close_btn) as ImageView
        closeButton.setImageResource(R.drawable.ic_close_grey_24dp)

        // set hint and the text colors
        txtSearch = searchView.findViewById<View>(android.support.v7.appcompat.R.id.search_src_text) as EditText
        txtSearch.addTextChangedListener(this@SearchAnimationToolbar)
        txtSearch.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                notifySearchSubmitted(txtSearch.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

    }

    override fun onDetachedFromWindow() {
        txtSearch.removeTextChangedListener(this@SearchAnimationToolbar)
        super.onDetachedFromWindow()
    }

    fun setSupportActionBar(act: AppCompatActivity) {
        act.setSupportActionBar(toolbar)
    }

    fun onSearchIconClick(): Boolean {
        expand()
        searchMenuItem.expandActionView()
        return true
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun circleReveal(isShow: Boolean) {

        var width = toolbar.width
        width -= resources.getDimensionPixelSize(R.dimen.action_button_min_width) / 2

        val cx = width
        val cy = toolbar.height / 2

        val anim: Animator
        anim = if (isShow)
            ViewAnimationUtils.createCircularReveal(searchToolbar, cx, cy, 0f, width.toFloat())
        else
            ViewAnimationUtils.createCircularReveal(searchToolbar, cx, cy, width.toFloat(), 0f)

        anim.duration = 250L

        // make the view invisible when the animation is done
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!isShow) {
                    super.onAnimationEnd(animation)
                    searchToolbar.visibility = View.INVISIBLE
                }
            }
        })

        // make the view visible and start the animation
        if (isShow) {
            searchToolbar.visibility = View.VISIBLE
            notifySearchExpanded()
        } else {
            notifySearchCollapsed()
        }

        // start the animation
        anim.start()
    }

    fun onBackPressed(): Boolean {
        val isInSearchMode = searchToolbar.visibility == View.VISIBLE
        if (!isInSearchMode) {
            return false
        }
        collapse()
        searchMenuItem.collapseActionView()
        return true
    }

    fun setOnSearchQueryChangedListener(onSearchQueryChangedListener: OnSearchQueryChangedListener) {
        this.onSearchQueryChangedListener = onSearchQueryChangedListener
    }

    private fun notifySearchCollapsed() {
        if (this.onSearchQueryChangedListener != null) {
            this.onSearchQueryChangedListener!!.onSearchCollapsed()
        }
    }

    private fun notifySearchExpanded() {
        if (this.onSearchQueryChangedListener != null) {
            this.onSearchQueryChangedListener!!.onSearchExpanded()
        }
    }

    private fun notifySearchQueryChanged(q: String) {
        if (this.onSearchQueryChangedListener != null) {
            this.onSearchQueryChangedListener!!.onSearchQueryChanged(q)
        }
    }

    private fun notifySearchSubmitted(q: String) {
        if (this.onSearchQueryChangedListener != null) {
            this.onSearchQueryChangedListener!!.onSearchSubmitted(q)
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        if (!currentQuery.equals(s.toString(), ignoreCase = true)) {
            currentQuery = s.toString()
            notifySearchQueryChanged(currentQuery)
        }
    }

    private fun collapse() {
        isSearchExpanded = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circleReveal(false)
        } else
            searchToolbar.visibility = View.GONE
    }

    private fun expand() {
        isSearchExpanded = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circleReveal(true)
        } else {
            searchToolbar.visibility = View.VISIBLE
        }
    }

    /**
     * Expands the toolbar (Without animation) and update the search with a given query
     */
    fun expandAndSearch(query: String) {
        searchToolbar.visibility = View.VISIBLE
        searchMenuItem.expandActionView()
        txtSearch.setText(query)
    }

    fun setTitle(title: String) {
        toolbar.title = title
        toolbar.invalidate()
    }

    fun setTitleTextColor(color: Int) {
        toolbar.setTitleTextColor(color)
    }

    fun setToolbarSubtitleColor(toolbarSubtitleColor: Int) {
        toolbar.setSubtitleTextColor(toolbarSubtitleColor)
    }

    fun setSearchBackgroundColor(searchBackgroundColor: Int) {
        searchToolbar.setBackgroundColor(searchBackgroundColor)
    }

    fun setSearchTextColor(color: Int) {
        txtSearch.setTextColor(color)
    }

    fun setSearchHintColor(color: Int) {
        txtSearch.setHintTextColor(color)
    }

    fun setSearchHint(hint: String) {
        txtSearch.hint = hint
    }

    fun setToolbarSubtitle(toolbarSubtitle: String) {
        toolbar.subtitle = toolbarSubtitle
    }

    interface OnSearchQueryChangedListener {

        fun onSearchCollapsed()

        fun onSearchQueryChanged(query: String)

        fun onSearchExpanded()

        fun onSearchSubmitted(query: String)
    }

}
