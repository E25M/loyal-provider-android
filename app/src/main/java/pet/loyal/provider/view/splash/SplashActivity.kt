package pet.loyal.provider.view.splash

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.crashlytics.android.Crashlytics
import com.google.gson.Gson
import io.fabric.sdk.android.Fabric
import pet.loyal.client.api.response.AppVersionDataResponse
import pet.loyal.provider.BuildConfig
import pet.loyal.provider.api.responses.AppVersionResponse
import pet.loyal.provider.services.DownloadService
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import pet.loyal.provider.model.Download
import android.content.BroadcastReceiver
import android.content.Context
import android.widget.Toast
import pet.loyal.provider.R
import pet.loyal.provider.util.*
import pet.loyal.provider.view.home.HomeScreen
import pet.loyal.provider.view.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 1

    private lateinit var splashViewModel: SplashViewModel
    private lateinit var preferenceManager: PreferenceManager

    companion object {
        private val TAG = SplashActivity.javaClass.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initDataBinding()

        setObservers()
        splashViewModel.getAppVersion()
    }

    private fun initDataBinding() {
        splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        preferenceManager = PreferenceManager(this)

        Fabric.with(this, Crashlytics())
    }

    private fun setObservers(){
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
                        showPopupWithFinish(this, appVersionResponse.throwable?.message!!,
                            "Info")
                    }
                } else {
                    handleAppVersion(appVersionResponse?.appVersionResponse?.data!!)
                }
            }
        })
    }

    private fun handleAppVersion(appVersion: AppVersionDataResponse) {

        if (appVersion.version != BuildConfig.VERSION_NAME){
            showForceUpdatePopup()
        }else{
            if (preferenceManager.isAuthenticated()){
//                user has logged in. navigate to the home screen
                invokeIntent(Intent(this, HomeScreen::class.java))
            }else{
//                user hasn't logged in . navigate to the login screen
                invokeIntent(Intent(this, LoginActivity::class.java))
            }
        }
    }

    private fun invokeIntent(intent: Intent){
        this.finish()
        startActivity(intent)
    }

    private fun showForceUpdatePopup() {

        val builder: AlertDialog.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlertDialog.Builder(this, R.style.Theme_MaterialComponents_Light_Dialog)
        } else {
            AlertDialog.Builder(this)
        }
        builder.setTitle(getString(R.string.msg_title_update_available))
            .setMessage(getString(R.string.msg_install_new_version))
            .setCancelable(false)
            .setPositiveButton("Update") { _, _ ->
                downloadAPK()
                finish()
            }
            .setNegativeButton("Cancel"){ _,_ ->
                finish()
            }
            .show()
    }

    private fun downloadAPK(){
        registerReceiver()
        val intent = Intent(this, DownloadService::class.java)
        startService(intent)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            if (intent.action == Constants.action_download_apk) {
                val download = intent.getParcelableExtra<Download>(Constants.extra_download_apk_status)
                if (download.progress == 100) {
                    showToast(this@SplashActivity, "Download completed!")
                } else {

                }
            }
        }
    }

    private fun registerReceiver() {
        val bManager = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.action_download_apk)
        bManager.registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun redirectToLogin() {

//        val intent = Intent(this, LoginActivity::class.java)
//        startActivity(intent)
        finish()
    }
}
