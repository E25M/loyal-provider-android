package pet.loyal.provider.view.login

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import pet.loyal.provider.R
import pet.loyal.provider.api.repository.ProviderRepository
import pet.loyal.provider.api.repository.RepositoryProvider
import pet.loyal.provider.api.responses.LoginBaseResponse

class LoginViewModel : ViewModel() {

    var progressBarVisibility = MutableLiveData<Int>()
    var emailError = MutableLiveData<String>()
    var passwordError = MutableLiveData<String>()
    var emailErrorEnabled = MutableLiveData<Boolean>()
    var passwordsErrorEnabled = MutableLiveData<Boolean>()
    var loginFields = LoginFields("", "")
    var loginResponse: MediatorLiveData<LoginBaseResponse>
    var providerRepository: ProviderRepository

    init {
        providerRepository = RepositoryProvider.provideProviderRepository()
        progressBarVisibility.value = View.GONE
        emailError.value = ""
        passwordError.value = ""
        emailErrorEnabled.value = true
        passwordsErrorEnabled.value = true
        loginResponse = MediatorLiveData()
    }


    fun onLoginClick(
        context: Context,
        pushToken: String,
        deviceId: String,
        loginToken: String
    ) {

        emailErrorEnabled.value = true
        passwordsErrorEnabled.value = true

        //  check if the username  is empty
        if (TextUtils.isEmpty(loginFields.email.trim())) {
            emailError.value = context.getString(R.string.msg_empty_username)
//            check if the email is valid
        }
//        else if (!(Patterns.EMAIL_ADDRESS.matcher(loginFields.email).matches())) {
//            emailError.value = "Invalid email address or phone number"
//        }
        else {
            emailErrorEnabled.value = false
        }

        //        check if the password is empty
        if (TextUtils.isEmpty(loginFields.password.trim())) {
            passwordError.value = context.getString(R.string.msg_empty_password)
        } else {
            passwordsErrorEnabled.value = false
        }

        if (passwordsErrorEnabled.value == false && emailErrorEnabled.value == false) {
//             everything is validated. make the api call
            loginUser(pushToken, deviceId, loginToken)
        } else {
            return
        }

    }


    private fun loginUser(
        pushToken: String,
        deviceId: String,
        loginToken: String
    ): LiveData<LoginBaseResponse> {

        progressBarVisibility.value = View.VISIBLE

        val jsonObject = JSONObject()
        jsonObject.put("email", loginFields.email)
        jsonObject.put("password", loginFields.password)
        jsonObject.put("deviceId", deviceId)
        jsonObject.put("pushToken", pushToken)

        val requestBody = RequestBody.create(
            MediaType.parse("text/plain"),
            jsonObject.toString()
        )

        val dataSource = providerRepository.login(requestBody, loginToken)
        loginResponse.addSource(
            dataSource
        ) { dataResponse ->
            if (this.loginResponse.hasActiveObservers()) {
                this.loginResponse.removeSource(dataSource)
            }
            this.loginResponse.setValue(dataResponse)
        }
        return loginResponse
    }

}