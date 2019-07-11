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

    var providerRepository: ProviderRepository = RepositoryProvider.provideProviderRepository()
    var dialogStatus = MutableLiveData<Int>()
    var selfInviteBaseResponse: MediatorLiveData<SelfInviteBaseResponse> = MediatorLiveData()
    var liveEmailOrPhone: MutableLiveData<String> = MutableLiveData()

    fun selfInvite(token: String, emailOrPhone: String): LiveData<SelfInviteBaseResponse> {

        dialogStatus.value = View.VISIBLE

        val jsonObject = JSONObject().put("emailOrPhone", emailOrPhone)
        val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())

        val dataSource: LiveData<SelfInviteBaseResponse> = providerRepository.selfInvite(requestBody, token)
        selfInviteBaseResponse.addSource(dataSource) { dataResponse ->
            if (this.selfInviteBaseResponse.hasActiveObservers()) {
                this.selfInviteBaseResponse.removeSource(dataSource)
            }
            this.selfInviteBaseResponse.setValue(dataResponse)
        }
        return selfInviteBaseResponse
    }
}