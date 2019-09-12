package pet.loyal.provider.view.home

import android.content.Intent
import android.os.Bundle
import pet.loyal.provider.R
import pet.loyal.provider.view.editpetcard.EditPetCardFragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import org.json.JSONException
import pet.loyal.provider.api.responses.CommonResponse
import pet.loyal.provider.databinding.LayoutHomeScreenBinding
import pet.loyal.provider.util.*
import pet.loyal.provider.view.login.LoginActivity
import pet.loyal.provider.view.mainmenu.MainMenuFragment
import pet.loyal.provider.view.patient.PatientCardsFragment
import pet.loyal.provider.view.selfinvite.SelfInviteFragment
import pet.loyal.provider.view.settings.SettingsFragment
import java.net.ConnectException

class HomeScreen : AppCompatActivity() {
    private val PERMISSION_REQUEST_WRITE_STORAGE = 103
    private val PERMISSION_REQUEST_READ_STORAGE = 104

    lateinit var layoutBinding: LayoutHomeScreenBinding
    lateinit var viewModel: HomeScreenViewModel
    lateinit var preferenceManager: PreferenceManager
    var editPetCardPermissionListener: EditPetCardPermissionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        setUpObservers()
        loadHomeFragment(Constants.fragment_type_home)
    }


    fun setEditCardPermissionListener(editPetCardPermissionListener: EditPetCardPermissionListener) {
        this.editPetCardPermissionListener = editPetCardPermissionListener
    }

    private fun setUpObservers() {
        viewModel.logoutResponse.observe(this, Observer { logOutResponse ->
            viewModel.progressBarVisibility.value = View.GONE
            if (logOutResponse?.throwable != null) {
                handleError(logOutResponse.throwable)
            } else if (logOutResponse?.commonResponse != null) {
                preferenceManager.deleteSession()
                this.finish()
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                handleError(Throwable(""))
            }
        })
    }

    private fun initDataBinding() {
        layoutBinding = DataBindingUtil.setContentView(this, R.layout.layout_home_screen)
        viewModel = ViewModelProviders.of(this).get(HomeScreenViewModel::class.java)
        layoutBinding.viewModel = viewModel
        layoutBinding.lifecycleOwner = this
        preferenceManager = PreferenceManager(this)
    }

    private fun loadSelfInviteFragment() {
        val newsFeedFragment = EditPetCardFragment()
        val bundle = Bundle()
        bundle.putString(Constants.extra_appointment_id, "")
        newsFeedFragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.mainContainer, newsFeedFragment)
            .disallowAddToBackStack().commit()
    }

    fun onLogout(view: View) {
        if (isConnected(this)) {
            viewModel.logOut(preferenceManager.getLoginToken())
        }else{
            handleError(Throwable(getString(R.string.error_common)))
        }
    }

    fun navigateToHome(view: View) {
        loadHomeFragment(Constants.fragment_type_home)
    }

    private fun loadHomeFragment(type: Int) {
        supportFragmentManager.beginTransaction().replace(
            R.id.constraint_layout_container_main,
            getFragment(type)
        ).commit()
    }

    fun changeFragment(type: Int) {
        supportFragmentManager.beginTransaction().replace(
            R.id.constraint_layout_container_main,
            getFragment(type)
        ).addToBackStack(type.toString()).commit()
    }

    fun changeFragment(fragment: Fragment, type: Int) {
        showHideToolBar(type)
        supportFragmentManager.beginTransaction().replace(
            R.id.constraint_layout_container_main,
            fragment
        ).addToBackStack(type.toString()).commit()
    }

    private fun getFragment(type: Int): Fragment {
        return when (type) {
            Constants.fragment_type_home -> {
                viewModel.toolbarVisibility.value = View.VISIBLE
                MainMenuFragment()
            }
            Constants.fragment_type_pet_cards -> {
                viewModel.toolbarVisibility.value = View.GONE
                PatientCardsFragment()
            }
            Constants.fragment_type_settings -> {
                viewModel.toolbarVisibility.value = View.VISIBLE
                SettingsFragment()
            }
            Constants.fragment_type_parent_sign_up -> {
                viewModel.toolbarVisibility.value = View.VISIBLE
                SelfInviteFragment()
            }
            else -> {
                viewModel.toolbarVisibility.value = View.VISIBLE
                MainMenuFragment()
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            finishAffinity()
        } else {
            supportFragmentManager.popBackStackImmediate()
            val fragment =
                supportFragmentManager.findFragmentById(R.id.constraint_layout_container_main)
            if (fragment != null && fragment.isVisible) {
                if (fragment is PatientCardsFragment) {
                    viewModel.toolbarVisibility.value = View.GONE
                } else {
                    viewModel.toolbarVisibility.value = View.VISIBLE
                }
            }
        }
    }

    private fun showHideToolBar(type: Int) {
        when (type) {
            Constants.fragment_type_home -> {
                viewModel.toolbarVisibility.value = View.VISIBLE
            }
            Constants.fragment_type_pet_cards -> {
                viewModel.toolbarVisibility.value = View.GONE
            }
            Constants.fragment_type_settings -> {
                viewModel.toolbarVisibility.value = View.VISIBLE
            }
            else -> {
                viewModel.toolbarVisibility.value = View.VISIBLE
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_READ_STORAGE || requestCode == PERMISSION_REQUEST_WRITE_STORAGE) {
            editPetCardPermissionListener?.onPermissionGranted(true, requestCode, grantResults)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun handleError(throwable: Throwable?) {

        var errorMessage: String = ""
        var errorTitle = ""

        try {
            val errorResponse =
                Gson().fromJson(throwable?.message, CommonResponse::class.java)
            if (throwable is ConnectException) {
                errorMessage = getString(R.string.error_no_connection)
            } else if (errorResponse != null) {

                when (errorResponse.errorMessage) {
                    Constants.error_user_deactivated -> {
                        errorMessage = getString(R.string.msg_inactive_account)
                    }
                    else -> {
                        errorMessage = getString(R.string.error_common)
                    }
                }
            } else {
                errorMessage = getString(R.string.error_common)
            }
        } catch (e: JSONException) {
            errorMessage = getString(R.string.error_common)
        }

        showToast(this, errorMessage)
        viewModel.progressBarVisibility.value = View.GONE
    }
}
