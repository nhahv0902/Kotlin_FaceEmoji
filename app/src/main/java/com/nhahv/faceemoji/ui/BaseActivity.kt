package com.nhahv.faceemoji.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import java.io.File

/**
 * Created by nhahv0902 on 10/17/17.
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {


    companion object {
        const val REQUEST_PICK_IMAGE = 1
        const val REQUEST_IMAGE_CAPTURE = 2
    }

    inline fun <reified T : ViewModel> obtainViewModel(
            viewModelClass: Class<T>) = ViewModelProviders.of(this,
            ViewModelFactory.getInstance(this)).get(viewModelClass)

    fun startPickPicture() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    fun startCamera(photoFile: File?) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            if (photoFile != null) {
                var photoURI = Uri.fromFile(photoFile)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    photoURI = FileProvider.getUriForFile(this, packageName, photoFile)
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    inline fun <reified T : AppCompatActivity> switchActivity() {
        startActivity(Intent(this, T::class.java))
    }
}