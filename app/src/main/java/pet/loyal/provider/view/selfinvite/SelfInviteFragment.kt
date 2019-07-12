package pet.loyal.provider.view.selfinvite

import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_parent_self_invite.*
import pet.loyal.provider.R
import pet.loyal.provider.api.responses.AppVersionResponse
import pet.loyal.provider.api.responses.SelfInviteDataResponse
import pet.loyal.provider.api.responses.SelfInviteResponse
import pet.loyal.provider.databinding.FragmentParentSelfInviteBinding
import pet.loyal.provider.util.*
import pet.loyal.provider.view.login.LoginActivity
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

/**
 * A simple [Fragment] subclass.
 *
 */
class SelfInviteFragment : Fragment() {

    private lateinit var selfInviteViewModel: SelfInviteViewModel
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var fragmentParentSelfInviteBinding: FragmentParentSelfInviteBinding

    private fun initDataBinding(){
        selfInviteViewModel =
        ViewModelProviders.of(this).get(SelfInviteViewModel::class.java)
        fragmentParentSelfInviteBinding.lifecycleOwner = this
        fragmentParentSelfInviteBinding.viewModel = selfInviteViewModel
        preferenceManager = PreferenceManager(activity!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        fragmentParentSelfInviteBinding = FragmentParentSelfInviteBinding.inflate(inflater,
            container, false)
       initDataBinding()

        return fragmentParentSelfInviteBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        btnSelfInvite?.setOnClickListener {
            sendSelfInvitation(false)
        }
    }

    private fun setObservers(){
        selfInviteViewModel.selfInviteBaseResponse.observe(this, Observer {
                selfInviteResponse ->
            run {
                if (selfInviteResponse?.throwable != null){
                    if (isJSONValid(selfInviteResponse.throwable?.message!!)) {
                        val signInResponse = Gson().fromJson(
                            selfInviteResponse.throwable?.message, AppVersionResponse::class.java
                        )
                        if (signInResponse.statusCode == 401) {
                            redirectToLogin()
                        }else if (signInResponse.statusCode == 400
                            && signInResponse.errorMessage == Constants.self_invite_parent_already_exist_inactive){
                            showPopup(activity!!, getString(R.string.msg_parent_inactive),
                                getString(R.string.text_info))
                        }else{
                            showPopup(activity!!, selfInviteResponse.throwable?.message!!, "Info")
                        }
                    } else {
                        showPopup(activity!!, selfInviteResponse.throwable?.message!!, "Info")
                    }
                }else if(selfInviteResponse.selfInviteResponse != null){
                    showSuccessPopup(selfInviteResponse.selfInviteResponse?.data)
                }
            }
        })
    }

    private fun showSuccessPopup(data: SelfInviteDataResponse?){
        if (data != null) {
            when {
                data.status == Constants.self_invite_parent_added_success -> {
                    val selfInviteSuccessDialogFragment = SelfInviteSuccessDialogFragment()
                    selfInviteSuccessDialogFragment.show(fragmentManager!!, "")
                }
                data.status == Constants.self_invite_parent_exist_facility_not_exist ->
                    showAddToFacilityPopup(data.data.firstName, data.data.lastName)
                data.status == Constants.self_invite_parent_already_exist ->
                    showParentExistPopup(data.data.firstName, data.data.lastName)
            }
        }
    }

    private fun validateEmailPhone(emailPhone: String):Boolean{

        return if (emailPhone.isNotEmpty()){
            when {
                isValidEmail(emailPhone) -> {
                    showValidStatus()
                    true
                }
                isValidPhone(emailPhone) -> {
                    showValidStatus()
                    true
                }
                else -> {
                    selfInviteViewModel.emailPhoneError.value =
                        resources.getString(R.string.error_invalid_email_phone)
                    false
                }
            }
        }else{
            selfInviteViewModel.emailPhoneError.value =
                resources.getString(R.string.error_email_phone_empty)
            false
        }
    }

    private fun showValidStatus(){
        selfInviteViewModel.isEmailPhoneError.value = false
    }

    private fun sendSelfInvitation(addToCurrentAccount: Boolean){
        if (isConnected(activity!!)) {
            if (validateEmailPhone(txtEmailOrPhone.text.toString())) {
                if (txtEmailOrPhone.text!!.isNotEmpty()) {
                    selfInviteViewModel.selfInvite(
                        preferenceManager.getLoginToken(),
                        Constants.sample_first_name,
                        Constants.sample_last_name,
                        addToCurrentAccount,
                        txtEmailOrPhone.text.toString()
                    )
                }
            }
        }else{
            val snackBar =
                Snackbar.make(containerView, getString(R.string.no_connection), Snackbar.LENGTH_INDEFINITE)
            snackBar.setAction(getString(R.string.text_retry)) {
               sendSelfInvitation(addToCurrentAccount)
            }
            snackBar.show()
        }
    }

    private fun showAddToFacilityPopup(fistName: String, lastName: String){
        val builder: AlertDialog.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlertDialog.Builder(activity!!, R.style.Theme_MaterialComponents_Light_Dialog)
        } else {
            AlertDialog.Builder(activity!!)
        }
        builder.setTitle(getString(R.string.text_confirm))
            .setMessage("$fistName $lastName " +
                    getString(R.string.msg_parent_exist_with_another_facility)
            )
            .setCancelable(false)
            .setPositiveButton(getString(R.string.text_add)) { _, _ ->
                sendSelfInvitation(true)
            }
            .setNegativeButton(getString(R.string.text_cancel)){ _, _ ->
                redirectToLogin()
            }
            .show()
    }

    private fun showParentExistPopup(fistName: String, lastName: String){
        val builder: AlertDialog.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlertDialog.Builder(activity!!, R.style.Theme_MaterialComponents_Light_Dialog)
        } else {
            AlertDialog.Builder(activity!!)
        }
        builder.setTitle(getString(R.string.text_info))
            .setMessage("$fistName $lastName " +
                    getString(R.string.msg_parent_already_exist)
            )
            .setCancelable(false)
            .setPositiveButton(getString(R.string.text_ok)) { _, _ ->
                sendSelfInvitation(true)
            }
            .show()
    }

    private fun redirectToLogin() {
        //Todo:
//        preferenceManager.deleteSession()
        showToast(context!!, getString(R.string.txt_logged_out))
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }
}
