package pet.loyal.provider.api.service

import okhttp3.RequestBody
import pet.loyal.provider.api.responses.AppVersionResponse
import pet.loyal.provider.api.responses.LoginResponse
import pet.loyal.provider.api.responses.PetCardResponse
import pet.loyal.provider.api.responses.SelfInviteResponse
import pet.loyal.provider.util.Constants
import retrofit2.Call
import retrofit2.http.*

interface ProviderAPIService {

    @GET(Constants.url_init)
    fun getVersion(): Call<AppVersionResponse>

    @GET(Constants.url_appointment_by_id + "{appointmentId}")
    fun getPetCardById(@HeaderMap hashMap: HashMap<String, String>,
                       @Path("appointmentId") appointmentId: String):
            Call<PetCardResponse>

    @POST(Constants.url_invite)
    fun selfInvite(@HeaderMap hashMap: HashMap<String, String>, @Body requestBody: RequestBody):
            Call<SelfInviteResponse>

    @POST(Constants.url_login)
    fun login(@HeaderMap hashMap: HashMap<String, String>, @Body requestBody: RequestBody):
            Call<LoginResponse>
}