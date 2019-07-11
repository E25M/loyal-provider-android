package pet.loyal.provider.view.login

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    var loginResponse : MutableLiveData<LoginBaseResponse>
    var providerRepository : ProviderRepository

    init {
        providerRepository = RepositoryProvider.provideProviderRepository()
        progressBarVisibility.value = View.GONE
        emailError.value = ""
        passwordError.value = ""
        emailErrorEnabled.value = true
        passwordsErrorEnabled.value = true
        loginResponse = MutableLiveData()
    }


    fun onLogin(context : Context){

        emailErrorEnabled.value = true
        passwordsErrorEnabled.value = true

        //  check if the username  is empty
        if (TextUtils.isEmpty(loginFields.email)) {
            emailError.value = context.getString(R.string.msg_empty_username)
//            check if the email is valid
        } else if (!(Patterns.EMAIL_ADDRESS.matcher(loginFields.email).matches())) {
            emailError.value = "Invalid email address or phone number"
        }
        else {
            emailErrorEnabled.value = false
        }

        //        check if the password is empty
        if (TextUtils.isEmpty(loginFields.password)) {
            passwordError.value = context.getString(R.string.msg_empty_password)
        } else {
            passwordsErrorEnabled.value = false
        }

        if (passwordsErrorEnabled.value == false && emailErrorEnabled.value == false) {
//             everything is validated. make the api call
        } else {
            return
        }
    }


}