package pet.loyal.provider.view.home

import android.content.Intent
import android.os.Bundle
import pet.loyal.provider.R
import pet.loyal.provider.util.Constants
import pet.loyal.provider.view.editpetcard.EditPetCardFragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import pet.loyal.provider.databinding.LayoutHomeScreenBinding
import pet.loyal.provider.util.EditPetCardPermissionListener
import pet.loyal.provider.util.PreferenceManager
import pet.loyal.provider.view.login.LoginActivity
import pet.loyal.provider.view.mainmenu.MainMenuFragment
import pet.loyal.provider.view.patient.PatientCardsFragment
import pet.loyal.provider.view.selfinvite.SelfInviteFragment
import pet.loyal.provider.view.settings.SettingsFragment

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
        loadHomeFragment(Constants.fragment_type_home)
    }

    fun setEditCardPermissionListener(editPetCardPermissionListener: EditPetCardPermissionListener){
        this.editPetCardPermissionListener = editPetCardPermissionListener
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
        preferenceManager.deleteSession()
        this.finish()
        startActivity(Intent(this, LoginActivity::class.java))
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
        if (supportFragmentManager.backStackEntryCount == 0){
            finishAffinity()
        }else{
            supportFragmentManager.popBackStack()
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
}
