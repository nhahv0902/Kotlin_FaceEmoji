package com.nhahv.faceemoji.ui.home

import android.net.Uri

/**
 * Created by nhahv on 10/19/17.
 */

interface OnOpenDialogLibrary {
    fun openDialog()

    fun setImagePicture(uri: Uri?)

    fun setImagePicture(path: String?)
}
