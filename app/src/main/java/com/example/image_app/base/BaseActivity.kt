package com.example.image_app.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.android.retainedKodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

abstract class BaseActivity<B : ViewDataBinding,Vm : ViewModel>: AppCompatActivity(),KodeinAware{
   protected lateinit var binding: B
   protected lateinit var viewModel: Vm
    private val parentKodien by closestKodein()
    override val kodein: Kodein by retainedKodein {
        extend(parentKodien,copy = Copy.All)
  bind<Activity>() with singleton { this@BaseActivity }
  bind<Context>("ActivityContext") with singleton { this@BaseActivity }
        import(activityModule)
    }

    private val viewModelFactory: ViewModelProvider.Factory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindContentView(layoutId())

    }

    private fun bindContentView(layoutId: Int) {
        binding = DataBindingUtil.setContentView(this, layoutId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(getViewModelClass())

    }
    @LayoutRes
    abstract fun layoutId(): Int
    abstract fun getViewModelClass(): Class<Vm>
}