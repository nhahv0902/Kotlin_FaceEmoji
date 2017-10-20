package com.nhahv.faceemoji.ui.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import com.nhahv.faceemoji.R
import com.nhahv.faceemoji.data.model.ItemNavigation
import com.nhahv.faceemoji.data.model.TypeAction.ADJUST
import com.nhahv.faceemoji.data.model.TypeAction.BRUSH
import com.nhahv.faceemoji.data.model.TypeAction.FILTER
import com.nhahv.faceemoji.data.model.TypeAction.FOCUS
import com.nhahv.faceemoji.data.model.TypeAction.FRAME
import com.nhahv.faceemoji.data.model.TypeAction.LIBRARY
import com.nhahv.faceemoji.data.model.TypeAction.OVERLAY
import com.nhahv.faceemoji.data.model.TypeAction.STICKER
import com.nhahv.faceemoji.data.model.TypeAction.TEXT
import com.nhahv.faceemoji.data.model.TypeAction.TRANSFORM
import com.nhahv.faceemoji.ui.BaseRecyclerAdapter
import com.nhahv.faceemoji.ui.BaseViewModel
import com.nhahv.faceemoji.ui.emoji.EmojiActivity
import com.nhahv.faceemoji.ui.home.HomeActivity.Companion.REQUEST_IMAGE_CAPTURE
import com.nhahv.faceemoji.ui.home.HomeActivity.Companion.REQUEST_PICK_IMAGE
import com.nhahv.faceemoji.utils.Navigator
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by nhahv0902 on 10/17/17.
 *
 */
class HomeViewModel(private val navigator: Navigator) : BaseViewModel(), HomeListener {
    lateinit var onDialogLibrary: OnOpenDialogLibrary

    override fun onClick(item: ItemNavigation, position: Int) {
        mNavigatorItem
            .filter { it != item }
            .forEach { it.isActive = false }
        item.isActive = true
        adapterNavigator.get().notifyDataSetChanged()
        when (item.typeAction) {
            LIBRARY -> {
                onDialogLibrary.let { it.openDialog() }
            }
            TEXT -> {
                navigator.startActivity<EmojiActivity>()
            }
        }
    }

    private val mNavigatorItem: ArrayList<ItemNavigation> = ArrayList()
    val adapterNavigator: ObservableField<BaseRecyclerAdapter<ItemNavigation>>
        = ObservableField(BaseRecyclerAdapter(mNavigatorItem, this, R.layout.item_bottom_navigator))

    init {
        mNavigatorItem.add(
            ItemNavigation(R.drawable.library_status, name = "Library", typeAction = LIBRARY))
        mNavigatorItem.add(
            ItemNavigation(R.drawable.transform_status, name = "Transform", typeAction = TRANSFORM))
        mNavigatorItem.add(
            ItemNavigation(R.drawable.filter_status, name = "Filter", typeAction = FILTER))
        mNavigatorItem.add(
            ItemNavigation(R.drawable.sticker_status, name = "Sticker", typeAction = STICKER))
        mNavigatorItem.add(
            ItemNavigation(R.drawable.adjust_status, name = "Adjust", typeAction = ADJUST))
        mNavigatorItem.add(
            ItemNavigation(R.drawable.text_status, name = "Text", typeAction = TEXT))
        mNavigatorItem.add(
            ItemNavigation(R.drawable.overlay_status, name = "Overlay", typeAction = OVERLAY))
        mNavigatorItem.add(
            ItemNavigation(R.drawable.frame_status, name = "Frame", typeAction = FRAME))
        mNavigatorItem.add(
            ItemNavigation(R.drawable.brush_status, name = "Brush", typeAction = BRUSH))
        mNavigatorItem.add(
            ItemNavigation(R.drawable.focus_status, name = "Focus", typeAction = FOCUS))
    }

    fun onResultFromActivity(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK) return
        when (requestCode) {
            REQUEST_PICK_IMAGE -> {
                print(data)
                onDialogLibrary.let { it.setImagePicture(data?.data) }
            }
            REQUEST_IMAGE_CAPTURE -> {
                handleImageCapture()
            }
            else -> log("null")
        }
    }

    private fun handleImageCapture() {
        mCurrentPath?.let {
            galleryAddPicture()
            onDialogLibrary.let {
                it.setImagePicture(mCurrentPath)
            }
            print(mCurrentPath)
            return
        }
        log("Error file")
    }

    private var mCurrentPath: String? = null

    fun createImageFile(): File? {
        return try {
            val nameFile = "IMG_" + SimpleDateFormat("yyyyMMdd_HHmmss_",
                Locale.getDefault()).format(Date())
            val albumFile = getAlbumDir()
            val imageFile = File.createTempFile(nameFile, ".jpg", albumFile)
            mCurrentPath = imageFile.absolutePath
            imageFile
        } catch (ex: IOException) {
            null
        }
    }

    private fun getAlbumDir(): File? {
        var storageDir: File? = null
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            storageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Face Emoji")
            if (!storageDir.mkdirs() && !storageDir.exists()) {
                log("failed to create directory")
                return null
            }
        } else {
            log("External storage is not mounted READ/WRITE.");
        }
        return storageDir
    }

    private fun galleryAddPicture() {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(File(mCurrentPath))
        navigator.context.sendBroadcast(intent)
    }



}