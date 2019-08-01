package pet.loyal.provider.view.settings


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import pet.loyal.provider.databinding.LayoutSettingsBinding
import pet.loyal.provider.model.Facility
import pet.loyal.provider.util.PreferenceManager

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    lateinit var layoutBinding: LayoutSettingsBinding
    lateinit var viewModel: SettingsViewModel
    lateinit var preferenceManager: PreferenceManager
    lateinit var facilityList: ArrayList<Facility>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutBinding = LayoutSettingsBinding.inflate(inflater, container, false)
        initDataBinding()
        return layoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.facilityListResponse.observe(this, Observer {
            response ->
            if (response.throwable != null){
//                 api call failed. handle the error
            }else{
                if (response.facilityResponse?.data != null) {
                    facilityList.clear()
                    val data = response.facilityResponse?.data
                    facilityList.addAll(data!!)
                }else{
//                     response is empty . handle the error
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
    }


    private fun loadFacilityList(token : String){
        viewModel.getFacilityList(preferenceManager.getLoginToken())
    }

}
