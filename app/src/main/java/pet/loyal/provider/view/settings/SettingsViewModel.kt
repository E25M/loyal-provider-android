package pet.loyal.provider.view.settings

import android.view.View
import androidx.lifecycle.*
import pet.loyal.provider.api.repository.ProviderRepository
import pet.loyal.provider.api.repository.RepositoryProvider
import pet.loyal.provider.api.responses.GetFacilityBaseResponse
import pet.loyal.provider.api.responses.LoginBaseResponse

class SettingsViewModel : ViewModel() {

    var progressBarVisibility = MutableLiveData<Int>()
    var facilityListResponse: MediatorLiveData<GetFacilityBaseResponse>
    var providerRepository : ProviderRepository
    var applyButtonEnabled = MutableLiveData<Boolean>()
    var selectedFacility = MutableLiveData<String>()

    init {
        progressBarVisibility.value = View.GONE
        facilityListResponse = MediatorLiveData()
        providerRepository = RepositoryProvider.provideProviderRepository()
        applyButtonEnabled.value = false
        selectedFacility.value = ""
    }


    fun getFacilityList(token : String) : LiveData<GetFacilityBaseResponse>{

        this.progressBarVisibility.value = View.VISIBLE

        val dataSource =  providerRepository.getFacilityList(token)
        facilityListResponse.addSource(dataSource) {
            facilityBaseResponse ->
            if (facilityListResponse.hasActiveObservers()){
                facilityListResponse.removeSource(dataSource)
            }
            facilityListResponse.value = facilityBaseResponse
        }
        return facilityListResponse
    }
}