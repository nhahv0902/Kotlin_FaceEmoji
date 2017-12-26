package com.nhahv.faceemoji.utils

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.nhahv.faceemoji.R
import java.io.File

/**
 * Created by nhahv0902 on 10/19/17.
 */

@BindingAdapter(value = *arrayOf("imageUrl", "imageError"), requireAll = false)
fun bindImage(view: ImageView, source: Int?, error: Drawable) {
    source?.let {
        view.setImageResource(source)
    }
}

@BindingAdapter("selected")
fun bindSelected(view: View, isSelected: Boolean) {
    view.isSelected = isSelected
}

@BindingAdapter(value = *arrayOf("imageFile", "imageFileError"), requireAll = false)
fun bindImageFile(view: ImageView, imageFile: String?, error: Drawable) {
    imageFile?.let {
        when {
            it.isEmpty() -> Glide.with(view.context).load(R.drawable.create).into(view)
            it.contains("img/") or it.contains("you/") or it.contains("sticker/") -> GlideApp.with(view.context).load(Uri.parse("$START_ASSET$it")).error(error).into(view)
            else -> GlideApp.with(view.context).load(File(it)).error(error).into(view)
        }
    }
}

@BindingAdapter(value = *arrayOf("imageAssetFile", "imageAssetFileError"), requireAll = false)
fun bindAssetImageFile(view: ImageView, imageFile: String?, error: Drawable) {
    imageFile?.let {
        GlideApp.with(view.context).load(Uri.parse("$START_ASSET$it")).error(error).into(view)
    }
}