package pet.loyal.provider.view.patient


import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.layout_patient_cards.*
import pet.loyal.provider.R
import pet.loyal.provider.api.responses.PetTrackingBoardDataResponse
import pet.loyal.provider.databinding.LayoutPatientCardsBinding
import pet.loyal.provider.model.PetTrackingAppointment
import pet.loyal.provider.model.Phase
import pet.loyal.provider.util.Constants
import pet.loyal.provider.util.PreferenceManager
import pet.loyal.provider.util.isConnected
import pet.loyal.provider.util.showToast
import pet.loyal.provider.view.patient.card.OnPetCardClickListener
import pet.loyal.provider.view.patient.card.PatientCardsAdapter
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

/**
 * A simple [Fragment] subclass.
 */
class PatientCardsFragment : Fragment(), OnPetCardClickListener, OnPhaseClickListener {

    lateinit var layoutBinding: LayoutPatientCardsBinding
    lateinit var viewModel: PatientCardsViewModel
    lateinit var preferenceManager: PreferenceManager
    lateinit var conteinerView: View

    var sortBy = Constants.sort_ascending
    var keyWord = ""
    var facilityId = "default"
    var sort = Constants.sort_type_parent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        layoutBinding = LayoutPatientCardsBinding.inflate(inflater, container, false)
        initDataBinding()
        return layoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        conteinerView = view.findViewById(R.id.constraint_layout_cards_container)
        setUpLayoutManager()
        setUpObservers()
        serachview_patient_cards_keyword.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(newText: String?): Boolean {
                if (newText != null) {
                    if (!TextUtils.isEmpty(newText)) {
                        keyWord = newText
                        loadData()
                    }
                }
                return true
            }
        })
        tablayout_patient_cards_sort.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tablayout_patient_cards_sort.selectedTabPosition == 0) {
                    sort = Constants.sort_type_parent
                } else {
                    sort = Constants.sort_type_patient
                }
                loadData()
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }
        })

        img_patient_cards_sort_by.setOnClickListener {
            if (sortBy == Constants.sort_ascending) {
                sortBy = Constants.sort_descending
            } else {
                sortBy = Constants.sort_ascending
            }
            loadData()
        }
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    private fun initDataBinding() {
        viewModel = ViewModelProviders.of(this).get(PatientCardsViewModel::class.java)
        layoutBinding.viewModel = viewModel
        layoutBinding.lifecycleOwner = this
        preferenceManager = PreferenceManager(context!!)
        facilityId = preferenceManager.getFacilityId()
    }


    private fun setUpLayoutManager() {
        recyclerview_patient_cards.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerview_phases.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }


    private fun loadData() {
        if (isConnected(context!!)) {
            if (!facilityId.equals("default", true)) {
                viewModel.getCards(
                    sort,
                    sortBy,
                    keyWord,
                    preferenceManager.getLoginToken(),
                    facilityId
                )
            } else {
                // no facility selected. select a facility
                showToast(context!!, getString(R.string.msg_no_facility_selected))
            }
        } else {
            handleError(Throwable(getString(R.string.error_no_connection)), false)
        }
    }

    private fun setUpObservers() {
        viewModel.petTrackingBoardResponse.observe(this, Observer { response ->
            if (response.throwable != null) {
                handleError(response.throwable!!, false)
            } else {
                if (response.petTrackingBoardResponse != null) {
                    refreshData(response.petTrackingBoardResponse?.data)
                } else {
                    handleError(Throwable(getString(R.string.error_no_connection)), true)
                }
            }
        })
    }

    private fun refreshData(data: PetTrackingBoardDataResponse?) {
        if (data != null) {
            if (recyclerview_patient_cards.adapter != null) {
                val cardsAdapter = recyclerview_patient_cards.adapter as PatientCardsAdapter
                cardsAdapter.updateList(data.appointments)
            } else {
                recyclerview_patient_cards.adapter =
                    PatientCardsAdapter(context!!, data.appointments, this)
            }
            if (recyclerview_phases.adapter != null) {
                val phaseAdapter = recyclerview_phases.adapter as PatientCardsPhaseAdapter
                phaseAdapter.updateList(data.phases)
            } else {
                recyclerview_phases.adapter =
                    PatientCardsPhaseAdapter(context!!, data.phases, this)
            }
            viewModel.progressBarVisibility.value = View.GONE
        } else {
            handleError(Throwable(getString(R.string.error_no_connection)), true)
        }
    }


    private fun handleError(throwable: Throwable, isConnected: Boolean) {


        var errorMessage = context?.getString(R.string.error_common)
        if (!isConnected) {
            errorMessage = context?.getString(R.string.error_no_connection)
        } else {
            when (throwable) {
                is ConnectException -> {
                    errorMessage = context?.getString(R.string.error_failed_to_connect_to_server)
                }
                is TimeoutException -> {
                    errorMessage = context?.getString(R.string.error_timeout)
                }
                is SocketTimeoutException -> {
                    errorMessage = context?.getString(R.string.error_timeout)
                }
            }
        }

        viewModel.progressBarVisibility.value = View.GONE

        val snackBar =
            Snackbar.make(conteinerView, errorMessage.toString(), Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction("RETRY") {
            loadData()
        }
        snackBar.show()

    }

    override fun onPerCardClick(card: PetTrackingAppointment, position: Int) {
        // navigate the user to the pet card section
    }


    override fun onPhaseClick(position: Int, phase: Phase) {
        viewModel.modifyFilters(phase.id)
        loadData()
    }
}
