package pet.loyal.provider.api.repository

import androidx.lifecycle.LiveData
import okhttp3.RequestBody
import pet.loyal.provider.api.responses.AppVersionBaseResponse
import pet.loyal.provider.api.responses.LoginBaseResponse
import pet.loyal.provider.api.responses.PetCardBaseResponse
import pet.loyal.provider.api.responses.SelfInviteBaseResponse
import pet.loyal.provider.model.Appointment

interface ProviderRepository {

    fun getAppVersion(): LiveData<AppVersionBaseResponse>

    fun selfInvite(requestBody: RequestBody, token: String):
            LiveData<SelfInviteBaseResponse>

    fun login(requestBody: RequestBody , token: String ): LiveData<LoginBaseResponse>

    fun getPetCardById(appointmentId: String, token: String): LiveData<PetCardBaseResponse>
}