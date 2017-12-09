package com.nhahv.faceemoji.ui.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhahv.faceemoji.R
import com.nhahv.faceemoji.data.model.FaceEmoji
import com.nhahv.faceemoji.networking.NetworkService
import com.nhahv.faceemoji.ui.BaseActivity.Companion.REQUEST_IMAGE_CAPTURE
import com.nhahv.faceemoji.ui.BaseActivity.Companion.REQUEST_PICK_IMAGE
import com.nhahv.faceemoji.ui.BaseRecyclerAdapter
import com.nhahv.faceemoji.ui.BaseRecyclerAdapter.OnItemListener
import com.nhahv.faceemoji.ui.BaseViewModel
import com.nhahv.faceemoji.utils.FileUtil.createImageFile
import com.nhahv.faceemoji.utils.FileUtil.loadPictures
import com.nhahv.faceemoji.utils.Navigator
import com.nhahv.faceemoji.utils.PREF_YOU_MOJI
import com.nhahv.faceemoji.utils.START_BASE64
import com.nhahv.faceemoji.utils.galleryAddPicture
import com.theartofdev.edmodo.cropper.CropImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*


/**
 * Created by nhahv0902 on 10/17/17.
 *
 */
class HomeViewModel(private val navigator: Navigator) : BaseViewModel(), BaseRecyclerAdapter.OnItemLongListener<String> {
    lateinit var listener: IHomeListener
    var currentPath: String? = null

    val isYourMoji = ObservableBoolean(true)

    override fun onClick(item: String, position: Int) {
        listener.editAddPicture(item)
    }

    override fun onLongClick(item: String, position: Int): Boolean {
        listener.removePicture(item, position)
        return false
    }

    val picturesAsset = ArrayList<String>()
    val pictures: ArrayList<String> = ArrayList()
    val adapter: ObservableField<BaseRecyclerAdapter<String>>
            = ObservableField(BaseRecyclerAdapter(pictures, this, R.layout.item_emoji))

    val mores = ArrayList<String>()
    val adapterMore: ObservableField<BaseRecyclerAdapter<String>> =
            ObservableField(BaseRecyclerAdapter(mores, object : OnItemListener<String> {
                override fun onClick(item: String, position: Int) {
                    listener.editAddPicture(item)
                }
            }, R.layout.item_mores_emoji))

    val morePictures: ArrayList<FaceEmoji> = ArrayList()
    val moreAdapter: ObservableField<BaseRecyclerAdapter<FaceEmoji>> =
            ObservableField(BaseRecyclerAdapter(morePictures, object : OnItemListener<FaceEmoji> {
                override fun onClick(item: FaceEmoji, position: Int) {
                    moreEmojiClick(position, item)
                }
            }, R.layout.item_more_emoji))

    init {
        // set up bottom recycler more icon
        var temp = readFile()
        val pictureTemp: ArrayList<FaceEmoji> = Gson().fromJson(temp, object : TypeToken<List<FaceEmoji>>() {}.type)
        morePictures.addAll(pictureTemp)
        morePictures[0].selected = true
        moreAdapter.notifyChange()

        // setup recycler more icon
        morePictures[0].items.mapTo(mores) { it.image }
        adapterMore.notifyChange()

        val youEmoji = navigator.sharePref.get(PREF_YOU_MOJI, String::class.java)
        if (youEmoji.isNullOrEmpty()) {
            temp = readFile(PREF_YOU_MOJI)
            navigator.sharePref.put(PREF_YOU_MOJI, temp)
        } else {
            temp = navigator.sharePref.get(PREF_YOU_MOJI, String::class.java)

        }
        temp?.let {
            val pictureTemp2: ArrayList<String> = Gson().fromJson(it, object : TypeToken<List<String>>() {}.type)
            pictures.addAll(pictureTemp2)
            picturesAsset.addAll(pictureTemp2)
            adapter.notifyChange()
        }
        // setup recycler you emoji

    }

    fun loadPicture() {
        pictures.addAll(0, loadPictures())
        adapter.notifyChange()
    }

    fun onResultFromActivity(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK) return
        when (requestCode) {
            REQUEST_PICK_IMAGE -> {
                listener.setImagePicture(data?.data)
//                data?.let {
//                    upFileImage(convertFileImageToString(it.data.toString()))
//                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                handleImageCapture()
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                upFileImage(convertFileImageToString(result.uri.path))

            }
            else -> log("null")
        }
    }

    private fun handleImageCapture() {
        currentPath?.let {
            navigator.context.galleryAddPicture(it)
            listener.setImagePicture(Uri.fromFile(File(it)))
        }
    }

    fun convertFileImageToString(path: String): String {
        val bm = BitmapFactory.decodeFile(path)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos) //bm is the bitmap object
        return START_BASE64 + Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

    }

    fun convertBase64ToFileImage(base64: String): String? {
        val temp = base64.removePrefix(START_BASE64)
        val imageBytes: ByteArray = Base64.decode(temp, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        val file = createImageFile()
        val os = BufferedOutputStream(FileOutputStream(file))
        decodedImage.compress(Bitmap.CompressFormat.JPEG, 100, os)
        os.close()
        return file?.path
    }

    fun createFileImageFromBitmap(bitmap: Bitmap): String? {
        val file = createImageFile("Face")
        val os = BufferedOutputStream(FileOutputStream(file))
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
        os.close()
        return file?.path
    }

    fun readFile(fileName: String = "database.json"): String? {
        return try {
            val input = navigator.context.assets.open(fileName)
            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            String(buffer)
        } catch (ex: IOException) {
            null
        }
    }

    fun upFileImage(base64: String) {
        listener.showDialog()
        NetworkService.getInstance(navigator.context).getAPI().uploadImage(base64).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                listener.hideDialog()
                navigator.log(t.toString())
                navigator.toast("Not connect to server")
            }

            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (response != null) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            val pathFile = convertBase64ToFileImage(it)
                            pathFile?.let {
                                isYourMoji.set(true)
                                pictures.add(0, it)
                                adapter.notifyChange()
                            }
                            return@let
                        }
                    } else {
                        navigator.toast("No detect face")
                    }
                } else {
                    navigator.toast("No detect face")
                }
                listener.hideDialog()
            }
        })
    }

    fun changeYouEmoji() {
        isYourMoji.set(true)
    }

    fun changeMoreEmoji() {
        isYourMoji.set(false)
    }

    private fun moreEmojiClick(position: Int, item: FaceEmoji) {
        for ((index, value) in morePictures.withIndex()) {
            if (value.selected) {
                value.selected = false
                moreAdapter.get().notifyItemChanged(index)
            }
        }
        item.selected = true
        moreAdapter.get().notifyItemChanged(position)
        mores.clear()
        morePictures[position].items.mapTo(mores) { it.image }
        adapterMore.notifyChange()
    }

}