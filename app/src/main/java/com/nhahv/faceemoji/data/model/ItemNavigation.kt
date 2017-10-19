package com.nhahv.faceemoji.data.model

import com.nhahv.faceemoji.R

/**
 * Created by nhahv on 10/19/17.
 */
data class ItemNavigation(
    val icon: Int = R.drawable.icon_transform,
    val isActive: Boolean = false,
    val name: String = "Transform",
    val typeAction: Int = TypeAction.LIBRARY
)
