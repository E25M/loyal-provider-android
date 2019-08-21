package pet.loyal.provider.view.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.crashlytics.android.Crashlytics
import com.google.gson.Gson
import io.fabric.sdk.android.Fabric
import pet.loyal.client.api.response.AppVersionDataResponse
import pet.loyal.provider.BuildConfig
import pet.loyal.provider.api.responses.AppVersionResponse
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import pet.loyal.provider.R
import pet.loyal.provider.databinding.ActivitySplashBinding
import pet.loyal.provider.util.*
import pet.loyal.provider.view.home.HomeScreen
import pet.loyal.provider.view.login.LoginActivity
import java.io.File

class SplashActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_WRITE_STORAGE = 101

    private lateinit var splashViewModel: SplashViewModel
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var activitySplashBinding: ActivitySplashBinding

    private var downloadUrl: String? = null

    companion object {
        private val TAG = SplashActivity.javaClass.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        initDataBinding()
        setObservers()
        splashViewModel.getAppVersion()
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }

    private fun initDataBinding() {
        splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        preferenceManager = PreferenceManager(this)
        activitySplashBinding.lifecycleOwner = this
        activitySplashBinding.viewModel = splashViewModel
        Fabric.with(this, Crashlytics())
    }

    private fun setObservers() {
        splashViewModel.appVersionResponse.observe(this, Observer { appVersionResponse ->
            run {
                if (appVersionResponse?.throwable != null) {
                    if (isJSONValid(appVersionResponse.throwable?.message!!)) {
                        val signInResponse = Gson().fromJson(
                            appVersionResponse.throwable?.message, AppVersionResponse::class.java
                        )
                        if (signInResponse.statusCode == 401) {
                            redirectToLogin()
                        }
                    } else {
                        showPopupWithFinish(
                            this, appVersionResponse.throwable?.message!!,
                            getString(R.string.text_info)
                        )
                    }
                } else {
                    handleAppVersion(appVersionResponse?.appVersionResponse?.data!!)
                }
            }
        })
    }

    private fun handleAppVersion(appVersion: AppVersionDataResponse) {
        if (appVersion.version != BuildConfig.VERSION_NAME) {
            downloadUrl = appVersion.downloadUrl
            if (appVersion.isForced) {
                showForceUpdatePopup()
            } else {
                showUpdatePopup()
            }
        } else {
            if (preferenceManager.isAuthenticated()) {
//                user has logged in. navigate to the home screen
                invokeIntent(Intent(this, HomeScreen::class.java))
            } else {
//                user hasn't logged in . navigate to the login screen
                invokeIntent(Intent(this, LoginActivity::class.java))
            }
        }
    }

    private fun invokeIntent(intent: Intent) {
        this.finish()
        startActivity(intent)
    }

    private fun showUpdatePopup() {

        val builder: AlertDialog.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AlertDialog.Builder(this, R.style.Theme_MaterialComponents_Light_Dialog)
            } else {
                AlertDialog.Builder(this)
            }
        builder.setTitle(getString(R.string.msg_title_update_available))
            .setMessage(getString(R.string.msg_download_new_version))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.text_update)) { _, _ ->
                checkPermission()
            }
            .setNegativeButton(getString(R.string.text_cancel)) { _, _ ->
                if (preferenceManager.isAuthenticated()) {
                    invokeIntent(Intent(this, HomeScreen::class.java))
                } else {
                    invokeIntent(Intent(this, LoginActivity::class.java))
                }
            }
            .show()
    }

    private fun showForceUpdatePopup() {

        val builder: AlertDialog.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AlertDialog.Builder(this, R.style.Theme_MaterialComponents_Light_Dialog)
            } else {
                AlertDialog.Builder(this)
            }
        builder.setTitle(getString(R.string.msg_title_update_available))
            .setMessage(getString(R.string.msg_download_new_version))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.text_update)) { _, _ ->
                checkPermission()
            }
            .show()
    }

    private fun showInstallPopup() {

        val builder: AlertDialog.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AlertDialog.Builder(this, R.style.Theme_MaterialComponents_Light_Dialog)
            } else {
                AlertDialog.Builder(this)
            }
        builder.setTitle(getString(R.string.msg_title_install_update))
            .setMessage(getString(R.string.msg_install_new_version))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.text_update)) { _, _ ->
                installApk()
                finish()
            }
            .show()
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_WRITE_STORAGE
            )
        } else {

            downloadTheFile()
        }
    }

    private fun downloadTheFile() {

        if (downloadUrl != null) {
            val configuration = ClientConfiguration()
            configuration.maxErrorRetry = 3
            configuration.connectionTimeout = 501000
            configuration.socketTimeout = 501000
            configuration.protocol = Protocol.HTTP

            val credentialsProvider = CognitoCachingCredentialsProvider(
                this, Constants.aws_identity_pool_id, Regions.US_EAST_2
            )

            val s3 = AmazonS3Client(credentialsProvider) //Changed
            s3.setRegion(Region.getRegion(Regions.US_EAST_2))

            val transferUtility = TransferUtility.builder()
                .context(applicationContext)
                .awsConfiguration(AWSMobileClient.getInstance().configuration)
                .s3Client(s3)
                .build()

            val folder =
                File("${Environment.getExternalStorageDirectory()}${Constants.folder_app_apk}")
            if (!folder.exists()) {
                folder.mkdir()
            }

            val file = File(
                "${Environment.getExternalStorageDirectory()}${Constants.folder_app_apk}",
                Constants.file_apk_apk
            )
            if (!file.exists()) {
                file.createNewFile()
            }

            val downloadObserver = transferUtility.download(Constants.s3_bucket, downloadUrl, file)

            // Attach a listener to get state updates
            downloadObserver.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState) {
                    if (state == TransferState.COMPLETED) {
                        // Handle a completed upload.
                        showInstallPopup()
                        splashViewModel.dialogStatus.value = View.GONE
                    } else if (state == TransferState.IN_PROGRESS) {
                        splashViewModel.dialogStatus.value = View.VISIBLE
                    }
                }

                override fun onProgressChanged(id: Int, current: Long, total: Long) {
                    try {
                        val done = (((current.toDouble() / total) * 100.0).toInt()) //as Int
                        splashViewModel.liveDownloadStatus.value = "Downloading.. $done %"
//                    showToast(this@SplashActivity, done.toString())
                        Log.d(TAG, "DOWNLOAD - - ID: $id, percent done = $done")
                    } catch (e: Exception) {
                        Log.e(TAG, "Trouble calculating progress percent", e)
                    }
                }

                override fun onError(id: Int, ex: Exception) {
                    showPopupWithFinish(
                        this@SplashActivity,
                        getString(R.string.msg_download_failed),
                        getString(R.string.text_error)
                    )
                    Log.d(TAG, "DOWNLOAD ERROR - - ID: $id - - EX: ${ex.message.toString()}")
                }
            })
            Log.d(TAG, "Bytes Transferred: ${downloadObserver.bytesTransferred}")

        } else {
            showPopupWithFinish(
                this@SplashActivity,
                getString(R.string.msg_download_failed),
                "Error"
            )
        }
    }

    private fun installApk() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            FileProvider.getUriForFile(
                this, BuildConfig.APPLICATION_ID + ".provider",
                File(
                    "${Environment.getExternalStorageDirectory()}${Constants.folder_app_apk}" +
                            Constants.file_apk_apk
                )
            ),
            Constants.file_mime_type
        )
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
    }

    private fun redirectToLogin() {

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun redirectToHome() {

        val intent = Intent(this, HomeScreen::class.java)
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_WRITE_STORAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadTheFile()
            }
        }
    }
}
