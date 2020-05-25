package com.example.image_app.util

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.image_app.util.Constant.DIR_PATH
import com.example.image_app.util.Constant.DOWNLOADING_PATH
import com.example.image_app.util.Constant.KEY_IMAGE_URL
import java.io.File
import java.util.*


class UploadWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    var apkStorage: File? = null
    override suspend fun doWork(): Result {
        return try {
            val imageUrl = inputData.getString(KEY_IMAGE_URL)
            val filePath = if (imageUrl != null) downloadImage(imageUrl) else ""
            val outputData = workDataOf(DOWNLOADING_PATH to filePath)
            Result.success(outputData)
        } catch (e: Exception) {
            Result.failure()
        }
    }

    @SuppressLint("NewApi")
    private fun downloadImage(imageUrl: String): String {
        if (CheckForSDCard().isSDCardPresent) apkStorage = File(DOWNLOADING_PATH)
        else Log.e("download", "Oops!! There is no SD Card.")
        if (!apkStorage?.exists()!!) {
            apkStorage?.mkdirs()
        }

        val manager = applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(imageUrl)).setTitle("Image Downloading")
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setRequiresCharging(false)
            .setShowRunningNotification(true)
            .setAllowedOverMetered(true).setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
            )
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(
                DIR_PATH,
                "${Calendar.getInstance().timeInMillis}.png"
            )

        manager.enqueue(request)
        return apkStorage!!.absolutePath.toString() + File.separator + Calendar.getInstance().timeInMillis+".png"
    }

    private class CheckForSDCard {
        val isSDCardPresent: Boolean
            get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
}