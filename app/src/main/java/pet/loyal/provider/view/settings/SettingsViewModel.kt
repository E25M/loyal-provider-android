package pet.loyal.provider.view.settings

import android.view.View
import androidx.lifecycle.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import pet.loyal.provider.api.repository.ProviderRepository
import pet.loyal.provider.api.repository.RepositoryProvider
import pet.loyal.provider.api.responses.CommonBaseResponse
import pet.loyal.provider.api.responses.GetFacilityBaseResponse
import pet.loyal.provider.api.responses.LoginBaseResponse

class SettingsViewModel : ViewModel() {

    var progressBarVisibility = MutableLiveData<Int>()
    var facilityListResponse: MediatorLiveData<GetFacilityBaseResponse>
    var saveFacilityResponse: MediatorLiveData<CommonBaseResponse>
    var providerRepository: ProviderRepository
    var applyButtonEnabled = MutableLiveData<Boolean>()
    var selectedFacility = MutableLiveData<String>()

    init {
        progressBarVisibility.value = View.GONE
        facilityListResponse = MediatorLiveData()
        saveFacilityResponse = MediatorLiveData()
        providerRepository = RepositoryProvider.provideProviderRepository()
        applyButtonEnabled.value = false
        selectedFacility.value = ""
    }


    fun getFacilityList(token: String): LiveData<GetFacilityBaseResponse> {

        this.progressBarVisibility.value = View.VISIBLE

        val dataSource = providerRepository.getFacilityList(token)
        facilityListResponse.addSource(dataSource) { facilityBaseResponse ->
            if (facilityListResponse.hasActiveObservers()) {
                facilityListResponse.removeSource(dataSource)
            }
            facilityListResponse.value = facilityBaseResponse
        }
        return facilityListResponse
    }


    fun saveDeviceFacility(token: String, deviceId: String, facilityId: String)
            : LiveData<CommonBaseResponse> {

        this.progressBarVisibility.value = View.VISIBLE

        val dataJson = JSONObject()
        dataJson.put("deviceId", deviceId)
        dataJson.put("facilityId", facilityId)
        val requestBody =
            RequestBody.create(MediaType.parse("text/plain"), dataJson.toString())

        val dataSource = providerRepository.saveDeviceFacility(requestBody, token)
        saveFacilityResponse.addSource(dataSource) { facilityBaseResponse ->
            if (saveFacilityResponse.hasActiveObservers()) {
                saveFacilityResponse.removeSource(dataSource)
            }
            saveFacilityResponse.value = facilityBaseResponse
        }
        return saveFacilityResponse
    }
}