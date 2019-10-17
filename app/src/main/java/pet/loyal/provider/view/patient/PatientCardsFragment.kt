package pet.loyal.provider.view.patient


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.layout_patient_cards.*
import pet.loyal.provider.api.responses.GetPhaseListResponse
import pet.loyal.provider.api.responses.PetTrackingBoardDataResponse
import pet.loyal.provider.databinding.LayoutPatientCardsBinding
import pet.loyal.provider.model.PetTrackingAppointment
import pet.loyal.provider.model.Phase
import pet.loyal.provider.util.*
import pet.loyal.provider.view.editpetcard.EditPetCardFragment
import pet.loyal.provider.view.home.HomeScreen
import pet.loyal.provider.view.patient.card.OnPetCardClickListener
import pet.loyal.provider.view.patient.card.PatientCardsAdapter
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import pet.loyal.provider.BuildConfig
import pet.loyal.provider.R
import pet.loyal.provider.api.responses.CommonResponse
import pet.loyal.provider.view.login.LoginActivity
import java.net.URISyntaxException


/**
 * A simple [Fragment] subclass.
 */
class PatientCardsFragment : Fragment(), OnPetCardClickListener, OnPhaseClickListener {

    lateinit var layoutBinding: LayoutPatientCardsBinding
    lateinit var viewModel: PatientCardsViewModel
    lateinit var preferenceManager: PreferenceManager
    lateinit var conteinerView: View
    lateinit var edittext: EditText
    var phasesLoaded = false

    lateinit var phasesList: ArrayList<Phase>

    var sortBy = Constants.sort_ascending
    var keyWord = ""
    var facilityId = "default"
    var sort = Constants.sort_type_parent

    private lateinit var mSocket:Socket

    private val onNewMessage = Emitter.Listener { args ->

        activity!!.runOnUiThread {
            loadData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            mSocket = IO.socket(BuildConfig.SOCKET_URL)
        }catch (ex:URISyntaxException){
            showToast(activity!!, "Updating pet cards on real time is not working.")
        }

//        mSocket.connect()
        mSocket.on("updateDashboard", onNewMessage)
        mSocket.let {
            it.connect().on(Socket.EVENT_CONNECT) {
//                    Log.d("SignallingClient", "Socket connected!!!!!")
            }
        }
    }

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
        edittext = view.findViewById(R.id.serachview_patient_cards_keyword)
//        val searchView = view.findViewById(R.id.serachview_patient_cards_keyword) as SearchView
//        val id = searchView.context
//            .resources
//            .getIdentifier("android:id/search_src_text", null, null)
//        val textView = searchView.findViewById<View>(id) as TextView
//        textView.setHintTextColor(Color.WHITE)
//        textView.setTextColor(Color.WHITE)
//        setUpLayoutManager()
        viewModel.selectedFacilityLogo.value = preferenceManager.getfacilityLogo()
        setUpGridLayoutManager()
        setUpObservers()
//        serachview_patient_cards_keyword.setOnQueryTextListener(object :
//            SearchView.OnQueryTextListener {
//            override fun onQueryTextChange(p0: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextSubmit(newText: String?): Boolean {
//                if (newText != null) {
//                    if (!TextUtils.isEmpty(newText)) {
//                        keyWord = newText
//                        loadData()
//                    }
//                }
//                return true
//            }
//        })
        serachview_patient_cards_keyword.setOnEditorActionListener(
            object : TextView.OnEditorActionListener {
                override fun onEditorAction(p0: TextView?, actionId: Int, p2: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (!TextUtils.isEmpty(edittext.text.toString())) {
                            keyWord = edittext.text.toString()
                            loadData()
                        }
                        return true
                    }
                    return false
                }
            }
        )


        imageView2.setOnClickListener {
            if (activity is HomeScreen) {
                val homeScreen = activity as HomeScreen
                homeScreen.loadPatientCardsFragment()
            }
        }
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

        img_scroll_left_pet_cards.setOnClickListener {
            recyclerview_phases.smoothScrollToPosition(0)
        }

        img_scroll_right_pet_cards.setOnClickListener {
            recyclerview_phases.smoothScrollToPosition(
                recyclerview_phases.adapter!!.itemCount - 1)
        }


//        drpDwnFilterArea.alpha = 0.5f
        drpDwnFilterArea.setOnClickListener {
            if (filterPanel.height == 0) {
                expand(filterPanel, 200, 240)
                drpDwnFilterArea.setImageResource(R.drawable.ic_up_nav)
                loadPhases()
            } else {
                collapse(filterPanel, 200, 0)
                viewModel.collapseIconVisibility.value = View.GONE
                viewModel.expandIconVisibility.value = View.VISIBLE
//                drpDwnFilterArea.setImageResource(R.drawable.ic_up_nav)
            }


        }
        img_patient_cards_logout.setOnClickListener {
            val activity = activity as HomeScreen
            activity.onLogout(img_patient_cards_logout)
        }

        img_patient_cards_home.setOnClickListener {
            val activity = activity as HomeScreen
            activity.navigateToHome(img_patient_cards_logout)
        }


        img_expand_cards_view.setOnClickListener {
            expand(filterPanel, 200, 240)
            viewModel.collapseIconVisibility.value = View.VISIBLE
            viewModel.expandIconVisibility.value = View.GONE
            loadPhases()
        }
    }

    override fun onStart() {
        super.onStart()
        loadPhases()
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


    private fun setUpGridLayoutManager() {
        recyclerview_patient_cards.layoutManager =
            GridLayoutManager(context, 4, RecyclerView.VERTICAL, false)
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
            handleError(Throwable(getString(R.string.error_no_connection)), false, true)
        }
    }

    private fun loadPhases() {
        if (isConnected(context!!)) {
            viewModel.getPhases(preferenceManager.getLoginToken())
        } else {
            handleError(Throwable(getString(R.string.error_no_connection)), false, false)
        }
    }

    private fun setUpObservers() {
        viewModel.petTrackingBoardResponse.observe(this, Observer { response ->
            if (response.throwable != null) {
                handleError(response.throwable!!, false, true)
            } else {
                if (response.petTrackingBoardResponse != null) {
                    refreshData(response.petTrackingBoardResponse?.data)
                } else {
                    handleError(Throwable(getString(R.string.error_no_connection)), true, true)
                }
            }
        })

        viewModel.phaseListResponse.observe(this, Observer { response ->
            if (response.throwable != null) {
                handleError(response.throwable!!, false, false)
            } else {
                if (response.phaseListResponse != null) {
                    refreshPhases(response.phaseListResponse!!)
                } else {
                    handleError(
                        Throwable(getString(R.string.error_no_connection)),
                        true, false
                    )
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
//            if (recyclerview_phases.adapter != null) {
//                val phaseAdapter = recyclerview_phases.adapter as PatientCardsPhaseAdapter
//                phaseAdapter.updateList(data.phases)
//            } else {
//                recyclerview_phases.adapter =
//                    PatientCardsPhaseAdapter(context!!, data.phases, this)
//            }
            viewModel.progressBarVisibility.value = View.GONE
        } else {
            handleError(Throwable(getString(R.string.error_no_connection)), true, true)
        }
    }


    private fun refreshPhases(response: GetPhaseListResponse) {
        if (recyclerview_phases.adapter != null) {
            val phaseAdapter = recyclerview_phases.adapter as PatientCardsPhaseAdapter
            phaseAdapter.updateList(response.data)
        } else {
            recyclerview_phases.adapter =
                PatientCardsPhaseAdapter(context!!, response.data, this)
        }
        viewModel.progressBarVisibility.value = View.GONE
    }


    private fun handleError(throwable: Throwable, isConnected: Boolean, isLoadingCards: Boolean) {

        var errorMessage = context?.getString(R.string.error_common)
        if (isConnected) {
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
                else -> {
                    val errorResponse =
                        Gson().fromJson(throwable?.message, CommonResponse::class.java)

                    if (errorResponse.statusCode == 401 && errorResponse.error == "Error: Unauthorized"){
                        errorMessage = getString(R.string.txt_logged_out)
                        preferenceManager.deleteSession()
                        activity!!.finish()
                        startActivity(Intent(activity, LoginActivity::class.java))
                    }
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
        val activity = activity as HomeScreen
        val editPetCardFragment = EditPetCardFragment()
        val bundle = Bundle()
        bundle.putString(Constants.extra_appointment_id, card.id)
        editPetCardFragment.arguments = bundle
        viewModel.expandIconVisibility.value = View.GONE
        viewModel.collapseIconVisibility.value = View.VISIBLE
        activity.changeFragment(editPetCardFragment, 5)
    }


    override fun onPhaseClick(position: Int, phase: Phase) {
        viewModel.modifyFilters(phase.id)
        loadData()
    }

    override fun onPhaseTurnedOff(position: Int, phase: Phase) {
        viewModel.removeFromFilters(phase.id)
        loadData()
    }

    override fun onPhaseTurnedOn(position: Int, phase: Phase) {
        viewModel.addToFilters(phase.id)
        loadData()
    }

    override fun onDestroy() {
        super.onDestroy()

        mSocket.disconnect()
        mSocket.off("updateDashboard", onNewMessage)
    }
}
