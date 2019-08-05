package pet.loyal.provider.view.phasechange

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudinary.Util
import com.google.gson.Gson
import pet.loyal.provider.R
import pet.loyal.provider.api.responses.AppVersionResponse
import pet.loyal.provider.api.responses.PhaseChangeResponse
import pet.loyal.provider.databinding.LayoutPhaseChangeSelectorBinding
import pet.loyal.provider.model.Appointment
import pet.loyal.provider.model.Phase
import pet.loyal.provider.util.*
import pet.loyal.provider.view.editpetcard.EditPetCardFragment
import pet.loyal.provider.view.login.LoginActivity

class PhaseListDialogFragment : DialogFragment(), PhaseListRecyclerViewAdapter.PhaseListChangeListener {

    private lateinit var layoutPhaseChangeSelectorBinding: LayoutPhaseChangeSelectorBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var phaseList: ArrayList<Phase>
    private lateinit var viewModel: PhaseListViewModel
    private var sendingPhaseId = 1
    private var petName:String? = null

    interface PhaseListDialogFragmentListener{
        fun onPhaseChangeSuccess()
    }

    private fun initDataBind(){
        preferenceManager = PreferenceManager(activity!!)
        viewModel = ViewModelProviders.of(this).get(PhaseListViewModel::class.java)
        layoutPhaseChangeSelectorBinding.lifecycleOwner = this
        layoutPhaseChangeSelectorBinding.viewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        layoutPhaseChangeSelectorBinding = DataBindingUtil.inflate(inflater,
            R.layout.layout_phase_change_selector, container, false)
        initDataBind()

        return layoutPhaseChangeSelectorBinding.root
    }

    // load the image once the image is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var appointmentId: String? = null

        if (arguments != null) {
            phaseList = arguments!!.getParcelableArrayList(Constants.extra_phase_list)
            appointmentId = arguments!!.getString(Constants.extra_appointment_id)
            petName = arguments!!.getString(Constants.extra_pet_name)
        }
        setObservers()
        loadPhaseList()

        layoutPhaseChangeSelectorBinding.btnCancel.setOnClickListener {
            dismiss()
        }

        layoutPhaseChangeSelectorBinding.btnDone.setOnClickListener {
            sendPhaseChange(appointmentId)
        }
    }

    private fun loadPhaseList(){
        val phaseListRecyclerViewAdapter = PhaseListRecyclerViewAdapter(phaseList, this)
        layoutPhaseChangeSelectorBinding.recyclerViewPhaseList.setHasFixedSize(true)
        layoutPhaseChangeSelectorBinding.recyclerViewPhaseList.layoutManager = LinearLayoutManager(context)
        layoutPhaseChangeSelectorBinding.recyclerViewPhaseList.adapter = phaseListRecyclerViewAdapter
    }

    private fun sendPhaseChange(appointmentId: String?){
        if (appointmentId != null) {
            viewModel.changePhase(appointmentId, sendingPhaseId, preferenceManager.getLoginToken())
        }
    }

    private fun setObservers() {
        viewModel.phaseChangeBaseResponse.observe(this, Observer { phaseChangeResponse ->
            run {
                if (phaseChangeResponse != null) {
                    if (phaseChangeResponse.throwable != null) {
                        if (isJSONValid(phaseChangeResponse.throwable?.message!!)) {
                            val signInResponse = Gson().fromJson(
                                phaseChangeResponse.throwable?.message, AppVersionResponse::class.java
                            )
                            if (signInResponse.statusCode == 401) {
                                redirectToLogin()
                            } else if (signInResponse.statusCode == 400
                                && signInResponse.errorMessage == Constants.self_invite_parent_already_exist_inactive
                            ) {
                                showPopup(activity!!, getString(R.string.msg_parent_inactive),
                                    getString(R.string.text_info)
                                )
                            } else {
                                showPopup(
                                    activity!!, phaseChangeResponse.throwable?.message!!,
                                    getString(R.string.text_info)
                                )
                            }
                        } else {
                            showPopup(activity!!, phaseChangeResponse.throwable?.message!!,
                                getString(R.string.text_info)
                            )
                        }
                    } else if (phaseChangeResponse.phaseChangeResponse != null) {
                        showPhaseChange(phaseChangeResponse.phaseChangeResponse)
                    }
                }
                viewModel.dialogStatus.value = View.GONE
            }
        })
    }

    private fun showPhaseChange(phaseChangeResponse: PhaseChangeResponse?){
        if (phaseChangeResponse?.statusCode == 200){
            (targetFragment as EditPetCardFragment).onPhaseChangeSuccess()
            dismiss()
        }
    }

    override fun onItemSelected(position: Int, phaseId: Int) {
        sendingPhaseId = phaseId
        phaseList.iterator().forEach { phase ->
            phase.isSelected = phase.id == phaseId
        }
        layoutPhaseChangeSelectorBinding.recyclerViewPhaseList.adapter?.notifyDataSetChanged()
    }

    private fun redirectToLogin() {
        //Todo:
//        preferenceManager.deleteSession()
        showToast(context!!, getString(R.string.txt_logged_out))
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }
}