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
                    baseResponse.throwable = Throwable(response.errorBody()?.string())
                }
//                else {
//                    val resetPasswordResponse = Gson().fromJson(
//                        response.errorBody()!!.string(), SelfInviteResponse::class.java)
//                    baseResponse.selfInviteResponse = resetPasswordResponse
//                }

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

    override fun getPetCardById(
        appointmentId: String,
        token: String
    ): LiveData<PetCardBaseResponse> {
        val petCardLiveData: MutableLiveData<PetCardBaseResponse> = MutableLiveData()
        val call: Call<PetCardResponse> = apiService.getPetCardById(
            getRequestHeaders(token),
            appointmentId
        )
        val baseResponse = PetCardBaseResponse()

        call.enqueue(object : Callback<PetCardResponse> {

            override fun onFailure(call: Call<PetCardResponse>, t: Throwable) {
                baseResponse.throwable = t
                petCardLiveData.value = baseResponse
            }

            override fun onResponse(
                call: Call<PetCardResponse>,
                response: Response<PetCardResponse>
            ) {
                if (response.isSuccessful) {
                    baseResponse.petCardResponse = response.body()
                } else {
                    baseResponse.throwable = Throwable(response.errorBody()?.string())
                }
                petCardLiveData.value = baseResponse
            }
        })
        return petCardLiveData
    }
  
    override fun savePTBMessage(
        requestBody: RequestBody,
        token: String
    ): LiveData<SavePTBMessageBaseResponse> {
        val savePTBMessageLiveData: MutableLiveData<SavePTBMessageBaseResponse> = MutableLiveData()
        val call: Call<SavePTBMessageResponse> =
            apiService.savePTBMessage(getRequestHeaders(token), requestBody)
        val baseResponse = SavePTBMessageBaseResponse()
        call.enqueue(object : Callback<SavePTBMessageResponse> {
            override fun onResponse(
                call: Call<SavePTBMessageResponse>,
                response: Response<SavePTBMessageResponse>
            ) {
                if (response.isSuccessful) {
                    baseResponse.savePTBMessageResponse = response.body()
                } else {
                    baseResponse.throwable = Throwable(response.errorBody()?.string())
                }
//                else {
//                    val resetPasswordResponse = Gson().fromJson(
//                        response.errorBody()!!.string(), SelfInviteResponse::class.java)
//                    baseResponse.selfInviteResponse = resetPasswordResponse
//                }

                savePTBMessageLiveData.value = baseResponse
            }

            override fun onFailure(call: Call<SavePTBMessageResponse>, t: Throwable) {
                baseResponse.throwable = t
                savePTBMessageLiveData.value = baseResponse
            }
        })

        return savePTBMessageLiveData
    }

    override fun getPetTrackingBoard(
        requestBody: RequestBody,
        token: String
    ): LiveData<PetTrackingBoardBaseResponse> {
        val petTrackingBoardResponse = MutableLiveData<PetTrackingBoardBaseResponse>()
        val call = apiService.getPetTrackingBoard(getRequestHeaders(token), requestBody)
        var baseResponse = PetTrackingBoardBaseResponse()

        call.enqueue(object : Callback<PetTrackingBoardResponse> {

            override fun onFailure(call: Call<PetTrackingBoardResponse>, t: Throwable) {
                baseResponse.throwable = t
                petTrackingBoardResponse.value = baseResponse
            }

            override fun onResponse(
                call: Call<PetTrackingBoardResponse>,
                response: Response<PetTrackingBoardResponse>
            ) {
                if (response.isSuccessful) {
                    baseResponse.petTrackingBoardResponse = response.body()
                } else {
                    baseResponse.throwable = Throwable(response.errorBody()?.string())
                }
                petTrackingBoardResponse.value = baseResponse
            }

        })

        return petTrackingBoardResponse
    }

    override fun getFacilityList(token: String): LiveData<GetFacilityBaseResponse> {
        val facilityListResponse = MutableLiveData<GetFacilityBaseResponse>()
        val call = apiService.getfacilityList(getRequestHeaders(token))

        val baseResponse = GetFacilityBaseResponse()

        call.enqueue(object : Callback<GetFacilityResponse> {
            override fun onResponse(
                call: Call<GetFacilityResponse>,
                response: Response<GetFacilityResponse>
            ) {
                if (response.isSuccessful) {
                    baseResponse.facilityResponse = response.body()
                } else {
                    baseResponse.throwable = Throwable(response.errorBody()?.string())
                }

                facilityListResponse.value = baseResponse
            }

            override fun onFailure(call: Call<GetFacilityResponse>, t: Throwable) {
                baseResponse.throwable = t
                facilityListResponse.value = baseResponse
            }
        })

        return facilityListResponse
    }
}