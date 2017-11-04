package com.nhahv.faceemoji.data.local

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

/**
 * Created by nhahv on 11/9/17.
 */
class SharePrefs private constructor(val context: Context) {
    val sharePrefs: SharedPreferences = context.getSharedPreferences("SharePrefs", Context.MODE_PRIVATE)

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: SharePrefs? = null

        fun getInstance(context: Context) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: SharePrefs(context).also { INSTANCE = it }
                }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> SharedPreferences.get(key: String, type: TypeSharePrefs): T? =
        when (type) {
            TypeSharePrefs.STRING -> getString(key, null) as T
            TypeSharePrefs.BOOLEAN -> getBoolean(key, false) as T
            TypeSharePrefs.INT -> getInt(key, 0) as T
            TypeSharePrefs.FLOAT -> getFloat(key, 0f) as T
        }

fun <T> SharedPreferences.put(key: String, value: T) {
    when (value) {
        is String -> edit().putString(key, value).apply()
        is Int -> edit().putInt(key, value).apply()
        is Float -> edit().putFloat(key, value).apply()
        is Boolean -> edit().putBoolean(key, value).apply()
    }
}

enum class TypeSharePrefs {
    STRING, INT, BOOLEAN, FLOAT
}