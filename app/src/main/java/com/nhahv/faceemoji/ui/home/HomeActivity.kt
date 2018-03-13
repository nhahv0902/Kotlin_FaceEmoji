package com.nhahv.faceemoji.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.style.ImageSpan
import android.util.Base64
import android.util.Log
import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.google.gson.Gson
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.nhahv.faceemoji.R
import com.nhahv.faceemoji.data.local.SharePrefs
import com.nhahv.faceemoji.databinding.*
import com.nhahv.faceemoji.networking.receiver.NetworkReceiver
import com.nhahv.faceemoji.ui.BaseActivity
import com.nhahv.faceemoji.utils.FileUtil.createImageFile
import com.nhahv.faceemoji.utils.FileUtil.dpToPx
import com.nhahv.faceemoji.utils.PREF_YOU_MOJI
import com.nhahv.faceemoji.utils.hideSoftKeyboard
import com.nhahv.faceemoji.utils.showSoftKeyboard
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_home.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

@RuntimePermissions
class HomeActivity : BaseActivity(), IHomeListener, ColorPickerDialogListener, NetworkReceiver.NetworkReceiverListener {

    companion object {
        val SHARE_IMAGE = 1123
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var detector: FaceDetector
    private var mNetworkReceiver: NetworkReceiver? = null

    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = obtainViewModel(HomeViewModel::class.java)
        loadPictureWithPermissionCheck()

        viewModel.listener = this
        mNetworkReceiver = NetworkReceiver()
        mNetworkReceiver?.setNetworkListener(this)

        val binding: ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.viewModel = viewModel
//        editText.typeface = ResourcesCompat.getFont(this, R.font.sans_serif)
        events()

        detector = FaceDetector.Builder(applicationContext)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build()

        registerNetworkBroadcastForNougat()
        editText.textSize = 28f

    }

    private fun registerNetworkBroadcastForNougat() {
        registerReceiver(mNetworkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }


    private fun events() {
        camera.setOnClickListener {
            openCameraWithPermissionCheck()
            library.collapse()
        }

        gallery.setOnClickListener {
            startPickPicture()
            library.collapse()

        }

        share.setOnClickListener {
            shareWithPermissionCheck()
        }

        layoutYour.setOnClickListener {
            hideSoftKeyboard()
            viewModel.updateYouEmo()
        }
        layoutMore.setOnClickListener {
            hideSoftKeyboard()
            viewModel.updateEmo()
        }
        layoutSticker.setOnClickListener {
            viewModel.updateSticker()
        }

        font.setOnClickListener {
            val build = AlertDialog.Builder(this)
            val binding: BottomFontsBinding = BottomFontsBinding.inflate(layoutInflater, null, false)
            build.setView(binding.root)
            val dialog = build.create()

            binding.sansSerif.setOnClickListener {
                editText.typeface = ResourcesCompat.getFont(this, R.font.sans_serif)
                dialog.dismiss()
                showSoftKeyboard(editText)
            }
            binding.trebutchetMS.setOnClickListener {
                editText.typeface = ResourcesCompat.getFont(this, R.font.trebutchet)
                dialog.dismiss()
                showSoftKeyboard(editText)
            }
            binding.comicSans.setOnClickListener {
                editText.typeface = ResourcesCompat.getFont(this, R.font.comic)
                dialog.dismiss()
            }
            binding.caviarDreams.setOnClickListener {
                editText.typeface = ResourcesCompat.getFont(this, R.font.caaviar)
                dialog.dismiss()
                showSoftKeyboard(editText)
            }
            binding.pacifico.setOnClickListener {
                editText.typeface = ResourcesCompat.getFont(this, R.font.pacifico)
                dialog.dismiss()
                showSoftKeyboard(editText)
            }
            dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.show()
        }

        sizeFont.setOnClickListener {
            val build = AlertDialog.Builder(this)
            val binding: DialogFontSizeBinding = DialogFontSizeBinding.inflate(layoutInflater, null, false)
            build.setView(binding.root)
            val dialog = build.create()

            binding.normal.setOnClickListener {
                editText.textSize = 22f
                dialog.dismiss()
                showSoftKeyboard(editText)
            }
            binding.large.setOnClickListener {
                editText.textSize = 28f
                dialog.dismiss()
                showSoftKeyboard(editText)
            }
            binding.huge.setOnClickListener {
                editText.textSize = 32f
                dialog.dismiss()
                showSoftKeyboard(editText)
            }
            dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.show()
        }

        colorFont.setOnClickListener {
            hideSoftKeyboard()
            ColorPickerDialog.newBuilder()
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setAllowPresets(true)
                    .setDialogId(0)
                    .setColor(ContextCompat.getColor(this, R.color.color_background))
                    .setShowAlphaSlider(true)
                    .show(this)
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
        dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.setContentView(binding.root)
        dialog.show()
    }


    override fun setImagePicture(uri: Uri?) {
        uri?.let {
            //            detectFace(uri)
            CropImage.activity(uri)
                    .setAspectRatio(1, 1)
                    .setOutputCompressQuality(100)
                    .setRequestedSize(400, 400)
                    .start(this)
//            val outputUri = Uri.fromFile(File(cacheDir, "cropped"))
//            Crop.of(uri, outputUri).asSquare().start(this)
        }
    }

    override fun editAddPicture(path: String) {
        showSoftKeyboard(editText)
        try {
            if (path.contains("img/") or path.contains("you/") or path.contains("sticker/")) {
                insert("(", Drawable.createFromStream(assets.open(path), null))
            } else {
                insert("(", Drawable.createFromPath(path))
            }
        } catch (ex: IOException) {
            toast("Not add emoji")
        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun loadPicture() {
        viewModel.loadYouEMO()
    }


    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun share() {

        val pathFile = viewModel.createFileImageFromBitmap(loadBitmapFromEdit())
        pathFile?.let {
            val photoURI = FileProvider.getUriForFile(this, packageName, it)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, photoURI)
            startActivityForResult(Intent.createChooser(intent, "share Image"), SHARE_IMAGE)
        }

//        val bundle = Bundle()
//        bundle.putString("image", pathFile)
//        switchActivity<EmojiActivity>(bundle)
    }


    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun openCamera() {
        val photoFile = createImageFile("Face")
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

    /**
     *  Event Text watcher
     *
     */
    private fun insert(emoticon: String, drawable: Drawable) {
        val width = dpToPx(editText.context, 84f).toInt()
        drawable.setBounds(0, 0, width, width)
        val span = ImageSpan(drawable, 0)
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val message: Editable = editText.editableText
        message.replace(start, end, emoticon)
        message.setSpan(span, start, emoticon.length + start, 33)
        message.append(" ")
    }

    /**
     *
     * Load bitmap
     * */

    @SuppressLint("SetTextI18n")
    private fun loadBitmapFromEdit(): Bitmap {

        val gText = "@youmoji"
        editText.isCursorVisible = false
        editText.setBackgroundColor(Color.WHITE)
        editText.setSelection(editText.text.length)

        editText.gravity = Gravity.CENTER
        editText.height = editText.height
        val width = editText.width
        val height: Int = editText.height

        editText.selectionEnd


        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        editText.setPadding(editText.paddingLeft, editText.paddingTop, editText.paddingRight, editText.paddingBottom)
        editText.layout(editText.left, editText.top, editText.right, editText.bottom)
        editText.draw(c)


        val y = bitmap.height - 50
        var bitmapConfig: android.graphics.Bitmap.Config? = bitmap.config
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        val bitmaptemp = bitmap.copy(bitmapConfig, true)
        val canvas = Canvas(bitmaptemp)
        // new antialised Paint
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        // text color - #3D3D3D
        paint.color = ContextCompat.getColor(this, R.color.color_text_emoji)
        // text size in pixels
        paint.textSize = 50f
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)
        paint.textAlign = Paint.Align.RIGHT

        // draw text to the Canvas center
        val bounds = Rect()

        paint.getTextBounds(gText, 0, gText.length, bounds)
        canvas.drawText(gText, width.toFloat() - 180, y.toFloat(), paint)

        editText.isCursorVisible = true
        editText.setBackgroundResource(R.drawable.bg_editor)

        editText.gravity = Gravity.START
        editText.setPadding(editText.paddingLeft, editText.paddingTop, editText.paddingRight, editText.paddingBottom)

//        editText.height = editText.height
//        return Bitmap.createScaledBitmap(bitmaptemp, (400 * width) / height, 400, true)
        return Bitmap.createScaledBitmap(bitmaptemp, width, height, true)
    }

    private fun detectFace(uri: Uri) {
        val bitmap: Bitmap? = decodeBitmapUri(this, uri)
        if (bitmap != null) {
            val frame: Frame = Frame.Builder().setBitmap(bitmap).build()
            val faces: SparseArray<Face> = detector.detect(frame)
            if (faces.size() == 0) {
                Toast.makeText(this, "No detect face", Toast.LENGTH_SHORT).show()
            } else {
                CropImage.activity(uri)
                        .setAspectRatio(5, 6)
                        .setRequestedSize(250, 300)
                        .start(this)
            }
        } else {
            Toast.makeText(this, "Could not set up the detector!", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(FileNotFoundException::class)
    private fun decodeBitmapUri(ctx: Context, uri: Uri): Bitmap {
        val targetW = 600
        val targetH = 600
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeStream(ctx.contentResolver.openInputStream(uri), null, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        val scaleFactor = Math.min(photoW / targetW, photoH / targetH)
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        return BitmapFactory.decodeStream(ctx.contentResolver
                .openInputStream(uri), null, bmOptions)
    }

    override fun onDestroy() {
        super.onDestroy()
        detector.release()
        mNetworkReceiver?.let {
            unregisterReceiver(it)
        }
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        if (dialogId == 0) {
            editText.setTextColor(color)
        }
    }

    override fun onDialogDismissed(dialogId: Int) {}

    override fun showDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this)
            progressDialog!!.setMessage("Processing")
            progressDialog!!.setCancelable(false)
        }
        if (!progressDialog!!.isShowing) progressDialog!!.show()
    }

    override fun hideDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) progressDialog!!.dismiss()
    }

    override fun removePicture(item: String, position: Int) {
        val mBottomSheetDialog = BottomSheetDialog(this)
        val binding: BottomRemoteImageBinding = BottomRemoteImageBinding.inflate(layoutInflater, null)
        mBottomSheetDialog.setContentView(binding.root)
        binding.delete.setOnClickListener {
            if (item.contains("you/")) {
                viewModel.picturesAsset.remove(item)
                val json = Gson().toJson(viewModel.picturesAsset)
                SharePrefs.getInstance(this).put(PREF_YOU_MOJI, json)
                Log.d("TAG", json)
            } else {
                val file = File(item)
                if (file.exists()) {
                    val deleted = file.delete()
                    Log.d("TAG", "$deleted")
                }
            }
            viewModel.youPictures.remove(item)
            viewModel.pictures.clear()
            viewModel.pictures.addAll(viewModel.youPictures)
            viewModel.adapter.notifyChange()
            mBottomSheetDialog.dismiss()
        }
        mBottomSheetDialog.show()
    }

    override fun onNetworkConnectChange(isConnected: Boolean) {
        viewModel.isNoNetwork.set(isConnected)
    }

    override fun showBottomSheetLibrary() {
        showBottomSheetGallery()
    }

    override fun shareSticker(item: String) {

        val bm = BitmapFactory.decodeStream(assets.open(item))

        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos) //bm is the bitmap object
        val base64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
        val pathFile = createImageFile("Face")
        pathFile?.let {

            val photoURI = FileProvider.getUriForFile(this, packageName, it)
            viewModel.convertBase64ToFileImageCache(it, base64)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, photoURI)
            startActivity(Intent.createChooser(intent, "share Image"))
        }
    }

    private fun createFile(path: String) {

    }

    override fun removeText() {
        editText.setText("")
    }

    private fun showBottomSheetGallery() {
        val bottomSheet = BottomSheetDialog(this)
        val binding = BottomLibraryBinding.inflate(layoutInflater, null, false)
        bottomSheet.setContentView(binding.root)
        binding.textCamera.setOnClickListener {
            openCameraWithPermissionCheck()
            bottomSheet.dismiss()
        }
        binding.camera.setOnClickListener {
            openCameraWithPermissionCheck()
            bottomSheet.dismiss()

        }
        binding.gallery.setOnClickListener {
            startPickPicture()
            bottomSheet.dismiss()

        }
        binding.textGallery.setOnClickListener {
            startPickPicture()
            bottomSheet.dismiss()

        }
        bottomSheet.show()
    }

    override fun showToastRemoveEmo() {
        msgRemoveEmo.visibility = View.VISIBLE
        Handler().postDelayed({
            msgRemoveEmo.visibility = View.GONE
        }, 6000)
    }
}
