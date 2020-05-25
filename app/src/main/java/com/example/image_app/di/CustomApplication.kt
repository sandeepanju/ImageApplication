package com.example.image_app.di

import android.app.Application
import android.content.Context
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

class CustomApplication :Application(),KodeinAware{
    override val kodein = Kodein.lazy {
        bind<Context>("ApplicationContext") with singleton { this@CustomApplication }
        bind<CustomApplication>()with singleton { this@CustomApplication }
        import(appModule)
        import(networkModule)
    }

}