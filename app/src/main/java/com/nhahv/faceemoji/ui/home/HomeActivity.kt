package com.nhahv.faceemoji.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.text.*
import android.text.style.ImageSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.nhahv.faceemoji.R
import com.nhahv.faceemoji.data.model.Emojis
import com.nhahv.faceemoji.databinding.ActivityHomeBinding
import com.nhahv.faceemoji.databinding.DialogLibraryBinding
import com.nhahv.faceemoji.ui.BaseActivity
import com.nhahv.faceemoji.utils.FileUtil.createImageFile
import com.nhahv.faceemoji.utils.FileUtil.dpToPx
import com.nhahv.faceemoji.utils.GlideApp
import com.nhahv.faceemoji.utils.TextDrawable
import kotlinx.android.synthetic.main.activity_home.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.*

@RuntimePermissions
class HomeActivity : BaseActivity(), OnOpenDialogLibrary, TextWatcher {

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
            GlideApp.with(this).load(File(path)).into(picture)
        }

        imageView.setOnClickListener {
            imageView.setImageBitmap(loadBitmapFromEdit())
//            shareWithPermissionCheck()
        }

        editText.addTextChangedListener(this)
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
        }


    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun share() {
//        val intent = Intent(Intent.ACTION_SEND)
//        intent.type = "image/*"
//        intent.putExtra(Intent.EXTRA_STREAM, Emojis.getImageUri(bitmap, packageName))
//        intent.`package` = packageName
//        startActivity(Intent.createChooser(intent, "share Image"))

//            val temp = shareImage()
//            temp?.let {
//                GlideApp.with(this).load(Uri.fromFile(it)).into(picture)
//                val intent = Intent(Intent.ACTION_SEND)
//                intent.type = "image/*"
//                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(it))
//                startActivity(Intent.createChooser(intent, "share Image"))
//                return@let
//            }

        Log.d("TAg", "null")
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


    private fun createDrawable(drawableId: Int, text: String): BitmapDrawable {
        val bm = BitmapFactory.decodeResource(resources, drawableId).copy(Bitmap.Config.ARGB_8888, true)
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK
        paint.textSize = 20f

        val canvas = Canvas(bm)
        canvas.drawText(text, 0f, (bm.height / 2).toFloat(), paint)

        return BitmapDrawable(bm)
    }

    fun addToText(section: Int, index: Int, collectible: Boolean) {
        if (collectible) {
            addToText(Emojis.getCollectibleMediumImageName(section, index))
        } else {
            addToText(Emojis.getMediumImageName(section, index))
        }
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

    val emoticonsToRemove: ArrayList<ImageSpan> = ArrayList()
    var oldText: CharSequence = ""

    fun insert(emoticon: String, resource: Int) {
        insert(emoticon, editText.context.resources.getDrawable(resource))
    }

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

    override fun afterTextChanged(s: Editable?) {
        var message: Editable = this.editText.getEditableText()
        if (message.length > this.oldText.length) {
            var difference: String = message.subSequence(this.oldText.length, message.length).toString()
//                    HomeActivity::javaClass.addToText()
            Log.d("TAG", difference)
            if (difference == "y") {
                message.replace(this.oldText.length, message.length, "");
                addToText("neymoji_body_300_80.png")

            }
        }
        var it = emoticonsToRemove.iterator()
        while (it.hasNext()) {
            var span: ImageSpan = it.next()
            var start = message.getSpanStart(span)
            var end = message.getSpanEnd(span)
            message.removeSpan(span)
            if (start != end) {
                message.delete(start, end)
            }
        }
        emoticonsToRemove.clear()
        this.oldText = s.toString()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (count > 0) {
            val end = start + count
            val message = editText.editableText
            for (span in message.getSpans(start, end, ImageSpan::class.java)) {
                val spanStart = message.getSpanStart(span)
                val spanEnd = message.getSpanEnd(span)
                if (spanStart < end && spanEnd > start) {
                    emoticonsToRemove.add(span)
                }
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }


    /**
     *
     * Load bitmap
     * */

    fun loadBitmapFromEdit(): Bitmap {
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
}
