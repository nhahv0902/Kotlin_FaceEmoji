package com.nhahv.faceemoji.ui.emoji

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.nhahv.faceemoji.R
import com.nhahv.faceemoji.databinding.ActivityEmojiBinding
import com.nhahv.faceemoji.ui.BaseActivity

class EmojiActivity : BaseActivity() {

    private lateinit var viewModel: EmojiViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = obtainViewModel(EmojiViewModel::class.java)

        val binding: ActivityEmojiBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_emoji)
        binding.viewModel = viewModel

    }
}
