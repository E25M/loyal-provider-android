package pet.loyal.provider.services

import android.app.IntentService
import android.content.Intent
import android.widget.Toast
import pet.loyal.provider.BuildConfig
import pet.loyal.provider.api.service.ProviderAPIService
import pet.loyal.provider.util.Constants
import retrofit2.Retrofit
import java.io.IOException
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import okhttp3.ResponseBody
import pet.loyal.provider.model.Download
import pet.loyal.provider.util.showToast
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.pow
import kotlin.math.roundToInt
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import pet.loyal.provider.R

class DownloadService: IntentService("Download Service") {

    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager
    private val totalFileSize: Int = 0

    override fun onHandleIntent(intent: Intent?) {

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationBuilder = NotificationCompat.Builder(this, "")
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle("Download")
            .setContentText("Downloading File")
            .setAutoCancel(true)
        notificationManager.notify(0, notificationBuilder.build())

        val urlApk = intent?.getStringExtra(Constants.extra_url_apk)

        if (urlApk != null) {
            initDownload(urlApk)
        }
    }

    private fun initDownload(urlDownload: String){

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .build()

        val retrofitInterface = retrofit.create(ProviderAPIService::class.java)

        val request = retrofitInterface.downloadAPK(urlDownload)
        try {
            downloadFile(request.execute().body())
        } catch (e:IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, e.message,Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun downloadFile(body: ResponseBody?) {

        if (body != null) {
            var count: Int
            val data = ByteArray(1024 * 4)
            val fileSize = body.contentLength()
            val bis = BufferedInputStream(body.byteStream(), 1024 * 8)
            val outputFile = File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS),
                "loyal_provider.apk"
            )
            val output = FileOutputStream(outputFile)
            var total: Long = 0
            val startTime = System.currentTimeMillis()
            var timeCount = 1
            while (bis.read(data) != -1) {
                count = bis.read(data)
                total += count.toLong()
                val totalFileSize = (fileSize / 1024.0.pow(2.0)).toInt()
                val current = (total / 1024.0.pow(2.0)).roundToInt().toDouble()

                val progress = (total * 100 / fileSize).toInt()

                val currentTime = System.currentTimeMillis() - startTime

                val download = Download()
                download.totalFileSize = totalFileSize

                if (currentTime > 1000 * timeCount) {

                    download.currentFileSize = current.toInt()
                    download.progress = progress
                    sendNotification(download)
                    timeCount++
                }

                output.write(data, 0, count)
            }
            onDownloadComplete()
            output.flush()
            output.close()
            bis.close()
        }else{
            showToast(this, "Download failed")
        }
    }

    private fun sendNotification(download: Download) {

        notificationBuilder.setProgress(100, download.progress, false)
        notificationBuilder.setContentText("Downloading file " + download.currentFileSize + "/" + totalFileSize + " MB")
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun onDownloadComplete() {

        val download = Download()
        download.progress = 100

        val intent = Intent(Constants.action_download_apk)
        intent.putExtra(Constants.extra_download_apk_status, download)
        sendBroadcast(intent)

        notificationManager.cancel(0)
        notificationBuilder.setProgress(0, 0, false)
        notificationBuilder.setContentText("File Downloaded")
        notificationManager.notify(0, notificationBuilder.build())

    }

    override fun onTaskRemoved(rootIntent: Intent) {
        notificationManager.cancel(0)
    }
}