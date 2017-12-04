package com.nhahv.faceemoji.utils

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.nhahv.faceemoji.data.model.FaceEmoji
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
    Log.d("TAG", "imageFile = $imageFile")
    imageFile?.let {
        if (it.contains("img/") or it.contains("you/")) {
            GlideApp.with(view.context).load(Uri.parse("file:///android_asset/$it")).error(error).into(view)
        } else {
            GlideApp.with(view.context).load(File(it)).error(error).into(view)
        }
    }
}

@BindingAdapter(value = *arrayOf("imageAssetFile", "imageAssetFileError"), requireAll = false)
fun bindAssetImageFile(view: ImageView, imageFile: String?, error: Drawable) {
    Log.d("TAG", "$imageFile")
    imageFile?.let {
        GlideApp.with(view.context).load(Uri.parse("file:///android_asset/$it")).error(error).into(view)
    }
}