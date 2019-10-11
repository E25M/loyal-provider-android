package pet.loyal.provider.util

import android.content.Context
import android.content.SharedPreferences
import pet.loyal.provider.R
import pet.loyal.provider.model.Facility
import pet.loyal.provider.model.LoginUser

class PreferenceManager(context: Context) {

    var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(
            context.getString(R.string.app_name) + "_data",
            Context.MODE_PRIVATE
        )
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

    fun setPushToken(pushToken: String) {
        editor.putString(Constants.data_push_token, pushToken).commit()
    }

    fun getPushToken(): String {
        return sharedPreferences.getString(Constants.data_push_token, "default")
    }

    fun setDeviceId(pushToken: String) {
        editor.putString(Constants.data_device_id, pushToken).commit()
    }

    fun getDeviceId(): String {
        return sharedPreferences.getString(Constants.data_device_id, "default")
    }

    fun saveUser(user: LoginUser) {
        editor.putString(Constants.data_user_id, user.id)
        editor.putString(Constants.data_user_type, user.type)
        editor.putString(Constants.data_user_first_name, user.firstName)
        editor.putString(Constants.data_user_last_name, user.lastName)
        editor.putString(Constants.data_user_phone, user.phone)
        editor.putString(Constants.data_user_email, user.email)
        editor.putString(Constants.data_user_avatar, user.avatar).commit()
    }


    fun setTimeOutType(type: String) {
        editor.putString(Constants.data_timeout_type, type).commit()
    }

    fun getTimeOutTYpe(): String {
        return sharedPreferences.getString(Constants.data_timeout_type, "No Auto Logout")
    }

    fun getUserId(): String {
        return sharedPreferences.getString(Constants.data_device_id, "")
    }

    fun getFacilityPhone(): String {
        return sharedPreferences.getString(Constants.data_facility_phone, "")
    }

    fun getFacilityName(): String {
        return sharedPreferences.getString(Constants.data_facility_name, "")
    }

    fun saveFacility(facility: Facility) {
        editor.putString(Constants.data_facility_id, facility.id)
        editor.putString(Constants.data_facility_name, facility.name)
        editor.putString(Constants.data_facility_phone, facility.phone)
        editor.putString(Constants.data_facility_logo, facility.logo)
        editor.putString(Constants.data_facility_admin, facility.admin).commit()
    }

    fun getFacilityId(): String {
        return sharedPreferences.getString(Constants.data_facility_id, "default")
    }

    fun getfacilityLogo(): String {
        return sharedPreferences.getString(Constants.data_facility_logo, "-")
    }

    fun getUserType(): String {
        return sharedPreferences.getString(Constants.data_user_type, "default")
    }

    fun facilitySelected(): Boolean {
        return !sharedPreferences.getString(Constants.data_facility_id, "default")
            .equals("default", true)
    }

    fun deleteSession() {
        editor.remove(Constants.data_user_id)
            .remove(Constants.data_login_token)
            .remove(Constants.data_is_authenticated)
            .remove(Constants.data_user_type)
            .remove(Constants.data_user_first_name)
            .remove(Constants.data_user_last_name)
            .remove(Constants.data_user_phone)
            .remove(Constants.data_user_email)
            .remove(Constants.data_user_avatar)
            .remove(Constants.data_timeout_type)
            .commit()
    }
}