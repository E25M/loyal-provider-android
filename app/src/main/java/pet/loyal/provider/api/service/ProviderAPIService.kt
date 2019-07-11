package pet.loyal.provider.api.service

import androidx.lifecycle.LiveData
import okhttp3.RequestBody
import okhttp3.ResponseBody
import pet.loyal.provider.api.responses.AppVersionResponse
import pet.loyal.provider.api.responses.LoginResponse
import pet.loyal.provider.api.responses.SelfInviteResponse
import pet.loyal.provider.util.Constants
import retrofit2.Call
import retrofit2.http.*

interface ProviderAPIService {

    @GET(Constants.url_apk + "{apkPath}")
    @Streaming
    fun downloadAPK(@Query("apkPath") apkPath: String): Call<ResponseBody>

    @GET(Constants.url_init)
    fun getVersion(): Call<AppVersionResponse>

    @POST(Constants.url_invite)
    fun selfInvite(@HeaderMap hashMap: HashMap<String, String>, @Body requestBody: RequestBody):
            Call<SelfInviteResponse>

    @POST(Constants.url_login)
    fun login(@HeaderMap hashMap: HashMap<String, String>, @Body requestBody: RequestBody):
            Call<LoginResponse>
}