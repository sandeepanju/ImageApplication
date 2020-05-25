package com.example.image_app.ui.main.activity


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.image_app.R
import com.example.image_app.base.BaseActivity
import com.example.image_app.databinding.AuthLayoutBinding
import com.example.image_app.ui.data.ViewState
import com.example.image_app.ui.main.viewModel.AuthViewModel
import com.example.image_app.util.Constant.USER_DATA
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.Gson

class AuthanticationActivity : BaseActivity<AuthLayoutBinding, AuthViewModel>() {
    private var isVarificationScreen=false
    private var varificationId=""
    override fun layoutId(): Int = R.layout.auth_layout

    override fun getViewModelClass(): Class<AuthViewModel> = AuthViewModel::class.java

    companion object{
        fun hideKeyboardInAndroidFragment(view: View) {
            val imm = view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listner()
    }

    private fun listner() {
        binding.btnSubmit.setOnClickListener {
            performAction()
        }
        binding.etMobNumberOtp.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                performAction()
                true
            } else {
                false
            }
        }
    }

    private fun performAction() {
        if (validateData()){
            if (isVarificationScreen) varifyUserByOtp(binding.etMobNumberOtp.text.toString())
            else verifyPhoneNumber()
        }
        else Toast.makeText(this,this.resources.getText(R.string.please_enter_valid_number),Toast.LENGTH_LONG).show()
    }

    private fun validateData(): Boolean {
        if (binding.etMobNumberOtp.text.isNotEmpty()){
            return if (isVarificationScreen &&  binding.etMobNumberOtp.text.length==6){
                true
            } else !isVarificationScreen &&  binding.etMobNumberOtp.text.length==10
        } else  return false
    }

    private fun verifyPhoneNumber() {
        observePhoneVerification()
        viewModel.verifyPhoneNumber(binding.etMobNumberOtp.text.toString())

    }

    private fun observePhoneVerification() {
        viewModel.verificationPhoneResponse.observe(this, Observer {
            when(it){
                is ViewState.Success-> {
                    varificationId=it.data
                    resetPhoneNumberForOtp()
                }
                is ViewState.Error-> Toast.makeText(this,it.data,Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun resetPhoneNumberForOtp() {
        isVarificationScreen=true
        binding.etMobNumberOtp.setText("")
        binding.etMobNumberOtp.hint=this.resources.getString(R.string.enter_otp)
    }

    private fun varifyUserByOtp(otp: String) {
        hideKeyboardInAndroidFragment(binding.etMobNumberOtp)
        val credential=PhoneAuthProvider.getCredential(varificationId,otp)
        viewModel.signInWithPhoneAuthCredential(credential)
        observeSingInData()
    }

    private fun observeSingInData() {
        viewModel.userResponse.observe(this, Observer {
            when(it){
                is ViewState.Success-> startActivity(Intent(this,MainActivity::class.java).putExtra(USER_DATA,Gson().toJson(it.data)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))

                is ViewState.Error-> Toast.makeText(this,it.data,Toast.LENGTH_SHORT).show()

            }
        })
    }

    override fun onBackPressed() {
        if (isVarificationScreen){
            binding.etMobNumberOtp.text.clear()
            binding.etMobNumberOtp.hint=this.resources.getString(R.string.enter_mobile_number)
            isVarificationScreen=false
        }
        else
        super.onBackPressed()
    }
}
