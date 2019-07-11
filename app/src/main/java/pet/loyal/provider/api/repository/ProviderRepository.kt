package pet.loyal.provider.api.repository

import androidx.lifecycle.LiveData
import okhttp3.RequestBody
import pet.loyal.provider.api.responses.AppVersionBaseResponse
import pet.loyal.provider.api.responses.LoginBaseResponse
import pet.loyal.provider.api.responses.SelfInviteBaseResponse

interface ProviderRepository {

    fun getAppVersion(): LiveData<AppVersionBaseResponse>

    fun selfInvite(requestBody: RequestBody, token: String):
            LiveData<SelfInviteBaseResponse>

    fun login(requestBody: RequestBody , token: String ): LiveData<LoginBaseResponse>
}