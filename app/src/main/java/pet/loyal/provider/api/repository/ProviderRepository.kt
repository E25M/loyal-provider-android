package pet.loyal.provider.api.repository

import androidx.lifecycle.LiveData
import okhttp3.RequestBody
import pet.loyal.provider.api.responses.*
import pet.loyal.provider.model.Appointment

interface ProviderRepository {

    fun getAppVersion(): LiveData<AppVersionBaseResponse>

    fun selfInvite(requestBody: RequestBody, token: String):
            LiveData<SelfInviteBaseResponse>

    fun login(requestBody: RequestBody, token: String):
            LiveData<LoginBaseResponse>

    fun getPetCardById(appointmentId: String, token: String):
            LiveData<PetCardBaseResponse>

    fun getPetTrackingBoard(requestBody: RequestBody, token: String):
            LiveData<PetTrackingBoardBaseResponse>

    fun getFacilityList(token: String) : LiveData<GetFacilityBaseResponse>
}