package pet.loyal.provider.view.editpetcard

import android.graphics.drawable.Drawable
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import pet.loyal.provider.api.repository.ProviderRepository
import pet.loyal.provider.api.repository.RepositoryProvider
import pet.loyal.provider.api.responses.PetCardBaseResponse
import pet.loyal.provider.api.responses.PetCardResponse
import pet.loyal.provider.api.responses.SavePTBMessageBaseResponse
import pet.loyal.provider.api.responses.SavePTBMessageResponse
import pet.loyal.provider.model.RequestPTBMessage

class EditPetCardViewModel : ViewModel() {

    private var providerRepository: ProviderRepository =
        RepositoryProvider.provideProviderRepository()
    var liveProgressDialog : MutableLiveData<Int> = MutableLiveData()
    var liveProgressPercentage : MutableLiveData<Int> = MutableLiveData()
    var petCardResponse: MediatorLiveData<PetCardBaseResponse> = MediatorLiveData()
    var savePTBMessageResponse: MediatorLiveData<SavePTBMessageBaseResponse> = MediatorLiveData()

    val livePetName = MutableLiveData<String>()
    val liveBreedSpecies = MutableLiveData<String>()
    val liveGendar = MutableLiveData<String>()
    val livePhase = MutableLiveData<String>()
    val liveColor = MutableLiveData<Drawable>()
    val livePhaseColorDrawable = MutableLiveData<Drawable>()
    val livePetImage = MutableLiveData<String>()
    val livePercentage = MutableLiveData<String>()
    var logo = MutableLiveData<String>()

    init {
        liveProgressDialog.value = View.GONE
        liveProgressPercentage.value = View.GONE
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

    fun savePTMMessages(ptbMessageList: ArrayList<RequestPTBMessage>, token: String, phaseId: Int,
                        appointmentId: String, facilityId:String, movingPhase: Int) :
            MutableLiveData<SavePTBMessageBaseResponse>{

        liveProgressDialog.value = View.VISIBLE

        val jsonObjectMain = JSONObject()
        val jsonArray = JSONArray()
        for (ptbMessage in ptbMessageList){
            val jsonObject = JSONObject()
            jsonObject.put("id", ptbMessage.id)
            jsonObject.put("message", ptbMessage.message)
            jsonObject.put("isCustom", ptbMessage.isCustom)

            val jsonGallery = JSONArray()
            if (ptbMessage.gallery != null){
                for (imageUrl in ptbMessage.gallery){
                    jsonGallery.put(imageUrl)
                }
                jsonObject.put("gallery", jsonGallery)
            }
            jsonArray.put(jsonObject)
        }
        jsonObjectMain.put("messages", jsonArray)
        jsonObjectMain.put("phaseId", phaseId)
        jsonObjectMain.put("appointmentId", appointmentId)
        jsonObjectMain.put("facility", facilityId)
        if (movingPhase - phaseId > 0) {
            jsonObjectMain.put("backward", false)
        }else{
            jsonObjectMain.put("backward", true)
        }

        if (ptbMessageList[ptbMessageList.size -1].isPhaseChange) {
            val dragJsonObject = JSONObject()
            dragJsonObject.put("cardId", appointmentId)
            dragJsonObject.put("laneId", movingPhase)
            jsonObjectMain.put("drag", dragJsonObject)
        }else{
            jsonObjectMain.put("drag", false)
        }

        val requestBody = RequestBody.create(MediaType.parse("application/json"),
            jsonObjectMain.toString())

        val dataSource: LiveData<SavePTBMessageBaseResponse> = providerRepository.savePTBMessage(
            requestBody, token)

        savePTBMessageResponse.addSource(dataSource) { dataResponse ->
            if (this.savePTBMessageResponse.hasActiveObservers()) {
                this.savePTBMessageResponse.removeSource(dataSource)
            }
            this.savePTBMessageResponse.setValue(dataResponse)
        }
        return savePTBMessageResponse
    }
}
