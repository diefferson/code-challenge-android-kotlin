package com.arctouch.codechallenge.ui.base

import android.support.annotation.StringRes

sealed class ViewState{
     data class Error(@StringRes val message:Int) : ViewState()
     data class Loading(val isLoading:Boolean): ViewState()
}

