package com.arctouch.codechallenge.util

import android.app.Activity
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseViewHolder

fun Activity.inflateView(@LayoutRes resource: Int, viewGroup: View, attachToRoot : Boolean = false): View =
        layoutInflater.inflate(resource, viewGroup.parent as ViewGroup, attachToRoot)


fun Activity.hideKeyboard() :Boolean {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = this.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
   return imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun ImageView.loadUrlImage(url:String?, isRounded:Boolean = false, @DrawableRes placeHolder:Int? = null){

    if(url!=null){
        var image = Glide.with(this).load(url)

        if(placeHolder!= null){
            image = image.apply(RequestOptions.placeholderOf(placeHolder))
        }

        if(isRounded){
            image = image.apply(RequestOptions.circleCropTransform())
        }

        image.into(this)
    }
}

fun BaseViewHolder.loadUrlImage(@IdRes viewId: Int, url:String?,
                                isRounded:Boolean = false,
                                @DrawableRes placeHolder:Int? = null): BaseViewHolder {
    if(url!=null){
        val view = getView<ImageView>(viewId)
        view.loadUrlImage(url, isRounded, placeHolder)
    }
    return this
}

fun String.formatDate() : String{
    return this.split("-").reversed().joinToString(separator = "/")
}
