package pet.loyal.provider.view.mainmenu

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pet.loyal.provider.api.repository.ProviderRepository
import pet.loyal.provider.api.repository.RepositoryProvider
import pet.loyal.provider.api.responses.UpdateFacilityBaseResponse
import pet.loyal.provider.util.showToast

class MainMenuViewModel : ViewModel() {

    var disableAlpha: MutableLiveData<Float> = MutableLiveData()
    var enableAlpha: MutableLiveData<Float> = MutableLiveData()
    var updateFacilityBaseResponse: MediatorLiveData<UpdateFacilityBaseResponse>
    var repository: ProviderRepository

    init {
        disableAlpha.value = 0.2f
        enableAlpha.value = 1f
        updateFacilityBaseResponse = MediatorLiveData()
        repository = RepositoryProvider.provideProviderRepository()
    }

    fun startIntent(action: Int, context: Context) {
        showToast(context, " number is : $action")
    }

    fun updateFacility(token: String, facilityId: String) {
        val dataSource =
            repository.updateFacility(token, facilityId)
        updateFacilityBaseResponse.addSource(dataSource) { response ->
            if (this.updateFacilityBaseResponse.hasActiveObservers()) {
                this.updateFacilityBaseResponse.removeSource(dataSource)
            }
            this.updateFacilityBaseResponse.value = response
        }
    }

}
