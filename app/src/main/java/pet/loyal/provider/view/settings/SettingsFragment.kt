package pet.loyal.provider.view.settings


import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.layout_dialog_facility.*
import kotlinx.android.synthetic.main.layout_patient_cards.*
import kotlinx.android.synthetic.main.layout_settings.*
import pet.loyal.provider.R
import pet.loyal.provider.databinding.LayoutSettingsBinding
import pet.loyal.provider.model.Facility
import pet.loyal.provider.util.PreferenceManager
import pet.loyal.provider.util.isConnected
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment(), OnFacilityClickListener {

    lateinit var layoutBinding: LayoutSettingsBinding
    lateinit var viewModel: SettingsViewModel
    lateinit var preferenceManager: PreferenceManager
    lateinit var facilityList: ArrayList<Facility>
    lateinit var tempList: ArrayList<Facility>
    lateinit var containerview: View
    var selectedId = ""
    var selected: Facility? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutBinding = LayoutSettingsBinding.inflate(inflater, container, false)
        initDataBinding()
        return layoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        containerview = view.findViewById(R.id.constraint_layout_settings_container)
        setUpObservers()
        btn_apply_settings.setOnClickListener {
            if (selected != null) {
                showApplyConfirmation(selected!!)
            }
        }
        search_view_facility.setOnClickListener {
            if (facilityList != null) {
                val facilityListFragment = FacilityFragment()
                var bundle = Bundle()

                if (selectedId != null) {
                    val iterator = facilityList?.iterator()
                    while (iterator?.hasNext()!!) {
                        val facility = iterator.next()
                        facility.selected = selectedId.equals(facility.name, true)
                    }
                }
                bundle.putParcelableArrayList("data", facilityList)
                facilityListFragment.arguments = bundle
                facilityListFragment.setTargetFragment(this, 3)
                facilityListFragment.show(fragmentManager!!, "")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (isConnected(context!!)) {
            loadFacilityList(preferenceManager.getLoginToken())
        } else {
            handleError(Throwable(getString(R.string.error_no_connection)), false)
        }
    }

    private fun setUpObservers() {
        viewModel.facilityListResponse.observe(this, Observer { response ->
            if (response.throwable != null) {
//                 api call failed. handle the error
                handleError(response.throwable!!, true)
            } else {
                if (response.facilityResponse?.data != null) {
                    facilityList.clear()
                    val data = response.facilityResponse?.data
                    facilityList.addAll(data!!)
                    viewModel.progressBarVisibility.value = View.GONE
                } else {
//                  response is empty . handle the error
                    handleError(Throwable(getString(R.string.error_no_connection)), true)
                }
            }
        })
    }

    private fun initDataBinding() {
        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        layoutBinding.viewModel = viewModel
        layoutBinding.lifecycleOwner = this
        preferenceManager = PreferenceManager(context!!)
        facilityList = ArrayList()
        tempList = ArrayList()
        selectedId = preferenceManager.getFacilityId()
        if (selectedId.equals("default", true)) {
            viewModel.applyButtonEnabled.value = false
        }
    }


    private fun loadFacilityList(token: String) {
        if (!TextUtils.isEmpty(preferenceManager.getFacilityName())){
            viewModel.selectedFacility.value = preferenceManager.getFacilityName()
        }
        viewModel.getFacilityList(preferenceManager.getLoginToken())
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
            Snackbar.make(containerview, errorMessage.toString(), Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction("RETRY") {
            loadFacilityList(preferenceManager.getLoginToken())
        }
        snackBar.show()

    }


    override fun onFacilitySelected(position: Int, facility: Facility) {
        viewModel.applyButtonEnabled.value = !selectedId.equals(facility.id, true)
        selected = facility
        selectedId = facility.id
        viewModel.selectedFacility.value = facility.displayName
    }


    fun showApplyConfirmation(facility: Facility) {
        val builder = AlertDialog.Builder(context!!, R.style.AlertDialogTheme)
        builder.setMessage(getString(R.string.txt_confirm_application) + " " + facility.displayName)
        builder.setPositiveButton(
            getString(R.string.btn_name_ok)
        ) { dialogInterface, _ ->
            preferenceManager.saveFacility(facility)
            dialogInterface.dismiss()
        }
        builder.create().show()
    }


    fun setUpRecyclerViews() {
        recyclerview_patient_cards.layoutManager =
            LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
    }
}
