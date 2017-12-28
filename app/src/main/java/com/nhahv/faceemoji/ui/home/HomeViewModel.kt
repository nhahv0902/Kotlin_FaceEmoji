package com.nhahv.faceemoji.ui.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhahv.faceemoji.R
import com.nhahv.faceemoji.data.model.FaceEmoji
import com.nhahv.faceemoji.networking.NetworkService
import com.nhahv.faceemoji.networking.NetworkService.Companion.BASE_POINT_URL
import com.nhahv.faceemoji.networking.NetworkService.Companion.END_POINT_URL
import com.nhahv.faceemoji.ui.BaseActivity.Companion.REQUEST_IMAGE_CAPTURE
import com.nhahv.faceemoji.ui.BaseActivity.Companion.REQUEST_PICK_IMAGE
import com.nhahv.faceemoji.ui.BaseRecyclerAdapter
import com.nhahv.faceemoji.ui.BaseRecyclerAdapter.OnItemListener
import com.nhahv.faceemoji.ui.BaseViewModel
import com.nhahv.faceemoji.utils.*
import com.nhahv.faceemoji.utils.FileUtil.createImageFile
import com.nhahv.faceemoji.utils.FileUtil.loadPictures
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.URL


/**
 * Created by nhahv0902 on 10/17/17.
 *
 */
class HomeViewModel(private val navigator: Navigator) : BaseViewModel(), BaseRecyclerAdapter.OnItemLongListener<String> {
    lateinit var listener: IHomeListener
    var currentPath: String? = null

    val emoType = ObservableInt(Emo.YOU)

    val isNoNetwork = ObservableBoolean(true)

    override fun onClick(item: String, position: Int) {
        if (position == 0 && item.isEmpty()) {
            listener.showBottomSheetLibrary()
        } else {
            if (emoType.get() == Emo.YOU) {
                listener.editAddPicture(item)
            } else if (emoType.get() == Emo.STICKER) {
                listener.shareSticker(item)
            }
        }
    }

    override fun onLongClick(item: String, position: Int): Boolean {
        if (emoType.get() == Emo.STICKER || (position == 0 && pictures[0].isEmpty())) {
            return true
        }
        listener.removePicture(item, position)
        return true
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


    // sticker
    val stickerPictures: ArrayList<String> = ArrayList()
    val youPictures: ArrayList<String> = ArrayList()

    init {
        loadEMO()
        loadSticker()
    }

    private fun loadEMO() {
        var temp = navigator.sharePref.get(PREF_EMO_EMO, String::class.java)
        if (temp.isNullOrEmpty()) {
            temp = readFile(PREF_EMO_EMO)
            navigator.sharePref.put(PREF_EMO_EMO, temp)
        }
        val pictureTemp: ArrayList<FaceEmoji> = Gson().fromJson(temp, object : TypeToken<List<FaceEmoji>>() {}.type)
        morePictures.addAll(pictureTemp)
        morePictures[0].selected = true
        moreAdapter.notifyChange()
        morePictures[0].items.mapTo(mores) { it.image }
        adapterMore.notifyChange()
    }

    private fun loadSticker() {
        var temp = navigator.sharePref.get(PREF_STICKER_EMO, String::class.java)
        if (temp.isNullOrEmpty()) {
            temp = readFile(PREF_STICKER_EMO)
            navigator.sharePref.put(PREF_STICKER_EMO, temp)
        }
        val pictureTemp: ArrayList<String> = Gson().fromJson(temp, object : TypeToken<List<String>>() {}.type)
        stickerPictures.addAll(pictureTemp)
    }


    fun loadYouEMO() {
        var youEmoji = navigator.sharePref.get(PREF_YOU_MOJI, String::class.java)
        if (youEmoji.isNullOrEmpty()) {
            youEmoji = readFile(PREF_YOU_MOJI)
            navigator.sharePref.put(PREF_YOU_MOJI, youEmoji)
        }
        youEmoji?.let {
            val pictureTemp2: ArrayList<String> = Gson().fromJson(it, object : TypeToken<List<String>>() {}.type)
            youPictures.addAll(pictureTemp2)
            picturesAsset.addAll(pictureTemp2)
            adapter.notifyChange()
        }
        val temps = loadPictures()
        if (temps.size == 0) {
            youPictures.add(0, "")
        } else {
            youPictures.addAll(0, temps)
        }
        pictures.addAll(youPictures)
        adapter.notifyChange()
    }

    fun onResultFromActivity(requestCode: Int, resultCode: Int, data: Intent?) {
        if (HomeActivity.SHARE_IMAGE == requestCode) {
            listener.removeText()
        }
        if (resultCode != RESULT_OK) return
        when (requestCode) {
            REQUEST_PICK_IMAGE -> {
                listener.setImagePicture(data?.data)
            }
            REQUEST_IMAGE_CAPTURE -> {
                handleImageCapture()
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                upFileImage(result.uri.path)

            }
            HomeActivity.SHARE_IMAGE -> {
                listener.removeText()
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
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos) //bm is the bitmap object
        return START_BASE64 + Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

    }

    fun convertBase64ToFileImage(base64: String): String? {
        return try {
            val temp = if (base64.contains(START_BASE64)) {
                base64.removePrefix(START_BASE64)
            } else {
                base64
            }
            val imageBytes: ByteArray = Base64.decode(temp, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            val file = createImageFile()
            val os = BufferedOutputStream(FileOutputStream(file))
            decodedImage.compress(Bitmap.CompressFormat.PNG, 100, os)
            os.close()
            file?.path
        } catch (ex: NullPointerException) {
            null
        }
    }

    fun convertBase64ToFileImageCache(file: File, base64: String): File? {
        return try {
            val temp = when {
                base64.contains(START_BASE64) -> base64.removePrefix(START_BASE64)
                base64.contains(START_BASE64_PNG) -> base64.removePrefix(START_BASE64_PNG)
                else -> base64
            }
            val imageBytes: ByteArray = Base64.decode(temp, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            val os = BufferedOutputStream(FileOutputStream(file))
            decodedImage.compress(Bitmap.CompressFormat.PNG, 100, os)
            os.close()
            file
        } catch (ex: NullPointerException) {
            null
        }
    }

    fun createFileImageFromBitmap(bitmap: Bitmap): File? {
        val file = createImageFile("Face")
        val os = BufferedOutputStream(FileOutputStream(file))
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
        os.close()
        return file
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

    private fun upFileImage(pathFile: String) {
        if (!isNoNetwork.get()) {
            navigator.toast("Please, connect network to create picture!")
            return
        }
        listener.showDialog()


        if (END_POINT_URL == BASE_POINT_URL) {
            doAsync {
                val result = URL(BASE_POINT_URL).readText()
                NetworkService.END_POINT_URL = result
                upFileImageToServer(pathFile)
            }
        } else {
            upFileImageToServer(pathFile)
        }
    }


    private fun upFileImageToServer(pathFile: String) {

        val file: File = File(pathFile)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body: MultipartBody.Part = MultipartBody.Part.createFormData(file.path, file.name, requestFile)

        val description = RequestBody.create(MediaType.parse("multipart/form-data"), "upFile")
        NetworkService.END_POINT_URL
        NetworkService.getInstance(navigator.context).getAPI().upFileImage(description, body).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (response != null) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            val pathFileTemp = convertBase64ToFileImage(it)
                            pathFileTemp?.let {
                                emoType.set(Emo.YOU)
                                if (youPictures[0].isEmpty()) {
                                    youPictures[0] = it
                                } else {
                                    youPictures.add(0, it)
                                }
                                pictures.clear()
                                pictures.addAll(youPictures)
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

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                listener.hideDialog()
                navigator.log(t.toString())
                navigator.toast("Not connect to server")
            }

        })
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

    fun updateYouEmo() {
        pictures.clear()
        pictures.addAll(youPictures)
        adapter.notifyChange()
        emoType.set(Emo.YOU)
    }

    fun updateEmo() {
        emoType.set(Emo.EMO)
    }

    fun updateSticker() {
        pictures.clear()
        pictures.addAll(stickerPictures)
        adapter.notifyChange()
        emoType.set(Emo.STICKER)
    }

}

