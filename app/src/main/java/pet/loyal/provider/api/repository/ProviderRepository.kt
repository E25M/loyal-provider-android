package pet.loyal.provider.api.repository

import androidx.lifecycle.LiveData
import okhttp3.RequestBody
import pet.loyal.provider.api.responses.*

interface ProviderRepository {

    fun getAppVersion(): LiveData<AppVersionBaseResponse>

    fun selfInvite(requestBody: RequestBody, token: String):
            LiveData<SelfInviteBaseResponse>

    fun login(requestBody: RequestBody, token: String):
            LiveData<LoginBaseResponse>
  
    fun getPetCardById(appointmentId: String, token: String): LiveData<PetCardBaseResponse>

    fun savePTBMessage(requestBody: RequestBody , token: String ) :
            LiveData<SavePTBMessageBaseResponse>

    fun changePhase(requestBody: RequestBody , token: String ) :LiveData<PhaseChangeBaseResponse>


    fun getPetTrackingBoard(requestBody: RequestBody, token: String):
            LiveData<PetTrackingBoardBaseResponse>

    fun getFacilityList(token: String) : LiveData<GetFacilityBaseResponse>

    fun getPhaseList(token: String) : LiveData<GetPhaseListBaseResponse>

    fun saveDeviceFacility(requestBody: RequestBody , token: String) : LiveData<CommonBaseResponse>

    fun logOut(token: String) : LiveData<CommonBaseResponse>

    fun updateFacility(token: String) : LiveData<UpdateFacilityBaseResponse>

}