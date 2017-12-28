package com.nhahv.faceemoji.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import com.nhahv.faceemoji.R
import com.nhahv.faceemoji.databinding.ActivityChoseBinding
import com.nhahv.faceemoji.networking.NetworkService
import com.nhahv.faceemoji.networking.NetworkService.Companion.BASE_POINT_URL
import com.nhahv.faceemoji.ui.BaseActivity
import com.nhahv.faceemoji.ui.home.HomeActivity
import com.nhahv.faceemoji.utils.FileUtil.createImageFile
import org.jetbrains.anko.doAsync
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.net.URL
import java.util.*
import kotlin.concurrent.timerTask

@RuntimePermissions
class ChoseActivity : BaseActivity() {

    private lateinit var viewModel: ChoseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityChoseBinding = DataBindingUtil.setContentView(this, R.layout.activity_chose)

        viewModel = obtainViewModel(ChoseViewModel::class.java)
        binding.viewModel = viewModel


        Timer().schedule(timerTask {
            switchActivity<HomeActivity>()
            finish()
        }, 1000)
//        camera.setOnClickListener { openCameraWithPermissionCheck() }
//        gallery.setOnClickListener { startPickPicture() }

        doAsync {
            val result = URL(BASE_POINT_URL).readText()
            NetworkService.END_POINT_URL = result
        }
    }

    private fun shareImageViaIntent() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File("/storage/emulated/0/DCIM/Camera/IMG_20171023_184955_HDR.jpg")))
        startActivity(Intent.createChooser(intent, "share Image"))
    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun openCamera() {
        val photoFile = createImageFile("Face")
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
