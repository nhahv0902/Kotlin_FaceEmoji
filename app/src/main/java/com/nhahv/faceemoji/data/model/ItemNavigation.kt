package com.nhahv.faceemoji.data.model

/**
 * Created by nhahv on 10/19/17.
 */

data class ItemNavigation(
    val icon: Int,
    var isActive: Boolean = false,
    val name: String = "Transform",
    val typeAction: Int = TypeAction.LIBRARY
)
