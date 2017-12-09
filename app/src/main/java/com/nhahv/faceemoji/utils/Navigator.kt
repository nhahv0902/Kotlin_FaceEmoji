package com.nhahv.faceemoji.utils

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.nhahv.faceemoji.data.local.SharePrefs

/**
 * Created by nhahv on 10/19/17.
 */
class Navigator(val context: AppCompatActivity) {

    val sharePref = SharePrefs.getInstance(context)
    inline fun <reified T : AppCompatActivity> startActivity() {
        val intent = Intent(context.applicationContext, T::class.java)
        context.startActivity(intent)
    }

    inline fun <reified T : AppCompatActivity> startActivity(bundle: Bundle) {
        val intent = Intent(context.applicationContext, T::class.java)
        intent.putExtras(bundle)
        context.startActivity(intent)
    }

    fun log(message: String) {
        Log.d("TAG", message)
    }

    fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


}