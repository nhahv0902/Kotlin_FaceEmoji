package com.nhahv.faceemoji.ui.home

import android.databinding.ObservableField
import com.nhahv.faceemoji.R
import com.nhahv.faceemoji.data.model.ItemNavigation
import com.nhahv.faceemoji.data.model.TypeAction.ADJUST
import com.nhahv.faceemoji.data.model.TypeAction.BRUSH
import com.nhahv.faceemoji.data.model.TypeAction.FILTER
import com.nhahv.faceemoji.data.model.TypeAction.FOCUS
import com.nhahv.faceemoji.data.model.TypeAction.FRAME
import com.nhahv.faceemoji.data.model.TypeAction.LIBRARY
import com.nhahv.faceemoji.data.model.TypeAction.OVERLAY
import com.nhahv.faceemoji.data.model.TypeAction.STICKER
import com.nhahv.faceemoji.data.model.TypeAction.TEXT
import com.nhahv.faceemoji.data.model.TypeAction.TRANSFORM
import com.nhahv.faceemoji.ui.BaseRecyclerAdapter
import com.nhahv.faceemoji.ui.BaseViewModel
import com.nhahv.faceemoji.utils.Navigator

/**
 * Created by nhahv0902 on 10/17/17.
 */
class HomeViewModel(private val navigator: Navigator) : BaseViewModel(), HomeListener {

    override fun onClick(item: ItemNavigation, position: Int) {
        when (item.typeAction) {
            LIBRARY -> {
                // open dialog

            }
        }
    }

    private val mNavigatorItem: ArrayList<ItemNavigation> = ArrayList()
    val adapterNavigator: ObservableField<BaseRecyclerAdapter<ItemNavigation>>
            = ObservableField(BaseRecyclerAdapter(mNavigatorItem, this, R.layout.item_bottom_navigator))

    init {
        mNavigatorItem.add(
                ItemNavigation(R.drawable.library_status, name = "Library", typeAction = LIBRARY))
        mNavigatorItem.add(
                ItemNavigation(R.drawable.transform_status, name = "Transform", typeAction = TRANSFORM))
        mNavigatorItem.add(
                ItemNavigation(R.drawable.filter_status, name = "Filter", typeAction = FILTER))
        mNavigatorItem.add(
                ItemNavigation(R.drawable.sticker_status, name = "Sticker", typeAction = STICKER))
        mNavigatorItem.add(
                ItemNavigation(R.drawable.adjust_status, name = "Adjust", typeAction = ADJUST))
        mNavigatorItem.add(
                ItemNavigation(R.drawable.text_status, name = "Text", typeAction = TEXT))
        mNavigatorItem.add(
                ItemNavigation(R.drawable.overlay_status, name = "Overlay", typeAction = OVERLAY))
        mNavigatorItem.add(
                ItemNavigation(R.drawable.frame_status, name = "Frame", typeAction = FRAME))
        mNavigatorItem.add(
                ItemNavigation(R.drawable.brush_status, name = "Brush", typeAction = BRUSH))
        mNavigatorItem.add(
                ItemNavigation(R.drawable.focus_status, name = "Focus", typeAction = FOCUS))
    }
}