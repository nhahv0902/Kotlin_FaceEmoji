package com.nhahv.faceemoji.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import com.nhahv.faceemoji.R
import com.nhahv.faceemoji.databinding.ActivityChoseBinding
import com.nhahv.faceemoji.ui.BaseActivity
import com.nhahv.faceemoji.utils.FileUtil.createImageFile
import kotlinx.android.synthetic.main.activity_chose.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File

@RuntimePermissions
class ChoseActivity : BaseActivity() {

    private lateinit var viewModel: ChoseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityChoseBinding = DataBindingUtil.setContentView(this, R.layout.activity_chose)

        viewModel = obtainViewModel(ChoseViewModel::class.java)
        binding.viewModel = viewModel


        camera.setOnClickListener { openCameraWithPermissionCheck() }
        gallery.setOnClickListener { startPickPicture() }
    }

    private fun shareImageViaIntent(){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File("/storage/emulated/0/DCIM/Camera/IMG_20171023_184955_HDR.jpg")))
        startActivity(Intent.createChooser(intent, "share Image"))
    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun openCamera() {
        val photoFile = createImageFile()
        viewModel.currentPath = photoFile?.path
        startCamera(photoFile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}
