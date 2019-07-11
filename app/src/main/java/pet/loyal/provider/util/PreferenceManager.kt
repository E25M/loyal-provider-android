package pet.loyal.provider.util

import android.content.Context
import android.content.SharedPreferences
import pet.loyal.provider.R

class PreferenceManager(context: Context) {

    var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name) + "_data", Context.MODE_PRIVATE)
    var editor: SharedPreferences.Editor

    init {
        editor = sharedPreferences.edit()
    }

    fun isAuthenticated(): Boolean {
        return sharedPreferences.getBoolean(Constants.data_is_authenticated, false)
    }

    fun setAuthenticated(isAuthenticated: Boolean) {
        editor.putBoolean(Constants.data_is_authenticated, isAuthenticated).commit()
    }

    fun saveLoginToken(loginToken: String) {
        editor.putString(Constants.data_login_token, "Bearer $loginToken").commit()
    }

    fun getLoginToken(): String {
        return sharedPreferences.getString(Constants.data_login_token, "")
    }
}