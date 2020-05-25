package com.example.image_app.ui.main.activity


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.image_app.R
import com.example.image_app.base.BaseActivity
import com.example.image_app.databinding.ImageDetailLayoutBinding
import com.example.image_app.ui.data.MImage
import com.example.image_app.ui.main.viewModel.ProfileViewModel
import com.example.image_app.util.UploadWorker
import com.example.image_app.util.Constant.IMAGE_DATA
import com.example.image_app.util.Constant.KEY_IMAGE_URL
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.util.concurrent.TimeUnit


class ImageDetailActivity : BaseActivity<ImageDetailLayoutBinding, ProfileViewModel>() {
    val workManager by lazy {WorkManager.getInstance(applicationContext)}
    private val imageData by lazy {  Gson().fromJson(intent.getStringExtra(IMAGE_DATA), MImage::class.java) }
    private  val imageWork by lazy { workDataOf(KEY_IMAGE_URL to imageData?.urls?.regular ) }
    override fun layoutId(): Int = R.layout.image_detail_layout
    override fun getViewModelClass(): Class<ProfileViewModel> = ProfileViewModel::class.java

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        listner()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun listner() {
        binding.header.crdImgBack.setOnClickListener {onBackPressed()  }
        binding.crdImgDownload.setOnClickListener {
            if(checkWriteExternalStoragePermission()){
                downloadImg(imageWork)
            }else requestWriteExtStoragePermission()
        }
    }

    private fun initialize() {
        val animSlideUp: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)
        binding.tvSwipeUp.startAnimation(animSlideUp)
      val animSlideLeft: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.slide_in)
        binding.tvSwipeLeft.startAnimation(animSlideLeft)
        binding.tvCount.text =
            "${this.resources.getString(R.string.image)} ${imageData.position}"
        Picasso.get().load(imageData.urls.small).error(R.drawable.ic_suggestion)
            .into(binding.imgLarge)
    }


    private fun checkWriteExternalStoragePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestWriteExtStoragePermission() {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadImg(imageWork)
            }
        }
    }
    private fun downloadImg(imageData: Data) {
        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputData(imageData)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()
        workManager.enqueue(uploadWorkRequest)

        workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id).observe(this, Observer { workInfo: WorkInfo? ->
            if (workInfo != null) {
                when(workInfo.state){
                    WorkInfo.State.SUCCEEDED->{
                        Toast.makeText(this,this.resources.getText(R.string.image_downloaded_sucessfully),Toast.LENGTH_SHORT).show()
                        Log.e("status","SUCCEEDED")
                    } WorkInfo.State.RUNNING->{
                        Log.e("status","RUNNING")
                } WorkInfo.State.FAILED->{
                    Log.e("status","FAILED")
                }
                }
            }
        })
    }
    override fun onBackPressed() {
        val animSlideExit: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.slide_out)
        binding.tvSwipeLeft.startAnimation(animSlideExit)
        val animSlideDown: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down)
        binding.tvSwipeUp.startAnimation(animSlideDown)
        super.onBackPressed()
    }
    companion object {
        const val REQUEST_CODE_WRITE_EXTERNAL_STORAGE: Int = 1021
    }
}
