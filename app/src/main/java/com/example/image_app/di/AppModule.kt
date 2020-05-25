package com.example.image_app.di

import android.content.res.Resources
import com.example.image_app.api.IRxSchedulers
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

private const val MODULE_NAME="App Module"

val appModule = Kodein.Module(MODULE_NAME,false){
    bind<Resources>() with singleton { instance<CustomApplication>().resources }
    bind<IRxSchedulers>() with singleton { getIRxSchedulers() }
}

fun getIRxSchedulers(): IRxSchedulers =object :
    IRxSchedulers {
    override fun main(): Scheduler = AndroidSchedulers.mainThread()
    override fun io(): Scheduler = Schedulers.io()

}