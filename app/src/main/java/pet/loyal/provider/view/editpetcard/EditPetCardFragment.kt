package pet.loyal.provider.view.editpetcard

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import pet.loyal.client.api.response.PetCardDataResponse
import pet.loyal.provider.R
import pet.loyal.provider.api.responses.AppVersionResponse
import pet.loyal.provider.databinding.FragmentEditPatiantCardBinding
import pet.loyal.provider.model.PhaseMessage
import pet.loyal.provider.util.*
import pet.loyal.provider.view.login.LoginActivity
import java.io.File

class EditPetCardFragment : Fragment(), PhaseMessageRecyclerViewAdapter.PhaseMessageItemListener {

    private val CONTEXT_MENU_TAKE_A_PHOTO = 1
    private val CONTEXT_MENU_SELECT_A_PHOTO = 2
    private val REQUEST_TAKE_PICTURE = 1
    private val REQUEST_SELECT_PICTURE = 2
    private val PERMISSION_REQUEST_WRITE_STORAGE = 103
    private val PERMISSION_REQUEST_READ_STORAGE = 104

    private var selectedPhotoFile: File? = null
    private lateinit var selectedPhotoUri: Uri

    override fun onClickAddPhotos(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun newInstance() = EditPetCardFragment()
    }

    private lateinit var fragmentEditPatiantCardBinding: FragmentEditPatiantCardBinding
    private lateinit var viewModel: EditPetCardViewModel
    private lateinit var preferenceManager: PreferenceManager

    private fun initDataBinding(){
        viewModel = ViewModelProviders.of(this).get(EditPetCardViewModel::class.java)
        fragmentEditPatiantCardBinding.lifecycleOwner = this
        fragmentEditPatiantCardBinding.viewModel = viewModel
        preferenceManager = PreferenceManager(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        fragmentEditPatiantCardBinding = FragmentEditPatiantCardBinding
            .inflate(inflater, container, false)
        initDataBinding()

        setObservers()
        loadAppointment()

        return fragmentEditPatiantCardBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun loadAppointment(){
//        if (arguments != null) {
            viewModel.getPetCardById("5d36a5a2239176001ee19285", preferenceManager.getLoginToken())
//        }
    }

    private fun setObservers(){
        viewModel.petCardResponse.observe(this, Observer {
                petCardResponse ->
            run {
                if (petCardResponse != null) {
                    if (petCardResponse.throwable != null) {
                        if (isJSONValid(petCardResponse.throwable?.message!!)) {
                            val signInResponse = Gson().fromJson(
                                petCardResponse.throwable?.message, AppVersionResponse::class.java
                            )
                            if (signInResponse.statusCode == 401) {
                                redirectToLogin()
                            }else if (signInResponse.statusCode == 400
                                && signInResponse.errorMessage == Constants.self_invite_parent_already_exist_inactive){
                                showPopup(activity!!, getString(R.string.msg_parent_inactive),
                                    getString(R.string.text_info))
                            }else{
                                showPopup(activity!!, petCardResponse.throwable?.message!!, getString(R.string.text_info))
                            }
                        } else {
                            showPopup(activity!!, petCardResponse.throwable?.message!!, getString(R.string.text_info))
                        }
                    } else if (petCardResponse.petCardResponse != null){
                        showCardDetails(petCardResponse.petCardResponse?.data)
                    }
                }
            }
        })
    }

    private fun showCardDetails(petCardDataResponse: PetCardDataResponse?){
        if (petCardDataResponse != null){
            val appointment = petCardDataResponse.appointment
            if (appointment != null){
                val phases = petCardDataResponse.phases
                viewModel.livePetName.value = appointment.petName + ", " + appointment.parentLastName
                viewModel.liveBreedSpecies.value = appointment.petBreed + ", " + appointment.petSpecies
                viewModel.liveGendar.value = appointment.petGender
                phases.iterator().forEach {
                    if (it.id == appointment.phase){
                        viewModel.livePhase.value = it.name
                    }
                }

                @RequiresApi(Build.VERSION_CODES.M)
                viewModel.liveColor.value = getPhaseColors(appointment.phase, activity!!)

                val phaseMessages = ArrayList<PhaseMessage>()
                petCardDataResponse.ptbSentMessages.iterator().forEach {
                    phaseMessages.add(PhaseMessage(it.phaseMessageId, it.status, it._id, it.phaseId,
                        it.appointmentId, it.message, it.dateTime))
                }

                petCardDataResponse.ptbMessageTemplates.iterator().forEach {
                    phaseMessages.add(PhaseMessage(it._id, it.phaseId, it.message))
                }

                val phaseMessageRecyclerViewAdapter = PhaseMessageRecyclerViewAdapter( phaseMessages,
                    this)
                fragmentEditPatiantCardBinding.recyclerViewMessages.setHasFixedSize(true)
                fragmentEditPatiantCardBinding.recyclerViewMessages.layoutManager =
                    LinearLayoutManager(context)
                fragmentEditPatiantCardBinding.recyclerViewMessages.adapter =
                    phaseMessageRecyclerViewAdapter
            }
        }
    }

    private fun redirectToLogin() {
        //Todo:
//        preferenceManager.deleteSession()
        showToast(context!!, getString(R.string.txt_logged_out))
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }
}
