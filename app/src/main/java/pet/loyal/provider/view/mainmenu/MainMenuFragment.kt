package pet.loyal.provider.view.mainmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.helpscout.beacon.ui.BeaconActivity
import kotlinx.android.synthetic.main.main_menu_fragment.*
import pet.loyal.provider.databinding.MainMenuFragmentBinding
import pet.loyal.provider.util.Constants
import pet.loyal.provider.util.PreferenceManager
import pet.loyal.provider.view.home.HomeScreen

class MainMenuFragment : Fragment() {

    lateinit var layoutBinding: MainMenuFragmentBinding
    lateinit var viewModel: MainMenuViewModel
    lateinit var preferenceManager: PreferenceManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutBinding = MainMenuFragmentBinding.inflate(inflater, container, false)
        initDataBinding()
        return layoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (preferenceManager.getUserType() == Constants.user_type_super_admin) {
            viewModel.disableAlpha.value = 0.2f
            viewModel.enableAlpha.value = 1f
        }else{
            viewModel.disableAlpha.value = 1f
            viewModel.enableAlpha.value = 0.2f
        }
        img_main_menu_patient_cards.setOnClickListener {
            if (preferenceManager.getUserType() != Constants.user_type_super_admin) {
                changeFragment(Constants.fragment_type_pet_cards)
            }
        }
        img_main_menu_settings.setOnClickListener {
            if (preferenceManager.getUserType() == Constants.user_type_super_admin) {
                changeFragment(Constants.fragment_type_settings)
            }
        }
        img_main_menu_parent_sign_up.setOnClickListener {
            if (preferenceManager.getUserType() != Constants.user_type_super_admin) {
                changeFragment(Constants.fragment_type_parent_sign_up)
            }
        }
        img_main_menu_support.setOnClickListener {
            if (preferenceManager.getUserType() != Constants.user_type_super_admin) {
                BeaconActivity.open(activity!!)
            }
        }
    }

    private fun initDataBinding() {
        viewModel = ViewModelProviders.of(this).get(MainMenuViewModel::class.java)
        layoutBinding.viewModel = viewModel
        layoutBinding.lifecycleOwner = this
        preferenceManager = PreferenceManager(context!!)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun changeFragment(type: Int) {
        val activity = activity as HomeScreen
        activity.changeFragment(type)
    }

}
