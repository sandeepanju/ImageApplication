package com.example.image_app.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel: ViewModel(),LifecycleObserver{
    private var compositeDisposable : CompositeDisposable?=null
    override fun onCleared() {
        super.onCleared()
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
            compositeDisposable!!.clear()
            compositeDisposable = null
        }
    }

    protected fun addDiposable(disposable : Disposable){
        if (compositeDisposable == null) compositeDisposable = CompositeDisposable()
        compositeDisposable!!.add(disposable)
    }
}