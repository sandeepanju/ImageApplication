package com.example.image_app.ui.main.activity


import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.util.Pair as UtilPair
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.image_app.R
import com.example.image_app.base.BaseActivity
import com.example.image_app.databinding.ActivityMainBinding
import com.example.image_app.databinding.ItemImageBinding
import com.example.image_app.ui.data.MImage
import com.example.image_app.ui.data.MUser
import com.example.image_app.ui.data.ViewState
import com.example.image_app.ui.main.adapter.ImageAdapter
import com.example.image_app.ui.main.interfaces.Callback
import com.example.image_app.ui.main.viewModel.MainViewModel
import com.example.image_app.util.Constant.CARD_DETAIL
import com.example.image_app.util.Constant.IMAGE_DATA
import com.example.image_app.util.Constant.IMAGE_MAIN
import com.example.image_app.util.Constant.USER_DATA
import com.google.gson.Gson


class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(),
    Callback {
   private var CURRENT_POSITION=1
    private var imageList :ArrayList<MImage> = ArrayList()
   private val data by lazy { Gson().fromJson(intent.getStringExtra(USER_DATA), MUser::class.java) }
    private val layoutManager by lazy { GridLayoutManager(applicationContext,2) }
    private var isloadingdata = false
    private var islastData = false

    override fun layoutId(): Int = R.layout.activity_main
    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        obServeListData()
        listner()
    }

    private fun listner() {
        binding.fbaDetail.setOnClickListener { startActivity(Intent(this,UserProfileActivity::class.java).putExtra(
            USER_DATA,Gson().toJson(data)
        ))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        binding.rvData.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isloadingdata) return
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    if (!islastData) {
                        isloadingdata = true
                        CURRENT_POSITION++
                        getImageList()
                    }
                }
            }
        })
    }

    private fun getImageList() {
        viewModel.getImageList(CURRENT_POSITION)
    }

    private fun initialize() {
        binding.rvData.layoutManager=layoutManager
        binding.rvData.adapter=
            ImageAdapter(imageList, this)
        getImageList()
    }

    private fun obServeListData() {
        viewModel.imageResponse.observe(this, Observer {
            when(it){
                is  ViewState.Success->{
                    binding.progressBar.visibility=View.GONE
                    updateMovieList(it.data as ArrayList<MImage>)
                }
              is  ViewState.Loading->binding.progressBar.visibility=View.VISIBLE
              is  ViewState.Error->binding.progressBar.visibility=View.GONE
              is  ViewState.NetworkError->binding.progressBar.visibility=View.GONE
            }
        })
    }

    private fun updateMovieList(data: ArrayList<MImage>) {
        if (data.isNotEmpty() && data.size>0){
            isloadingdata = false
            binding.rvData.adapter?.apply {
                imageList.addAll(data)
                notifyDataSetChanged()
            }
        }
        else if (!islastData) {
            CURRENT_POSITION--
            isloadingdata = false
            islastData = true
        }
    }

    override fun openSelectedImage(
        item: MImage,
        itemBinding: ItemImageBinding
    ) {
        val intent = Intent(this, ImageDetailActivity::class.java)
        intent.putExtra(IMAGE_DATA, Gson().toJson(item))
        val options = ActivityOptions.makeSceneTransitionAnimation(this,
            UtilPair.create( (itemBinding.crdImgDetail as View?)!!, CARD_DETAIL),
            UtilPair.create( (itemBinding.img as View?)!!, IMAGE_MAIN))
        startActivity(intent, options.toBundle())
    }
}
