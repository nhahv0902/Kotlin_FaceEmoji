package com.nhahv.faceemoji.utils

import android.content.Intent
import android.support.v7.app.AppCompatActivity

/**
 * Created by nhahv on 10/19/17.
 */
class Navigator(val context: AppCompatActivity) {

    inline fun <reified T : AppCompatActivity> startActivity() {
        val intent = Intent(context.applicationContext, T::class.java)
        context.startActivity(intent)
    }
}