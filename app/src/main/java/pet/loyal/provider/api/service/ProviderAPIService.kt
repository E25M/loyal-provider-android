package pet.loyal.provider.api.service

import okhttp3.RequestBody
import pet.loyal.provider.api.responses.*
import pet.loyal.provider.util.Constants
import retrofit2.Call
import retrofit2.http.*

interface ProviderAPIService {

    @GET(Constants.url_init)
    fun getVersion(): Call<AppVersionResponse>

    @GET(Constants.url_appointment_by_id + "{appointmentId}")
    fun getPetCardById(
        @HeaderMap hashMap: HashMap<String, String>,
        @Path("appointmentId") appointmentId: String
    ): Call<PetCardResponse>

    @POST(Constants.url_invite)
    fun selfInvite(@HeaderMap hashMap: HashMap<String, String>, @Body requestBody: RequestBody):
            Call<SelfInviteResponse>

    @POST(Constants.url_login)
    fun login(@HeaderMap hashMap: HashMap<String, String>, @Body requestBody: RequestBody):
            Call<LoginResponse>

    @POST(Constants.url_save_ptb_messages)
    fun savePTBMessage(@HeaderMap hashMap: HashMap<String, String>, @Body requestBody: RequestBody):
            Call<SavePTBMessageResponse>

    @POST(Constants.url_change_phase)
    fun changePhase(@HeaderMap hashMap: HashMap<String, String>, @Body requestBody: RequestBody):
            Call<PhaseChangeResponse>

    @POST(Constants.url_get_ptb)
    fun getPetTrackingBoard(
        @HeaderMap hashMap: HashMap<String, String>, @Body requestBody: RequestBody
    ): Call<PetTrackingBoardResponse>

    @GET(Constants.url_get_facility_list)
    fun getfacilityList(
        @HeaderMap hashMap: HashMap<String, String>
    ): Call<GetFacilityResponse>

    @GET(Constants.url_get_phases)
    fun getPhases(
        @HeaderMap hashMap: HashMap<String, String>
    ): Call<GetPhaseListResponse>

    @POST(Constants.url_save_facility)
    fun saveFacility(
        @HeaderMap hashMap: HashMap<String, String>, @Body requestBody: RequestBody
    ): Call<CommonResponse>

    @POST(Constants.url_logout)
    fun logOut(
        @HeaderMap hashMap: HashMap<String, String>
    ): Call<CommonResponse>

    @GET(Constants.url_facility + "{facilityId}")
    fun updateFacility(
        @HeaderMap hashMap: HashMap<String, String> ,
        @Path("facilityId") facilityId: String
    ): Call<UpdateFacilityResponse>
}