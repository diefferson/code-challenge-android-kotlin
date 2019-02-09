package com.arctouch.codechallenge.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultAsync<T> private constructor(action: suspend () -> T, scope: CoroutineScope) {

    internal  var onSuccess : (T) -> Unit = {}
    internal  var onError : (e: Throwable) -> Unit = {}

    companion object {
        fun <T> with(action: suspend () -> T, scope: CoroutineScope) : ResultAsync<T> {
            return  ResultAsync(action, scope)
        }
    }

    init {
        scope.launch {
            try {
                val result = action()
                withContext(Dispatchers.Main){
                    onSuccess(result)
                }
            }catch (e:Throwable){
                withContext(Dispatchers.Main){
                    onError(e)
                }
            }
        }
    }
}

fun <T> ResultAsync<T>.onFailure(action: (exception: Throwable) -> Unit): ResultAsync<T> {
    this.onError =  action
    return this
}


fun <T> ResultAsync<T>.onSuccess(action: (value: T) -> Unit): ResultAsync<T> {
    this.onSuccess = action
    return this
}

fun <T> CoroutineScope.asyncCatching(action: suspend () -> T): ResultAsync<T> {
    return ResultAsync.with(action, this)
}
