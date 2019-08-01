package pet.loyal.provider.util

import android.content.Context
import android.content.SharedPreferences
import pet.loyal.provider.R
import pet.loyal.provider.model.LoginUser
import java.nio.file.attribute.UserDefinedFileAttributeView

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
        setAuthenticated(true)
    }

    fun getLoginToken(): String {
        return sharedPreferences.getString(Constants.data_login_token, "")
    }

    fun setPushToken(pushToken : String){
        editor.putString(Constants.data_push_token, pushToken).commit()
    }

    fun getPushToken() : String {
        return sharedPreferences.getString(Constants.data_push_token, "default")
    }

    fun setDeviceId(pushToken : String){
        editor.putString(Constants.data_device_id, pushToken).commit()
    }

    fun getDeviceId() : String {
        return sharedPreferences.getString(Constants.data_device_id, "default")
    }

    fun saveUser(user: LoginUser){
        editor.putString(Constants.data_user_id, user.id)
        editor.putString(Constants.data_user_type, user.type)
        editor.putString(Constants.data_user_first_name, user.firstName)
        editor.putString(Constants.data_user_last_name, user.lastName)
        editor.putString(Constants.data_user_phone, user.phone)
        editor.putString(Constants.data_user_email, user.email)
        editor.putString(Constants.data_user_avatar, user.avatar).commit()
    }

    fun getUserId(): String{
        return sharedPreferences.getString(Constants.data_device_id, "")
    }

    fun getFacilityPhone(): String{
        return sharedPreferences.getString(Constants.data_facility_phone, "4565475")
    }

    fun getFacilityName(): String{
        return sharedPreferences.getString(Constants.data_facility_name, "Name")
    }
}