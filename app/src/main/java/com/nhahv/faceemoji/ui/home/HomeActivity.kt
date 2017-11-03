package com.nhahv.faceemoji.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.nhahv.faceemoji.R
import com.nhahv.faceemoji.databinding.ActivityHomeBinding
import com.nhahv.faceemoji.databinding.DialogLibraryBinding
import com.nhahv.faceemoji.ui.BaseActivity
import com.nhahv.faceemoji.utils.FileUtil.createImageFile
import com.nhahv.faceemoji.utils.GlideApp
import com.nhahv.faceemoji.utils.TextDrawable
import kotlinx.android.synthetic.main.activity_home.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream

@RuntimePermissions
class HomeActivity : BaseActivity(), OnOpenDialogLibrary {

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = obtainViewModel(HomeViewModel::class.java)
        viewModel.onDialogLibrary = this
        val binding: ActivityHomeBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_home)
        binding.viewModel = viewModel


        var bundle = intent.extras
        bundle?.let {

            val path = bundle.getString("result")
            GlideApp.with(this).load(File(path)).into(imageView)
        }

        picture.setOnClickListener {
            val temp = shareImage()
            temp?.let {
                GlideApp.with(this).load(Uri.fromFile(it)).into(picture)
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(it))
                startActivity(Intent.createChooser(intent, "share Image"))
                return@let
            }

            Log.d("TAg", "null")

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun openDialog() {
        val dialog = BottomSheetDialog(this)
        val binding: DialogLibraryBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.dialog_library, null, false)
        binding.imageCamera.setOnClickListener {
            openCameraWithPermissionCheck()
            dialog.dismiss()
        }
        binding.textCamera.setOnClickListener {
            openCameraWithPermissionCheck()
            dialog.dismiss()
        }
        binding.imageGallery.setOnClickListener {
            startPickPicture()
            dialog.dismiss()
        }
        binding.textGallery.setOnClickListener {
            startPickPicture()
            dialog.dismiss()
        }
        dialog.setContentView(binding.root)
        dialog.show()
    }

    override fun setImagePicture(path: String?) {
        GlideApp.with(this).load(path).into(picture)
        addImageBetweenText(Drawable.createFromPath(path))
    }

    override fun setImagePicture(uri: Uri?) {
        GlideApp.with(this).load(uri).into(picture)
        val drawable = try {
            val inputStream: InputStream = contentResolver.openInputStream(uri)
            Drawable.createFromStream(inputStream, uri.toString())

        } catch (e: FileNotFoundException) {
            null
        }
        drawable?.let {
            addImageBetweenText(drawable)
            imageView.setImageDrawable(convertTextToImage(editText.text.toString()))
            Log.d("TAG", editText.text.toString())
        }


    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun openCamera() {
        val photoFile = createImageFile()
        viewModel.currentPath = photoFile?.path
        startCamera(photoFile)
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onResultFromActivity(requestCode, resultCode, data)
    }

    private fun addImageBetweenText(drawable: Drawable) {
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

        var selectionCursor = editText.selectionStart
        editText.text.insert(selectionCursor, ".")
        selectionCursor = editText.selectionStart

        val builder = SpannableStringBuilder(editText.text)
        builder.setSpan(ImageSpan(drawable), selectionCursor - ".".length, selectionCursor,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        editText.text = builder
        editText.setSelection(selectionCursor)
    }

    private fun convertTextToImage(value: String): TextDrawable {

        val text = TextDrawable(this)
        text.text = value
        return text
    }

    private fun shareImage(): File? {
        return try {
            imageView.isDrawingCacheEnabled = true
            imageView.buildDrawingCache(true)
            val bitmap = imageView.drawingCache
            val file = createImageFile()
            val outPut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outPut)
            outPut.flush()
            outPut.close()
            imageView.isDrawingCacheEnabled = false
            imageView.buildDrawingCache(false)
            file
        } catch (ex: Exception) {
            null
        }
    }
}
