package com.example.image_app.ui.main.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.image_app.base.BaseViewModel
import com.example.image_app.api.ApiService
import com.example.image_app.api.IRxSchedulers
import com.example.image_app.ui.data.MImage
import com.example.image_app.ui.data.ViewState
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext
import java.io.IOException

class MainViewModel(context: Context): BaseViewModel(),KodeinAware{
    override val kodein by closestKodein(context)
    override val kodeinContext = kcontext(context)
    private val apiService: ApiService by instance()
    private val schedulers: IRxSchedulers by instance()
    val imageResponse: MutableLiveData<ViewState<List<MImage>>> = MutableLiveData()

    fun getImageList(currentPosition: Int) {
        addDiposable(apiService.getImageList(pageNumber = currentPosition)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.main())
            .subscribe { response, error ->
                run {
                    if (response!=null) {
                        imageResponse.postValue(ViewState.Success(response))
                    } else {
                        when(error){
                            is IOException-> imageResponse.postValue(ViewState.NetworkError)
                            else -> imageResponse.postValue(ViewState.Error(error.toString()))
                        }
                    }
                }
            })
    }
}