package com.nhahv.faceemoji.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by nhahv on 11/3/17.
 */
object FileUtil {

    fun getAlbumDir(folder: String = "Emoji"): File? {
        var storageDir: File? = null
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folder)
            if (!storageDir.mkdirs() && !storageDir.exists()) {
                log("failed to create directory")
                return null
            }
        } else {
            log("External storage is not mounted READ/WRITE.")
        }
        return storageDir
    }

    fun log(message: String) {
        Log.d("TAG", message)
    }

    fun createImageFile(folder: String = "Emoji"): File? {
        return try {
            val nameFile = "IMG_" + SimpleDateFormat("yyyyMMdd_HHmmss_",
                    Locale.getDefault()).format(Date())
            val albumFile = getAlbumDir(folder)
            File.createTempFile(nameFile, ".png", albumFile)
        } catch (ex: IOException) {
            null
        }
    }

    fun dpToPx(context: Context, valueInDp: Float): Float {
        return TypedValue.applyDimension(1, valueInDp, context.resources.displayMetrics)
    }

    fun loadPictures(): ArrayList<String> {
        val pictures = ArrayList<String>()
        val folder = File("/storage/emulated/0/Pictures/Emoji/")
        if (!folder.exists()) return pictures
        val listOfFiles = folder.listFiles()
        listOfFiles
                .filter { it.isFile && (it.path.endsWith(".png") or it.path.endsWith(".jpg")) }
                .mapTo(pictures) { it.path }
        pictures.sortDescending()
        return pictures
    }

    fun createFileFromCache(context: Context, nameFile: String): File =
            File.createTempFile("", ".png", context.cacheDir)
}