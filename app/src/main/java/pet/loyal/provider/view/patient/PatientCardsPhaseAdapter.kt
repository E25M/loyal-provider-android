package pet.loyal.provider.view.patient

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_phase.view.*
import kotlinx.android.synthetic.main.list_item_toggle_phase.view.*
import pet.loyal.provider.R
import pet.loyal.provider.model.Phase

class PatientCardsPhaseAdapter(
    val context: Context,
    var phaseList: ArrayList<Phase>,
    var onPhaseClickListener: OnPhaseClickListener
) :
    RecyclerView.Adapter<PatientCardsPhaseAdapter.PhaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhaseViewHolder {
        val view =
            LayoutInflater.from(this.context).inflate(
                R.layout.list_item_toggle_phase, parent, false
            )
        return PhaseViewHolder(view)
    }

    fun updateList(newPhaseList : ArrayList<Phase>){
        this.phaseList.clear()
        this.phaseList.addAll(newPhaseList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return phaseList.size
    }

    override fun onBindViewHolder(holder: PhaseViewHolder, position: Int) {
        holder.bindData(phaseList[position], position, this.onPhaseClickListener)
    }


    class PhaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(phase: Phase, position: Int, onPhaseClickListener: OnPhaseClickListener) {
            itemView.btn_list_item_toggle_phase_name.text = phase.name
            itemView.btn_list_item_toggle_phase_name.textOff = phase.name
            itemView.btn_list_item_toggle_phase_name.textOn = phase.name
            itemView.setOnClickListener {
                onPhaseClickListener.onPhaseClick(position, phase)
            }
        }
    }

}