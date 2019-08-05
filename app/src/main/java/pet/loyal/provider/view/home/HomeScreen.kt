package pet.loyal.provider.view.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import pet.loyal.provider.R
import pet.loyal.provider.databinding.LayoutHomeScreenBinding
import pet.loyal.provider.util.Constants
import pet.loyal.provider.util.PreferenceManager
import pet.loyal.provider.view.editpetcard.EditPetCardFragment
import pet.loyal.provider.view.mainmenu.MainMenuFragment
import pet.loyal.provider.view.patient.PatientCardsFragment
import pet.loyal.provider.view.settings.SettingsFragment

class HomeScreen : AppCompatActivity() {

    lateinit var layoutBinding: LayoutHomeScreenBinding
    lateinit var viewModel: HomeScreenViewModel
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        changeFragment(Constants.fragment_type_home)
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
        supportFragmentManager.beginTransaction().replace(R.id.mainContainer, newsFeedFragment)
            .disallowAddToBackStack().commit()
    }


    fun onLogout(view: View) {

    }

    fun navigateToHome(view: View) {

    }


    fun changeFragment(type: Int) {
        supportFragmentManager.beginTransaction().replace(
            R.id.constraint_layout_container_main,
            getFragment(type)
        ).commit()
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
            else -> {
                viewModel.toolbarVisibility.value = View.VISIBLE
                MainMenuFragment()
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()

    }

}
