package pet.loyal.provider.util

class Constants {

    companion object {

        //static parameters
        const val data_push_token = "push_token"
        const val data_login_token = "login_token"
        const val data_device_id = "device_id"
        const val data_is_authenticated = "is_authenticated"
        const val data_user_id = "user_id"
        const val data_user_type = "user_type"
        const val data_user_first_name = "user_first_name"
        const val data_user_last_name = "user_last_name"
        const val data_user_phone = "user_phone"
        const val data_user_email = "user_email"
        const val data_user_designation = "user_designation"
        const val data_user_avatar = "user_avatar"

        const val data_facility_phone = "user_facility_phone"
        const val data_facility_name = "user_facility_name"
        const val data_facility_id = "user_facility_id"

        // url endpoints
        const val url_apk = "tablet/"
        const val url_init = "init"
        const val url_invite = "invite"
        const val url_login = "login"
        const val url_appointment_by_id = "ptb/appointment/"
        const val url_change_phase = "ptb/change-phrase"
        const val url_save_ptb_messages = "ptb/save/"
        const val url_get_ptb = "ptb/appointments"
        const val url_get_facility_list = "facility-list"
        const val upload_preset = "rt8cuu9q"

        const val query_appointment_id = "appointment-ID"

        // Login fail scenarios
        const val error_invalid_credentials = "invalid_credentials"
        const val error_user_deactivated = "user_deactivated"
        const val error_unauthorized_device_for_user = "unauthorized_device_for_user"
        const val error_invalid_password = "invalid_password"
        const val error_user_not_found = "user_not_found"

        const val extra_url_apk = "extra.url.apk"
        const val extra_download_apk_status = "extra.download.apk.status"
        const val extra_phase_list = "extra.phase.list"
        const val extra_phase_id = "extra.phase.id"
        const val extra_appointment_id = "extra.appointment.id"
        const val extra_pet_name = "extra.pet.name"

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

        //Phase message view types
        const val view_type_sent_message = 0
        const val view_type_custom_message = 1
        const val view_type_template_message = 2
        const val custom_message_character_limit = 100

        //select image
        const val folder_loyal = "/Loyal"
        const val captured_pic_name = "captured_pic_name"


        //        pet cards sort types
        const val sort_type_parent = "parent"
        const val sort_type_patient = "patient"
        const val sort_ascending = 1
        const val sort_descending = -1

        const val url_cloudinary_news_feed =
            "https://res.cloudinary.com/dktj00acj/image/upload/newsfeed/"

        const val intent_type_parent_sign_up = 0
        const val intent_type_patient_cards = 1
        const val intent_type_support = 2
        const val intent_type_settings = 3

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

        //phase change error message codes
        const val pet_is_not_active = "pet_is_not_active"
        const val parent_is_not_active = "parent_is_not_active"
        const val there_are_another_ongoing_appointments_for_this_pet =
            "there_are_another_ongoing_appointments_for_this_pet"

        // fragment types. this is used to identify the fragments that are needed
        const val fragment_type_home = 0
        const val fragment_type_pet_cards = 1
        const val fragment_type_settings = 2
        const val fragment_type_parent_sign_up = 3

    }
}