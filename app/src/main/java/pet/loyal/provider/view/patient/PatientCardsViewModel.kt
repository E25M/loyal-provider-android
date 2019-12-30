package pet.loyal.provider.view.patient

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
import pet.loyal.provider.R
import pet.loyal.provider.api.repository.ProviderRepository
import pet.loyal.provider.api.repository.RepositoryProvider
import pet.loyal.provider.api.responses.GetPhaseListBaseResponse
import pet.loyal.provider.api.responses.PetTrackingBoardBaseResponse
import java.util.*
import kotlin.collections.ArrayList

class PatientCardsViewModel : ViewModel() {

    private var repository: ProviderRepository
    var progressBarVisibility = MutableLiveData<Int>()
    var petTrackingBoardResponse: MediatorLiveData<PetTrackingBoardBaseResponse>
    var phaseListResponse: MediatorLiveData<GetPhaseListBaseResponse>
    var collapseIconVisibility: MutableLiveData<Int> = MutableLiveData()
    var expandIconVisibility: MutableLiveData<Int> = MutableLiveData()
    var selectedFacilityLogo = MutableLiveData<String>()
    val sortByIcon = MutableLiveData<Drawable>()
    lateinit var filters: ArrayList<Int>

    init {
        repository = RepositoryProvider.provideProviderRepository()
        progressBarVisibility.value = View.GONE
        collapseIconVisibility.value = View.VISIBLE
        expandIconVisibility.value = View.GONE
        petTrackingBoardResponse = MediatorLiveData()
        phaseListResponse = MediatorLiveData()
        filters = arrayListOf()
    }


    fun getPhases(token: String): LiveData<GetPhaseListBaseResponse> {

        progressBarVisibility.value = View.VISIBLE

        val dataSource = repository.getPhaseList(token)
        phaseListResponse.addSource(dataSource) { listResponse ->
            if (phaseListResponse.hasActiveObservers()) {
                phaseListResponse.removeSource(dataSource)
            }
            phaseListResponse.value = listResponse
        }

        return phaseListResponse
    }

    fun getCards(
        sort: String,
        sortBy: Int,
        keyword: String,
        loginToken: String,
        facilityId: String
    )
            : LiveData<PetTrackingBoardBaseResponse> {

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

    fun removeFromFilters(phaseId: Int) {
        if (filters.contains(phaseId)) {
            filters.remove(phaseId)
        }
    }

    fun addToFilters(phaseId: Int) {
        filters.add(phaseId)
    }

}