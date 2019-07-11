package pet.loyal.provider.view.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import pet.loyal.provider.api.repository.ProviderRepository
import pet.loyal.provider.api.repository.RepositoryProvider
import pet.loyal.provider.api.responses.AppVersionBaseResponse

class SplashViewModel: ViewModel() {

    private var repository: ProviderRepository = RepositoryProvider.provideProviderRepository()
    var appVersionResponse: MediatorLiveData<AppVersionBaseResponse> = MediatorLiveData()

    fun getAppVersion(): LiveData<AppVersionBaseResponse>{

        val dataSource = repository.getAppVersion()

        appVersionResponse.addSource(dataSource) { response ->
            if (this.appVersionResponse.hasActiveObservers()) {
                this.appVersionResponse.removeSource(dataSource)
            }
            this.appVersionResponse.value = response
        }

        return appVersionResponse
    }
}