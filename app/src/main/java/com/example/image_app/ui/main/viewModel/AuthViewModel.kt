package com.example.image_app.ui.main.viewModel

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.image_app.base.BaseViewModel
import com.example.image_app.api.ApiService
import com.example.image_app.api.IRxSchedulers
import com.example.image_app.ui.data.MUser
import com.example.image_app.ui.data.ViewState
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext
import java.util.concurrent.TimeUnit

class AuthViewModel(context: Context): BaseViewModel(),KodeinAware{
    override val kodein by closestKodein(context)
    override val kodeinContext = kcontext(context)
    private val apiService: ApiService by instance()
    private val schedulers: IRxSchedulers by instance()
    val verificationPhoneResponse: MutableLiveData<ViewState<String>> = MutableLiveData()
    val userResponse: MutableLiveData<ViewState<MUser>> = MutableLiveData()
    fun verifyPhoneNumber(phoneNumber: String) {

   PhoneAuthProvider.getInstance().verifyPhoneNumber(
       "+91$phoneNumber",
       60,
       TimeUnit.SECONDS,
       TaskExecutors.MAIN_THREAD,object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
           override fun onVerificationCompleted(credential: PhoneAuthCredential) {
               // This callback will be invoked in two situations:
               // 1 - Instant verification. In some cases the phone number can be instantly
               //     verified without needing to send or enter a verification code.
               // 2 - Auto-retrieval. On some devices Google Play services can automatically
               //     detect the incoming verification SMS and perform verification without
               //     user action.
//               Log.d(TAG, "onVerificationCompleted:$credential")
//               signInWithPhoneAuthCredential(credential)
           }

           override fun onVerificationFailed(e: FirebaseException) {
               // This callback is invoked in an invalid request for verification is made,
               // for instance if the the phone number format is not valid.
               Log.e("AuthviewModel->",e.toString())
               verificationPhoneResponse.postValue(ViewState.Error(e.toString()))
           }

           override fun onCodeSent(
               verificationId: String,
               token: PhoneAuthProvider.ForceResendingToken
           ) {
               verificationPhoneResponse.postValue(ViewState.Success(verificationId))
               // The SMS verification code has been sent to the provided phone number, we
               // now need to ask the user to enter the code and then construct a credential
               // by combining the code with a verification ID.
//               Log.d(TAG, "onCodeSent:$verificationId")

               // Save verification ID and resending token so we can use them later
//               storedVerificationId = verificationId
//               resendToken = token
               // ...
           }
       })
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = task.result?.user
                    var  usedData: MUser = MUser(user?.displayName?:"Name Not Found in firebase",user?.phoneNumber?:"9999999999")
                    Log.e(TAG, usedData.toString())
                    userResponse.postValue(ViewState.Success(usedData))
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        verificationPhoneResponse.postValue(ViewState.Error("The verification code entered was invalid"))
                    }
                    else  verificationPhoneResponse.postValue(ViewState.Error("Sign in failed"))
                }
            }
    }
}