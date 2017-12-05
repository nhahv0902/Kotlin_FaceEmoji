package com.nhahv.faceemoji.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.io.File

/**
 * Created by nhahv on 11/3/17.
 */
fun AppCompatActivity.galleryAddPicture(path: String) {
    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    intent.data = Uri.fromFile(File(path))
    sendBroadcast(intent)
}


/**
 * Hides the soft keyboard
 */
fun AppCompatActivity.hideSoftKeyboard() {
    val focusedView = currentFocus
    focusedView?.let {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(focusedView.windowToken, 0)
    }
}

/**
 * Shows the soft keyboard
 */
fun showSoftKeyboard(view: View) {
    val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    view.requestFocus()
    inputMethodManager.showSoftInput(view, 0)
}