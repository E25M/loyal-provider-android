package pet.loyal.provider.view.selfinvite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_parent_self_invite.*
import pet.loyal.provider.R
import pet.loyal.provider.api.responses.AppVersionResponse
import pet.loyal.provider.databinding.FragmentParentSelfInviteBinding
import pet.loyal.provider.util.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

/**
 * A simple [Fragment] subclass.
 *
 */
class SelfInviteFragment : Fragment() {

    private val selfInviteViewModel: SelfInviteViewModel =
        ViewModelProviders.of(this).get(SelfInviteViewModel::class.java)
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var fragmentParentSelfInviteBinding: FragmentParentSelfInviteBinding

    fun initDataBinding(){
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
            sendSelfInvitation()
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
                        }
                    } else {
                        showPopup(activity!!, selfInviteResponse.throwable?.message!!, "Info")
                    }
                }else if(selfInviteResponse.selfInviteResponse != null){

                }
            }
        })
    }

    private fun sendSelfInvitation(){
        if (isConnected(activity!!)) {
            if (txtEmailOrPhone.text!!.isNotEmpty()) {
                selfInviteViewModel.selfInvite(
                    preferenceManager.getLoginToken(),
                    txtEmailOrPhone.text.toString()
                )
            }
        }else{
            val snackBar =
                Snackbar.make(containerView, getString(R.string.no_connection), Snackbar.LENGTH_INDEFINITE)
            snackBar.setAction("RETRY") {
               sendSelfInvitation()
            }
            snackBar.show()
        }
    }

    private fun redirectToLogin() {
        //Todo:
//        preferenceManager.deleteSession()
//        showToast(context!!, getString(R.string.txt_logged_out))
//        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }
}
