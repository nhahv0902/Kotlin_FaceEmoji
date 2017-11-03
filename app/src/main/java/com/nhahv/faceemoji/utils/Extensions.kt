package com.nhahv.faceemoji.utils

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import java.io.File

/**
 * Created by nhahv on 11/3/17.
 */
fun AppCompatActivity.galleryAddPicture(path: String) {
    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    intent.data = Uri.fromFile(File(path))
    sendBroadcast(intent)
}
