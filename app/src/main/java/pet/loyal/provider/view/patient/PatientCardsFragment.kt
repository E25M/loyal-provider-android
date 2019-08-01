package pet.loyal.provider.view.patient


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_patient_cards.*

import pet.loyal.provider.R

/**
 * A simple [Fragment] subclass.
 */
class PatientCardsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_patient_cards, container, false)
    }


    fun setUpLayoutManager() {
        recyclerview_phases.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }
}
