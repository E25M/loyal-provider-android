package pet.loyal.provider.api.service

import pet.loyal.provider.api.responses.AppVersionResponse
import pet.loyal.provider.util.Constants
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap

interface ProviderAPIService {

    @GET(Constants.url_init)
    fun getVersion(@HeaderMap hashMap: HashMap<String, String>): Call<AppVersionResponse>


}