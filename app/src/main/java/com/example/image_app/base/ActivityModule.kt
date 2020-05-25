package com.example.image_app.base

import androidx.lifecycle.ViewModelProvider
import com.example.image_app.di.ViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

private const val MODULE_NAME = "Activity Module"

val activityModule = Kodein.Module(MODULE_NAME, false) {
    bind<ViewModelProvider.Factory>() with singleton { ViewModelFactory(instance("ActivityContext")) }
}