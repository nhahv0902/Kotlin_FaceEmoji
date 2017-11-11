package com.nhahv.faceemoji.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.text.Editable
import android.text.Layout
import android.text.style.ImageSpan
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.FaceDetector
import com.nhahv.faceemoji.R
import com.nhahv.faceemoji.databinding.ActivityHomeBinding
import com.nhahv.faceemoji.databinding.DialogLibraryBinding
import com.nhahv.faceemoji.ui.BaseActivity
import com.nhahv.faceemoji.utils.FileUtil.createImageFile
import com.nhahv.faceemoji.utils.FileUtil.dpToPx
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_home.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

@RuntimePermissions
class HomeActivity : BaseActivity(), OnOpenDialogLibrary {

    private lateinit var viewModel: HomeViewModel
    private lateinit var detector: FaceDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = obtainViewModel(HomeViewModel::class.java)
        loadPictureWithPermissionCheck()

        viewModel.onDialogLibrary = this

        val binding: ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.viewModel = viewModel
        events()


        detector = FaceDetector.Builder(applicationContext)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build()

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

        layoutYour.setOnClickListener { viewModel.layoutYour(true) }
        layoutMore.setOnClickListener { viewModel.layoutYour(false) }
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
        path?.let {
            CropImage.activity(Uri.fromFile(File(it))).start(this)
//            detectFace(Uri.fromFile(File(it)))
        }
//        insert("(::", Drawable.createFromPath(path))

    }

    override fun setImagePicture(uri: Uri?) {
        uri?.let {
            CropImage.activity(uri).start(this)
//            detectFace(uri)
        }
//        try {
//            val inputStream: InputStream = contentResolver.openInputStream(uri)
//            Drawable.createFromStream(inputStream, uri.toString())
//            insert("(::", Drawable.createFromStream(inputStream, uri.toString()))
//
//
//
//        } catch (e: FileNotFoundException) {
//        }
    }

    override fun editAddPicture(path: String) {
        insert("(::", Drawable.createFromPath(path))
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun loadPicture() {
        viewModel.loadPicture()
    }


    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun share() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
//        intent.putExtra(Intent.EXTRA_STREAM, Emojis.getImageUri(bitmap, packageName))
        startActivity(Intent.createChooser(intent, "share Image"))
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

    fun addToText(name: String) {
        try {
            insert(":-)", Drawable.createFromStream(assets.open(name), null))
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    /**
     *  Event Text watcher
     *
     */
    fun insert(emoticon: String, drawable: Drawable) {
        var size = dpToPx(editText.context, 40.0f).toInt()
        drawable.setBounds(0, 0, size, size)
        var span = ImageSpan(drawable, 0)
        var start = editText.selectionStart
        var end = editText.selectionEnd
        var message: Editable = editText.editableText
        message.replace(start, end, emoticon)
        message.setSpan(span, start, emoticon.length + start, 33)
        message.append(" ")
    }

    /**
     *
     * Load bitmap
     * */

    private fun loadBitmapFromEdit(): Bitmap {
        Log.d("TAG", editText.text.toString())
        editText.isCursorVisible = false
        editText.setSelection(editText.text.length)
        var lines = editText.lineCount
        var width = editText.width
        var padding = dpToPx(this, 5.0f).toInt()
        editText.selectionEnd
        var pos = editText.selectionStart
        var layout: Layout = editText.layout
        var line = layout.getLineForOffset(pos)
        var baseline = layout.getLineBaseline(line)
        var ascent: Int = layout.getLineAscent(line)
        var x: Float = layout.getPrimaryHorizontal(pos)
        var y: Int = baseline + ascent
        var height: Int = editText.height
        if (lines == 1) {
            width = x.toInt() + (padding * 2)
            var spanSize: Int = (editText.text.getSpans(0, pos, ImageSpan::class.java) as Array<ImageSpan>).size * 3
            height = if ((editText.text.getSpans(0, pos, ImageSpan::class.java) as Array<ImageSpan>).size == 0 || editText.text.length - spanSize <= 0) {
                dpToPx(this, 50.0f).toInt()
            } else {
                dpToPx(this, 60.0f).toInt()
            }
        }
        var b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        var b = BitmapFactory.decodeResource(resources, R.drawable.neymar_heads_cmn_001_80).copy(Bitmap.Config.ARGB_8888, true)
        var c = Canvas(b)
        var p = Paint()
        p.style = Paint.Style.FILL
        p.setARGB(255, 181, 185, 194)
        c.drawRoundRect(RectF(0.0f, 0.0f, width.toFloat(), height.toFloat()), padding.toFloat(), padding.toFloat(), p)
        editText.layout(editText.left, editText.top, editText.right, editText.bottom)
        editText.draw(c)
        editText.isCursorVisible = true
        return b
    }

    fun detectFace(path: Uri) {
        var bitmap: Bitmap = decodeBitmapUri(this, path)
        if (detector != null && bitmap != null) {
            var scale: Float = resources.displayMetrics.density
            val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = Color.rgb(255, 61, 61)
            paint.textSize = (14 * scale).toInt().toFloat()
            paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f

            val canvas = Canvas(bitmap.copy(Bitmap.Config.ARGB_8888, true))
            canvas.drawBitmap(bitmap, 0f, 0f, paint)
            val frame = Frame.Builder().setBitmap(bitmap).build()
            val faces = detector.detect(frame)

            var text: String = ""
            for (index in 0 until faces.size()) {
                val face = faces.valueAt(index)
                canvas.drawRect(
                        face.position.x,
                        face.position.y,
                        face.position.x + face.width,
                        face.position.y + face.height, paint)
                text = text + " - " + face.isSmilingProbability.toString() + " - " + face.isLeftEyeOpenProbability.toString() + " - " + face.isRightEyeOpenProbability.toString()

                for (landmark in face.landmarks) {
                    val cx = landmark.position.x.toInt()
                    val cy = landmark.position.y.toInt()
                    canvas.drawCircle(cx.toFloat(), cy.toFloat(), 5f, paint)
                }
            }

            Log.d("TAG", "text = $text")

            if (faces.size() == 0) {
                Toast.makeText(this, "Not Face", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "No Face")
            } else {
                Toast.makeText(this, " Face", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "face ${faces.size()}")
            }
        } else {
            Toast.makeText(this, "Could not set up the detector!", Toast.LENGTH_SHORT).show()
            Log.d("TAG", "Could not set up the detector!")
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
    }
}
