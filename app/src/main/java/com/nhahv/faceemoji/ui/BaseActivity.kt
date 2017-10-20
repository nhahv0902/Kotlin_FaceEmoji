package com.nhahv.faceemoji.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity

/**
 * Created by nhahv0902 on 10/17/17.
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    inline fun <reified T : ViewModel> obtainViewModel(
        viewModelClass: Class<T>) = ViewModelProviders.of(this,
        ViewModelFactory.getInstance(this)).get(viewModelClass)

}