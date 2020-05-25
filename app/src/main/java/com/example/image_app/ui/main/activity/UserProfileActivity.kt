package com.example.image_app.ui.main.activity


import android.os.Bundle
import com.example.image_app.R
import com.example.image_app.base.BaseActivity
import com.example.image_app.databinding.ProfileLayoutBinding
import com.example.image_app.ui.data.MUser
import com.example.image_app.ui.main.viewModel.ProfileViewModel
import com.example.image_app.util.Constant.USER_DATA
import com.google.gson.Gson

class UserProfileActivity : BaseActivity<ProfileLayoutBinding, ProfileViewModel>() {
    override fun layoutId(): Int = R.layout.profile_layout

    override fun getViewModelClass(): Class<ProfileViewModel> = ProfileViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        listner()
    }

    private fun listner() {
        binding.header.crdImgBack.setOnClickListener {onBackPressed()  }
    }

    private fun initialize() {
       val data=Gson().fromJson(intent.getStringExtra(USER_DATA),MUser::class.java)
        setData(data)
    }

    private fun setData(data: MUser) {
        binding.etName.setText(data.userName)
        binding.etNumber.setText(data.userPhone)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

}
