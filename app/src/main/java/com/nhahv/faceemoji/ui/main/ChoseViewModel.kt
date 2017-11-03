package com.nhahv.faceemoji.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.nhahv.faceemoji.ui.BaseActivity.Companion.REQUEST_IMAGE_CAPTURE
import com.nhahv.faceemoji.ui.BaseActivity.Companion.REQUEST_PICK_IMAGE
import com.nhahv.faceemoji.ui.BaseViewModel
import com.nhahv.faceemoji.ui.home.HomeActivity
import com.nhahv.faceemoji.utils.Navigator
import com.nhahv.faceemoji.utils.galleryAddPicture


/**
 * Created by nhahv on 11/3/17.
 */
class ChoseViewModel(private val navigator: Navigator) : BaseViewModel() {

    var currentPath: String? = null

    val string = "/storage/emulated/0/DCIM/Camera/IMG_20171023_184955_HDR.jpg"
    @SuppressLint("Recycle")
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            REQUEST_PICK_IMAGE -> {
                data?.let {
                    val bundle = Bundle()
                    bundle.putString("result", it.data.lastPathSegment)
                    navigator.startActivity<HomeActivity>(bundle)
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                handleImageCapture()
            }
            else -> log("null")
        }
    }

    private fun handleImageCapture() {
        currentPath?.let {
            navigator.context.galleryAddPicture(it)
            // todo path of image
            print(currentPath)
            return
        }
        log("Error file")
    }
}