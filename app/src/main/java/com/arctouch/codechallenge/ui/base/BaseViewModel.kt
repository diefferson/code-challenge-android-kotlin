package com.arctouch.codechallenge.ui.base

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.StringRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class BaseViewModel : ViewModel(), CoroutineScope{

    val viewStateEvent: SingleLiveEvent<ViewState> = SingleLiveEvent()

    private val executionJob: Job  by lazy { Job() }

    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Default + executionJob
    }

    fun showLoading(){
        viewStateEvent.postValue(ViewState.Loading(true))
    }

    fun dismissLoading(){
        viewStateEvent.postValue(ViewState.Loading(false))
    }

    fun showError(@StringRes string:Int){
        viewStateEvent.postValue(ViewState.Error(string))
    }

    /**
     * Expose the LiveData so the UI can observe it.
     */
    fun getViewStateObservable(): LiveData<ViewState> {
        return viewStateEvent
    }

    override fun onCleared() {
        super.onCleared()
        executionJob.cancel()
    }
}