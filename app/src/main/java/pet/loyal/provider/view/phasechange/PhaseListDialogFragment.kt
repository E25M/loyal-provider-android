package pet.loyal.provider.view.phasechange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import pet.loyal.provider.R
import pet.loyal.provider.databinding.LayoutPhaseChangeSelectorBinding
import pet.loyal.provider.model.Phase
import pet.loyal.provider.util.Constants
import pet.loyal.provider.util.PreferenceManager

class PhaseListDialogFragment : DialogFragment(), PhaseListRecyclerViewAdapter.PhaseListChangeListener {

    private lateinit var layoutPhaseChangeSelectorBinding: LayoutPhaseChangeSelectorBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var phaseList: ArrayList<Phase>

    fun initDataBind(){
        preferenceManager = PreferenceManager(activity!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            phaseList = arguments!!.getParcelableArrayList(Constants.extra_phase_list)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        layoutPhaseChangeSelectorBinding = DataBindingUtil.inflate(inflater,
            R.layout.layout_phase_change_selector, container, false)

        return layoutPhaseChangeSelectorBinding.root
    }

    // load the image once the image is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layoutPhaseChangeSelectorBinding.btnCancel.setOnClickListener {
            dismiss()
        }
        layoutPhaseChangeSelectorBinding.btnDone.setOnClickListener {

        }
    }

    private fun loadPhaseList(){
        val phaseListRecyclerViewAdapter = PhaseListRecyclerViewAdapter(phaseList, this)
    }

    private fun sendPhaseChange(){

    }

    override fun onItemSelected(position: Int) {

    }
}