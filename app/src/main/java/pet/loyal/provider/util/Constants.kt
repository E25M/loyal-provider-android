package pet.loyal.provider.util

class Constants {

    companion object{

        //static parameters
        const val data_push_token = "push_token"
        const val data_login_token = "login_token"
        const val data_device_id = "device_id"
        const val data_is_authenticated = "is_authenticated"

        // url endpoints
        const val url_apk = "tablet/"
        const val url_init = "tablet/init"
        const val url_invite = "tablet/invite"
        const val url_login = "tablet/login"

        // Login fail scenarios
        const val error_invalid_credentials = "invalid_credentials"
        const val error_user_deactivated  = "user_deactivated"
        const val error_unauthorized_device_for_user = "unauthorized_device_for_user"
        const val error_invalid_password = "invalid_password"
        const val error_user_not_found = "user_not_found"

        const val extra_url_apk = "extra.url.apk"
        const val extra_download_apk_status = "extra.download.apk.status"

        const val action_download_apk = "pet.loyal.provider.download.apk"
    }
}