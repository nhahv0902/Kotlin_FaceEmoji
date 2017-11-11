package com.nhahv.faceemoji.ui.home

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.nhahv.faceemoji.R
import com.nhahv.faceemoji.data.loadimage.LoaderPicture
import com.nhahv.faceemoji.networking.NetworkService
import com.nhahv.faceemoji.ui.BaseActivity.Companion.REQUEST_IMAGE_CAPTURE
import com.nhahv.faceemoji.ui.BaseActivity.Companion.REQUEST_PICK_IMAGE
import com.nhahv.faceemoji.ui.BaseRecyclerAdapter
import com.nhahv.faceemoji.ui.BaseViewModel
import com.nhahv.faceemoji.utils.FileUtil.createImageFile
import com.nhahv.faceemoji.utils.GlideApp
import com.nhahv.faceemoji.utils.Navigator
import com.nhahv.faceemoji.utils.START_BASE64
import com.nhahv.faceemoji.utils.galleryAddPicture
import com.theartofdev.edmodo.cropper.CropImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.*


/**
 * Created by nhahv0902 on 10/17/17.
 *
 */
class HomeViewModel(private val navigator: Navigator) : BaseViewModel(), BaseRecyclerAdapter.OnItemListener<String> {
    lateinit var onDialogLibrary: OnOpenDialogLibrary
    var currentPath: String? = null

    val isYourMoji = ObservableBoolean(true)

    val api = NetworkService.getInstance(navigator.context)

    val fileImage = ObservableField<String>()

    override fun onClick(item: String, position: Int) {
        if (isYourMoji.get()) {
            onDialogLibrary.editAddPicture(item)
        }
    }

    val pictures: ArrayList<String> = ArrayList()
    val adapter: ObservableField<BaseRecyclerAdapter<String>>
            = ObservableField(BaseRecyclerAdapter(pictures, this, R.layout.item_emoji))


    fun loadPicture() {
        pictures.addAll(LoaderPicture.getInstance(navigator.context).pictures)
        adapter.notifyChange()
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
                convertBase64ToFileImage(result.uri.path)

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

    fun layoutYour(isBoolean: Boolean) {
        isYourMoji.set(isBoolean)
    }


    fun convertFileImageToString(path: String): String {
        val bm = BitmapFactory.decodeFile(path)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos) //bm is the bitmap object
        val result = START_BASE64 + Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
        navigator.log(result)
        return result

    }

    fun convertBase64ToFileImage(base64: String): File? {


        val temp = convertFileImageToString(base64).replace(START_BASE64, "")

        val imageBytes: ByteArray = Base64.decode(temp, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        val file = createImageFile()
        val os = BufferedOutputStream(FileOutputStream(file))
        decodedImage.compress(Bitmap.CompressFormat.JPEG, 100, os)
        os.close()
        fileImage.set(file?.path)
        return file

    }

    fun readFromfile(fileName: String, context: Context): String {
        val returnString = StringBuilder()
        var fIn: InputStream? = null
        var isr: InputStreamReader? = null
        var input: BufferedReader? = null
        try {
            fIn = context.resources.assets.open(fileName, Context.MODE_WORLD_READABLE)
            isr = InputStreamReader(fIn)
            input = BufferedReader(isr)
            var line: String? = input.readLine()
            while (line != null) {
                returnString.append(line)
                line = input.readLine()
            }
        } catch (e: Exception) {
            e.message
        } finally {
            try {
                if (isr != null)
                    isr.close()
                if (fIn != null)
                    fIn.close()
                if (input != null)
                    input.close()
            } catch (e2: Exception) {
                e2.message
            }

        }
        return returnString.toString()
    }

    fun upFileImage(base64: String) {
        val result = api.getAPI().uploadImage(base64).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                navigator
            }

            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                response?.let {
                    if (response.isSuccessful) {

                    }
                    return
                }

            }
        })
    }

}