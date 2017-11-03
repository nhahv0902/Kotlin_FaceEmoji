package com.nhahv.faceemoji.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import com.nhahv.faceemoji.ui.home.HomeViewModel
import com.nhahv.faceemoji.ui.main.ChoseViewModel
import com.nhahv.faceemoji.utils.Navigator

/**
 * Created by nhahv on 10/19/17.
 */
class ViewModelFactory private constructor(
        private val context: AppCompatActivity
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
            with(modelClass) {
                when {
                    isAssignableFrom(HomeViewModel::class.java) -> {
                        HomeViewModel(Navigator(context))
                    }
                    isAssignableFrom(ChoseViewModel::class.java) -> {
                        ChoseViewModel(Navigator(context))
                    }
                    else -> {
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                    }
                }
            } as T


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: AppCompatActivity) =
                INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                    INSTANCE ?: ViewModelFactory(context).also { INSTANCE = it }
                }


        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}