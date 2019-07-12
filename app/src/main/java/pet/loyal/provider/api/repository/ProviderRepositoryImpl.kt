package pet.loyal.provider.api.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import okhttp3.RequestBody
import pet.loyal.provider.BuildConfig
import pet.loyal.provider.api.responses.*
import pet.loyal.provider.api.service.ProviderAPIService
import pet.loyal.provider.util.getRequestHeaders
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProviderRepositoryImpl : ProviderRepository {

    var apiService: ProviderAPIService

    var retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.BASE_URL)
        .build()

    init {
        apiService = retrofit.create(ProviderAPIService::class.java)
    }

    override fun getAppVersion(): LiveData<AppVersionBaseResponse> {
        val commentLiveData: MutableLiveData<AppVersionBaseResponse> = MutableLiveData()
        val call: Call<AppVersionResponse> = apiService.getVersion()
        val baseResponse = AppVersionBaseResponse()

        call.enqueue(object : Callback<AppVersionResponse> {

            override fun onFailure(call: Call<AppVersionResponse>, t: Throwable) {
                baseResponse.throwable = t
                commentLiveData.value = baseResponse
            }

            override fun onResponse(
                call: Call<AppVersionResponse>,
                response: Response<AppVersionResponse>
            ) {
                if (response.isSuccessful) {
                    baseResponse.appVersionResponse = response.body()
                } else {
                    baseResponse.throwable = Throwable(response.errorBody()?.string())
                }
                commentLiveData.value = baseResponse
            }
        })
        return commentLiveData
    }

    override fun selfInvite(
        requestBody: RequestBody,
        token: String
    ): LiveData<SelfInviteBaseResponse> {
        val resetPasswordLiveData: MutableLiveData<SelfInviteBaseResponse> = MutableLiveData()
        val call: Call<SelfInviteResponse> =
            apiService.selfInvite(getRequestHeaders(token), requestBody)
        val baseResponse = SelfInviteBaseResponse()
        call.enqueue(object : Callback<SelfInviteResponse> {
            override fun onResponse(
                call: Call<SelfInviteResponse>,
                response: Response<SelfInviteResponse>
            ) {
                if (response.isSuccessful) {
                    baseResponse.selfInviteResponse = response.body()
                } else {
                    val resetPasswordResponse = Gson().fromJson(
                        response.errorBody()!!.string(), SelfInviteResponse::class.java
                    )
                    baseResponse.selfInviteResponse = resetPasswordResponse
                }
                resetPasswordLiveData.value = baseResponse
            }

            override fun onFailure(call: Call<SelfInviteResponse>, t: Throwable) {
                baseResponse.throwable = t
                resetPasswordLiveData.value = baseResponse
            }
        })

        return resetPasswordLiveData
    }


    override fun login(requestBody: RequestBody, token: String): LiveData<LoginBaseResponse> {
        val loginLiveData = MutableLiveData<LoginBaseResponse>()
        val call = apiService.login(getRequestHeaders(token), requestBody)
        val baseResponse = LoginBaseResponse()
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    baseResponse.loginResponse = response.body()!!
                } else {
                    baseResponse.throwable = Throwable(response.errorBody()?.string())
                }
                loginLiveData.value = baseResponse
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                baseResponse.throwable = t
                loginLiveData.value = baseResponse
            }
        })
        return loginLiveData
    }
}