package com.nhahv.faceemoji.data.loadimage

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore

/**
 * Created by nhahv on 11/11/17.
 */
class LoaderPicture(private val context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE: LoaderPicture? = null

        fun getInstance(context: Context) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: LoaderPicture(context).also { INSTANCE = it }
                }
    }

    val pictures = ArrayList<String>()

    private val SELECTION_ARGS = arrayOf("image/jpeg", "image/png", "Face Emoji")
    private val MIME_TYPE = MediaStore.Images.Media.MIME_TYPE
    private val SELECTION = "($MIME_TYPE=? or $MIME_TYPE=?) and ${MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME} =?"
    private val IMAGE_PROJECTION = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED
    )

    init {
        loadPictures()
    }

    private fun loadPictures() {
        val cursor: Cursor? = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, SELECTION,
                SELECTION_ARGS, IMAGE_PROJECTION[1] + " DESC", null)

        if (cursor == null || !cursor.moveToFirst()) return
        val indexPath = cursor.getColumnIndex(IMAGE_PROJECTION[0])
        while (!cursor.isAfterLast) {
            pictures.add(cursor.getString(indexPath))
            cursor.moveToNext()
        }
        cursor.close()
    }
}