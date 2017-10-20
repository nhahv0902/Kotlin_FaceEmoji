package com.nhahv.faceemoji.ui

import android.arch.lifecycle.ViewModel
import android.util.Log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by nhahv0902 on 10/17/17.
 */
open class BaseViewModel : ViewModel() {
    private val mDisposable: CompositeDisposable = CompositeDisposable()

    fun subscription(disposable: Disposable) {
        mDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        mDisposable.clear()
    }

    fun log(message: String) {
        Log.d("TAG", message)
    }
}