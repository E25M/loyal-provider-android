package pet.loyal.provider.view.phasechange

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
import pet.loyal.provider.api.responses.PhaseChangeBaseResponse

class PhaseListViewModel: ViewModel() {

    private var providerRepository: ProviderRepository =
        RepositoryProvider.provideProviderRepository()
    var dialogStatus = MutableLiveData<Int>()
    var phaseChangeBaseResponse: MediatorLiveData<PhaseChangeBaseResponse> = MediatorLiveData()

    fun changePhase(appointmentId: String, phase:Int, token: String): LiveData<PhaseChangeBaseResponse>{
        dialogStatus.value = View.VISIBLE

        val jsonObject = JSONObject()
        jsonObject.put("appointmentId", appointmentId)
        jsonObject.put("phase", phase)

        val requestBody = RequestBody.create(
            MediaType.parse("application/json"),
            jsonObject.toString())

        val dataSource: LiveData<PhaseChangeBaseResponse> = providerRepository.changePhase(
            requestBody, token)

        phaseChangeBaseResponse.addSource(dataSource) { dataResponse ->
            if (this.phaseChangeBaseResponse.hasActiveObservers()) {
                this.phaseChangeBaseResponse.removeSource(dataSource)
            }
            this.phaseChangeBaseResponse.setValue(dataResponse)
        }
        return phaseChangeBaseResponse
    }
}