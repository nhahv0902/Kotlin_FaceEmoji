package com.nhahv.faceemoji.utils

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView

/**
 * Created by nhahv0902 on 10/19/17.
 */

@BindingAdapter(value = *arrayOf("imageUrl", "imageError"), requireAll = false)
fun bindImage(view: ImageView, source: Int?, error: Drawable) {
//    GlideApp.with(view.context).load(source).error(error).into(view)
    if (source == null) {
        Log.d("TAG", "null")
    } else {
        Log.d("TAG", "not null")
    }

    source?.let {
        view.setImageResource(source)
    }
}