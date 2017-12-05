package com.nhahv.faceemoji.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.style.ImageSpan
import android.util.SparseArray
import android.widget.Toast
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.nhahv.faceemoji.R
import com.nhahv.faceemoji.databinding.ActivityHomeBinding
import com.nhahv.faceemoji.databinding.BottomFontsBinding
import com.nhahv.faceemoji.databinding.DialogFontSizeBinding
import com.nhahv.faceemoji.databinding.DialogLibraryBinding
import com.nhahv.faceemoji.ui.BaseActivity
import com.nhahv.faceemoji.ui.view.FaceProgressDialog
import com.nhahv.faceemoji.utils.FileUtil.createImageFile
import com.nhahv.faceemoji.utils.FileUtil.dpToPx
import com.nhahv.faceemoji.utils.hideSoftKeyboard
import com.nhahv.faceemoji.utils.showSoftKeyboard
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_home.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

@RuntimePermissions
class HomeActivity : BaseActivity(), IHomeListener, ColorPickerDialogListener {

    private lateinit var viewModel: HomeViewModel
    private lateinit var detector: FaceDetector

    var progressDialog: FaceProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = obtainViewModel(HomeViewModel::class.java)
        loadPictureWithPermissionCheck()

        viewModel.listener = this

        val binding: ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.viewModel = viewModel
        editText.typeface = ResourcesCompat.getFont(this, R.font.comic)
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

        layoutYour.setOnClickListener {
            hideSoftKeyboard()
            viewModel.changeYouEmoji()
        }
        layoutMore.setOnClickListener {
            hideSoftKeyboard()
            viewModel.changeMoreEmoji()
        }

        font.setOnClickListener {
            showSoftKeyboard(editText)
            val build = AlertDialog.Builder(this)
            val binding: BottomFontsBinding = BottomFontsBinding.inflate(layoutInflater, null, false)
            build.setView(binding.root)
            val dialog = build.create()

            binding.sansSerif.setOnClickListener {
                editText.typeface = ResourcesCompat.getFont(this, R.font.sans_serif)
                dialog.dismiss()
            }
            binding.trebutchetMS.setOnClickListener {
                editText.typeface = ResourcesCompat.getFont(this, R.font.trebutchet)
                dialog.dismiss()
            }
            binding.comicSans.setOnClickListener {
                editText.typeface = ResourcesCompat.getFont(this, R.font.comic)
                dialog.dismiss()
            }
            binding.caviarDreams.setOnClickListener {
                editText.typeface = ResourcesCompat.getFont(this, R.font.caaviar)
                dialog.dismiss()
            }
            binding.pacifico.setOnClickListener {
                editText.typeface = ResourcesCompat.getFont(this, R.font.pacifico)
                dialog.dismiss()
            }
            dialog.show()
        }

        sizeFont.setOnClickListener {
            showSoftKeyboard(editText)
            val build = AlertDialog.Builder(this)
            val binding: DialogFontSizeBinding = DialogFontSizeBinding.inflate(layoutInflater, null, false)
            build.setView(binding.root)
            val dialog = build.create()

            binding.normal.setOnClickListener {
                editText.textSize = 18f
                dialog.dismiss()
            }
            binding.large.setOnClickListener {
                editText.textSize = 24f
                dialog.dismiss()
            }
            binding.huge.setOnClickListener {
                editText.textSize = 30f
                dialog.dismiss()
            }
            dialog.show()
        }

        colorFont.setOnClickListener {
            hideSoftKeyboard()
            ColorPickerDialog.newBuilder()
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setAllowPresets(true)
                    .setDialogId(0)
                    .setColor(Color.BLUE)
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
        dialog.setContentView(binding.root)
        dialog.show()
    }

    override fun setImagePicture(uri: Uri?) {
        uri?.let {
            //            detectFace(uri)
            CropImage.activity(uri)
                    .setAspectRatio(1, 1)
                    .setRequestedSize(350, 350)
                    .setOutputCompressQuality(100)
                    .start(this)
        }
    }

    override fun editAddPicture(path: String) {
        showSoftKeyboard(editText)
        try {
            if (path.contains("img/") or path.contains("you/")) {
                insert("(", Drawable.createFromStream(assets.open(path), null))
            } else {
                insert("(", Drawable.createFromPath(path))
            }
        } catch (ex: IOException) {
            toast("Not add emoji")
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun loadPicture() {
        viewModel.loadPicture()
    }


    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun share() {
        val pathFile = viewModel.createFileImageFromBitmap(loadBitmapFromEdit())
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(pathFile)))
        startActivity(Intent.createChooser(intent, "share Image"))
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
        val width = dpToPx(editText.context, 68f).toInt()
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

    private fun loadBitmapFromEdit(): Bitmap {
        val color = editText.currentTextColor
        editText.isCursorVisible = false
        editText.setBackgroundColor(Color.WHITE)
        editText.setSelection(editText.text.length)
        editText.setTextColor(ContextCompat.getColor(this, R.color.color_blue_))

        val width = editText.width
        val height: Int = editText.height

        val padding = dpToPx(this, 5.0f).toInt()
        editText.selectionEnd

        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        val p = Paint()
        p.setARGB(0, 0, 0, 0)
        p.style = Paint.Style.FILL
        p.color = Color.BLACK
        c.drawText("@ymoji", padding.toFloat(), padding.toFloat(), p)
//        c.drawRoundRect(RectF(0.0f, 0.0f, width.toFloat(), height.toFloat()), padding.toFloat(), padding.toFloat(), p)
        editText.layout(editText.left, editText.top, editText.right, editText.bottom)
        editText.draw(c)
        editText.isCursorVisible = true
        editText.setBackgroundResource(R.drawable.bg_editor)
        editText.setTextColor(color)
        return b
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
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        if (dialogId == 0) {
            editText.setTextColor(color)
        }
    }

    override fun onDialogDismissed(dialogId: Int) {}

    override fun showDialog() {
        if (progressDialog == null) {
            progressDialog = FaceProgressDialog(this)
            progressDialog!!.setCancelable(false)
        }
        if (!progressDialog!!.isShowing) progressDialog!!.show()
    }

    override fun hideDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) progressDialog!!.dismiss()
    }
}
