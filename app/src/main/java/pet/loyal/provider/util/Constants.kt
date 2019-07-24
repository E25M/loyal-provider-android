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

        const val s3_bucket = "loyal-provider"
        const val folder_app_apk = "/Loyal Provider"
        const val file_apk_apk = "/app.apk"
        const val aws_identity_pool_id = "us-east-2:9b1ee60f-ad69-4343-9ebf-9154b08e6cd6"
        const val file_mime_type = "application/vnd.android.package-archive"

        //Add status
        const val self_invite_parent_added_success = "parent-added-success"
        const val self_invite_parent_exist_facility_not_exist = "parent-exist-facility-not-exist"
        const val self_invite_parent_already_exist = "parent-already-exist"
        const val self_invite_parent_already_exist_inactive = "parent-already-exist"

        //sample parameters
        const val sample_first_name = "Loyal User"
        const val sample_last_name = "."

        const val intent_type_parent_sign_up  = 0
        const val intent_type_patient_cards = 1
        const val intent_type_support  = 2
        const val intent_type_settings  = 3


        // phases
        const val phase_expected = "Expected"
        const val phase_check_in = "Check-In"
        const val phase_diagnostics = "Diagnostics"
        const val phase_preperation = "Preparation"
        const val phase_procedure = "Procedure "
        const val phase_dental = "Dental "
        const val phase_surgery = "Surgery"
        const val phase_recovery = "Recovery"
        const val phase_hospital = "Hospital"
        const val phase_boarding = "Boarding"
        const val phase_discharge = "Discharge"
        const val phase_complete = "Complete"

    }
}