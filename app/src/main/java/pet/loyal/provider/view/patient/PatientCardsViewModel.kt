package pet.loyal.provider.view.patient

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
import pet.loyal.provider.api.responses.PetTrackingBoardBaseResponse
import java.util.*
import kotlin.collections.ArrayList

class PatientCardsViewModel : ViewModel() {

    private var repository: ProviderRepository
    var progressBarVisibility = MutableLiveData<Int>()
    var petTrackingBoardResponse: MediatorLiveData<PetTrackingBoardBaseResponse>
    lateinit var filters: ArrayList<Int>

    init {
        repository = RepositoryProvider.provideProviderRepository()
        progressBarVisibility.value = View.GONE
        petTrackingBoardResponse = MediatorLiveData()
        filters = arrayListOf()
    }

    fun getCards(
        sort : String ,
        sortBy : Int ,
        keyword: String,
        loginToken: String,
        facilityId: String
    )
    :LiveData<PetTrackingBoardBaseResponse>
    {

        progressBarVisibility.value = View.VISIBLE

        val dataJson = JSONObject()
        val filtersArray = JSONArray(filters)
        dataJson.put("facilityId", facilityId)
        dataJson.put("search", keyword)
        dataJson.put("filter", filtersArray)
        dataJson.put("sort", sort)
        dataJson.put("sortBy", sortBy)
        val requestBody =
            RequestBody.create(MediaType.parse("text/plain"), dataJson.toString())

        val dataSource = repository.getPetTrackingBoard(requestBody, loginToken)
        petTrackingBoardResponse.addSource(
            dataSource
        ) { dataResponse ->
            if (this.petTrackingBoardResponse.hasActiveObservers()) {
                this.petTrackingBoardResponse.removeSource(dataSource)
            }
            this.petTrackingBoardResponse.setValue(dataResponse)
        }
        return petTrackingBoardResponse

    }

    fun modifyFilters(phaseId: Int) {
        if (filters.contains(phaseId)) {
            filters.remove(phaseId)
        } else {
            filters.add(phaseId)
        }
    }

}