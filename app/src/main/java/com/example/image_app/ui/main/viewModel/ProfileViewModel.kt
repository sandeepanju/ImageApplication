package com.example.image_app.ui.main.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.image_app.base.BaseViewModel
import com.example.image_app.api.ApiService
import com.example.image_app.api.IRxSchedulers
import com.example.image_app.ui.data.ViewState
import com.example.image_app.util.UploadWorker
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext
import java.util.concurrent.TimeUnit

class ProfileViewModel(context: Context): BaseViewModel(),KodeinAware{
    override val kodein by closestKodein(context)
    override val kodeinContext = kcontext(context)
}