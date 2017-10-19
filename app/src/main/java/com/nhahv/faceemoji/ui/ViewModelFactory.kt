package com.nhahv.faceemoji.ui

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.support.annotation.VisibleForTesting
import com.nhahv.faceemoji.ui.home.HomeViewModel
import com.nhahv.faceemoji.ui.home.OnOpenDialogLibrary
import com.nhahv.faceemoji.utils.Navigator

/**
 * Created by nhahv on 10/19/17.
 */
class ViewModelFactory private constructor(
        private val context: Context
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
            with(modelClass) {
                when {
                    isAssignableFrom(HomeViewModel::class.java) -> {
                        HomeViewModel(Navigator(context))
                    }
                    else -> {
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                    }
                }
            } as T


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context) =
                INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                    INSTANCE ?: ViewModelFactory(context).also { INSTANCE = it }
                }


        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}