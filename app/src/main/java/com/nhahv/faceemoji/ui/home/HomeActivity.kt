package com.nhahv.faceemoji.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.FileProvider
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.Menu
import android.view.MenuItem
import com.nhahv.faceemoji.R
import com.nhahv.faceemoji.databinding.ActivityHomeBinding
import com.nhahv.faceemoji.databinding.DialogLibraryBinding
import com.nhahv.faceemoji.ui.BaseActivity
import com.nhahv.faceemoji.utils.GlideApp
import kotlinx.android.synthetic.main.activity_home.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.FileNotFoundException
import java.io.InputStream

@RuntimePermissions
class HomeActivity : BaseActivity(), OnOpenDialogLibrary {

    companion object {
        const val REQUEST_PICK_IMAGE = 1
        const val REQUEST_IMAGE_CAPTURE = 2
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = obtainViewModel(HomeViewModel::class.java)
        viewModel.onDialogLibrary = this
        val binding: ActivityHomeBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_home)
        binding.viewModel = viewModel
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
        addImageBetweentext(Drawable.createFromPath(path))
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
            addImageBetweentext(drawable)
        }


    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val photoFile = viewModel.createImageFile()
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

    private fun startPickPicture() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
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

    private fun addImageBetweentext(drawable: Drawable) {
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

}
