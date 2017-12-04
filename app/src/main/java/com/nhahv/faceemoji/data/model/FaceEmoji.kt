package com.nhahv.faceemoji.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by nhahv0902 on 10/17/17.
 */
data class FaceEmoji(@SerializedName("name") val name: String,
                     @SerializedName("items") val items: ArrayList<ItemFaceEmoji>,
                     var selected: Boolean = false
) {
    fun getImage(): String? = items[0].image
}

data class ItemFaceEmoji(@SerializedName("image") val image: String)