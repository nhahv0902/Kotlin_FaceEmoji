package com.nhahv.faceemoji.ui.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.nhahv.faceemoji.utils.START_BASE64
import com.nhahv.faceemoji.utils.galleryAddPicture
import com.theartofdev.edmodo.cropper.CropImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException


/**
 * Created by nhahv0902 on 10/17/17.
 *
 */
class HomeViewModel(private val navigator: Navigator) : BaseViewModel(), OnItemListener<String> {
    lateinit var onDialogLibrary: OnOpenDialogLibrary
    var currentPath: String? = null

    val isYourMoji = ObservableBoolean(true)

    override fun onClick(item: String, position: Int) {
        onDialogLibrary.editAddPicture(item)
    }

    var pictureLocal = ArrayList<String>()
    val pictures: ArrayList<String> = ArrayList()
    val adapter: ObservableField<BaseRecyclerAdapter<String>>
            = ObservableField(BaseRecyclerAdapter(pictures, this, R.layout.item_emoji))


    val morePictures: ArrayList<FaceEmoji> = ArrayList()
    val moreAdapter: ObservableField<BaseRecyclerAdapter<FaceEmoji>> =
            ObservableField(BaseRecyclerAdapter(morePictures, object : OnItemListener<FaceEmoji> {
                override fun onClick(item: FaceEmoji, position: Int) {
                    moreEmojiClick(position, item)
                }
            }, R.layout.item_more_emoji))

    init {
        var temp = readFile()
        val pictureTemp: ArrayList<FaceEmoji> = Gson().fromJson(temp, object : TypeToken<List<FaceEmoji>>() {}.type)
        morePictures.addAll(pictureTemp)
        moreAdapter.notifyChange()


        temp = readFile("you_emoji.json")
        temp?.let {
            val pictureTemp2: ArrayList<String> = Gson().fromJson(it, object : TypeToken<List<String>>() {}.type)
            pictures.addAll(pictureTemp2)
            adapter.notifyChange()
        }

        pictureLocal.addAll(pictures)
    }

    fun loadPicture() {
        val temp2 = ArrayList<String>(pictures)
        pictures.clear()
        pictures.addAll(loadPictures())
        pictures.addAll(temp2)
        adapter.notifyChange()

        pictureLocal.clear()
        pictureLocal.addAll(pictures)
    }

    fun onResultFromActivity(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK) return
        when (requestCode) {
            REQUEST_PICK_IMAGE -> {
                onDialogLibrary.setImagePicture(data?.data)
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
        log("$currentPath")
        currentPath?.let {
            navigator.context.galleryAddPicture(it)
            onDialogLibrary.setImagePicture(currentPath)
            print(currentPath)
            return
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
        NetworkService.getInstance(navigator.context).getAPI().uploadImage(base64).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                navigator.log(t.toString())
            }

            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let {
                            val pathFile = convertBase64ToFileImage(it)
                            pathFile?.let {
                                pictureLocal.add(0, it)
                                isYourMoji.set(true)
                                pictures.clear()
                                pictures.addAll(pictureLocal)
                                adapter.notifyChange()
                            }
                        }
                    }
                }

            }
        })
    }

    fun changeYouEmoji() {
        isYourMoji.set(true)
        pictures.clear()
        pictures.addAll(pictureLocal)
        adapter.notifyChange()
    }

    fun changeMoreEmoji() {
        isYourMoji.set(false)
        morePictures[0].selected = true
        moreAdapter.get().notifyItemChanged(0)
        pictures.clear()
        morePictures[0].items.mapTo(pictures) { it.image }
        adapter.notifyChange()
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
        pictures.clear()
        morePictures[position].items.mapTo(pictures) { it.image }
        adapter.notifyChange()
    }

}