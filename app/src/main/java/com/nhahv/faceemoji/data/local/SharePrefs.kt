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

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, type: Class<T>): T? =
            when (type) {
                String::class.java -> sharePrefs.getString(key, null) as T
                Boolean::class.java -> sharePrefs.getBoolean(key, false) as T
                Int::class.java -> sharePrefs.getInt(key, 0) as T
                Float::class.java -> sharePrefs.getFloat(key, 0f) as T
                else -> null
            }

    fun <T> put(key: String, value: T) {
        when (value) {
            is String -> sharePrefs.edit().putString(key, value).apply()
            is Int -> sharePrefs.edit().putInt(key, value).apply()
            is Float -> sharePrefs.edit().putFloat(key, value).apply()
            is Boolean -> sharePrefs.edit().putBoolean(key, value).apply()
        }
    }
}


enum class TypeSharePrefs {
    STRING, INT, BOOLEAN, FLOAT
}