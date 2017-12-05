package com.nhahv.faceemoji.ui.emoji

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.bumptech.glide.Glide
import com.nhahv.faceemoji.R
import com.nhahv.faceemoji.databinding.ActivityEmojiBinding
import com.nhahv.faceemoji.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_emoji.*

class EmojiActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val binding: ActivityEmojiBinding = DataBindingUtil.setContentView(this, R.layout.activity_emoji)

        Glide.with(this).load(intent.extras.getString("image")).into(imageView11)
    }
}
