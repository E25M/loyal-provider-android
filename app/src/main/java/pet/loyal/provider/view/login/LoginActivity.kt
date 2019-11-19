package pet.loyal.provider.view.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import org.json.JSONException
import pet.loyal.provider.R
import pet.loyal.provider.api.responses.CommonResponse
import pet.loyal.provider.api.responses.LoginResponse
import pet.loyal.provider.databinding.LayoutLoginBinding
import pet.loyal.provider.util.Constants
import pet.loyal.provider.util.PreferenceManager
import pet.loyal.provider.util.showToast
import java.net.ConnectException
import android.text.InputFilter
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.layout_login.*
import pet.loyal.provider.view.home.HomeScreen
import pet.loyal.provider.view.selfinvite.SelfInviteFragment


class LoginActivity : AppCompatActivity() {

    lateinit var layoutLoginBinding: LayoutLoginBinding
    lateinit var viewModel: LoginViewModel
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.loginResponse.observe(this, Observer { loginResponse ->
            // check if the response has sign in data
            viewModel.progressBarVisibility.value = View.GONE
            if (loginResponse?.throwable != null) {
                handleError(loginResponse.throwable)
            } else if (loginResponse?.loginResponse != null) {
                saveUserData(loginResponse.loginResponse)
            } else {
                handleError(Throwable(""))
            }
        })
    }

    private fun saveUserData(loginResponse: LoginResponse) {
        preferenceManager.saveLoginToken(loginResponse.data.token)
        preferenceManager.saveUser(loginResponse.data.user)
//        Log.i("Token", preferenceManager.getLoginToken())
//        Log.i("DeviceId", preferenceManager.getDeviceId())
        invokeIntent(Intent(this, HomeScreen::class.java))
    }

    private fun initDataBinding() {
        preferenceManager = PreferenceManager(this)
        layoutLoginBinding = DataBindingUtil.setContentView(this, R.layout.layout_login)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        layoutLoginBinding.viewModel = this.viewModel
        layoutLoginBinding.lifecycleOwner = this

        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            //            for (i in start until end) {
//                if (Character.isWhitespace(source[i])) {
//                    return@InputFilter ""
//                }
//            }
//            null
            source.toString().filterNot { it.isWhitespace() }
        }

        txt_login_username.filters = (arrayOf(filter))
    }

    fun onLoginAttempt(view: View) {
        viewModel.onLoginClick(
            this,
            preferenceManager.getPushToken(),
            preferenceManager.getDeviceId(),
            preferenceManager.getLoginToken()
        )
    }


    private fun handleError(throwable: Throwable?) {

        var errorMessage: String = ""
        var errorTitle = ""

        try {
            val errorResponse =
                Gson().fromJson(throwable?.message, CommonResponse::class.java)
            if (throwable is ConnectException) {
                errorMessage = getString(R.string.error_no_connection)
            } else if (errorResponse != null) {

                when (errorResponse.errorMessage) {
                    Constants.error_user_deactivated -> {
                        errorMessage = getString(R.string.msg_inactive_account)
                    }
                    else -> {
                        errorMessage = getString(R.string.msg_invalid_credentials)
                    }
                }
            } else {
                errorMessage = getString(R.string.msg_invalid_credentials)
            }
        } catch (e: JSONException) {
            errorMessage = getString(R.string.msg_invalid_credentials)
        }

        showToast(this, errorMessage)
        viewModel.progressBarVisibility.value = View.GONE
    }

    private fun invokeIntent(intent: Intent) {
        this.finish()
        startActivity(intent)
    }
}
