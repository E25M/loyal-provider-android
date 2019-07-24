package pet.loyal.provider.view.editpetcard

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pet.loyal.provider.api.repository.ProviderRepository
import pet.loyal.provider.api.repository.RepositoryProvider
import pet.loyal.provider.api.responses.PetCardBaseResponse
import pet.loyal.provider.api.responses.PetCardResponse

class EditPetCardViewModel : ViewModel() {

    private var providerRepository: ProviderRepository =
        RepositoryProvider.provideProviderRepository()
    var liveProgressDialog : MutableLiveData<Int> = MutableLiveData()
    var petCardResponse: MediatorLiveData<PetCardBaseResponse> = MediatorLiveData()

    val livePetName = MutableLiveData<String>()
    val liveBreedSpecies = MutableLiveData<String>()
    val liveGendar = MutableLiveData<String>()
    val livePhase = MutableLiveData<String>()
    val liveColor = MutableLiveData<Int>()
    val livePetImage = MutableLiveData<String>()

    init {
        liveProgressDialog.value = View.GONE
    }

    fun getPetCardById(appointmentId: String, token: String): MutableLiveData<PetCardBaseResponse>{

        liveProgressDialog.value = View.VISIBLE

        val dataSource: LiveData<PetCardBaseResponse> = providerRepository.getPetCardById(
            appointmentId, token)

        petCardResponse.addSource(dataSource) { dataResponse ->
            if (this.petCardResponse.hasActiveObservers()) {
                this.petCardResponse.removeSource(dataSource)
            }
            this.petCardResponse.setValue(dataResponse)
        }
        return petCardResponse
    }
}
