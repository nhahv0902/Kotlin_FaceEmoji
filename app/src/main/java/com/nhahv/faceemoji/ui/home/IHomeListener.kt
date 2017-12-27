package com.nhahv.faceemoji.ui.home

import android.net.Uri

/**
 * Created by nhahv on 10/19/17.
 */

interface IHomeListener {
    fun openDialog()

    fun setImagePicture(uri: Uri?)

    fun editAddPicture(path: String)

    fun showDialog()

    fun hideDialog()

    fun removePicture(item: String, position: Int)

    fun showBottomSheetLibrary()

    fun shareSticker(item: String)
}
