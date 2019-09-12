package pet.loyal.provider.view.home

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pet.loyal.provider.api.repository.ProviderRepository
import pet.loyal.provider.api.repository.RepositoryProvider
import pet.loyal.provider.api.responses.CommonBaseResponse
import pet.loyal.provider.api.responses.LoginBaseResponse

class HomeScreenViewModel : ViewModel() {

    var toolbarVisibility = MutableLiveData<Int>()
    var progressBarVisibility = MutableLiveData<Int>()
    var logoutResponse: MediatorLiveData<CommonBaseResponse>
    var providerRepository: ProviderRepository
    var logo = MutableLiveData<String>()

    init {
        toolbarVisibility.value = View.VISIBLE
        progressBarVisibility.value = View.GONE
        logoutResponse = MediatorLiveData()
        providerRepository = RepositoryProvider.provideProviderRepository()
    }

    fun logOut(token: String): LiveData<CommonBaseResponse> {
        progressBarVisibility.value = View.VISIBLE

        val dataSource = providerRepository.logOut(token)
        logoutResponse.addSource(dataSource) { listResponse ->
            if (logoutResponse.hasActiveObservers()) {
                logoutResponse.removeSource(dataSource)
            }
            logoutResponse.value = listResponse
        }

        return logoutResponse
    }

}