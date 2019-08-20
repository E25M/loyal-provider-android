package pet.loyal.provider.view.selfinvite

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import pet.loyal.provider.api.repository.ProviderRepository
import pet.loyal.provider.api.repository.RepositoryProvider
import pet.loyal.provider.api.responses.SelfInviteBaseResponse

class SelfInviteViewModel: ViewModel() {

    private var providerRepository: ProviderRepository = RepositoryProvider.provideProviderRepository()
    var dialogStatus = MutableLiveData<Int>()
    var selfInviteBaseResponse: MediatorLiveData<SelfInviteBaseResponse> = MediatorLiveData()
    var liveEmailOrPhone: MutableLiveData<String> = MutableLiveData()
    val isEmailPhoneError = MutableLiveData<Boolean>()
    val emailPhoneError = MutableLiveData<String>()

    fun selfInvite(token: String, firstName:String, lastName: String, confirm: Boolean,
                   emailOrPhone: String, facilityId:String): LiveData<SelfInviteBaseResponse> {

        dialogStatus.value = View.VISIBLE

        val jsonObject = JSONObject()
        jsonObject.put("firstName", firstName)
        jsonObject.put("lastName", lastName)
        jsonObject.put("phoneEmail", emailOrPhone)
        jsonObject.put("addToCurrentAccount", confirm)
        jsonObject.put("facility", facilityId)

        val requestBody = RequestBody.create(MediaType.parse("application/json"),
            jsonObject.toString())

        val dataSource: LiveData<SelfInviteBaseResponse> = providerRepository.selfInvite(
            requestBody, token)

        selfInviteBaseResponse.addSource(dataSource) { dataResponse ->
            if (this.selfInviteBaseResponse.hasActiveObservers()) {
                this.selfInviteBaseResponse.removeSource(dataSource)
            }
            this.selfInviteBaseResponse.setValue(dataResponse)
        }
        return selfInviteBaseResponse
    }
}