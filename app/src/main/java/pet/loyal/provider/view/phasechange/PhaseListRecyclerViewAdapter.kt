package pet.loyal.provider.view.phasechange

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pet.loyal.provider.R
import pet.loyal.provider.databinding.LayoutPhaseSelectItemBinding
import pet.loyal.provider.model.Phase

class PhaseListRecyclerViewAdapter(
    private val phaseList: ArrayList<Phase>,
    private val phaseListChangeListener: PhaseListChangeListener):
    RecyclerView.Adapter<PhaseListRecyclerViewAdapter.PhaseListViewHolder>() {

    interface PhaseListChangeListener{
        fun onItemSelected(position: Int, phaseId:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhaseListViewHolder {
        val layoutPhaseSelectItemBinding = LayoutPhaseSelectItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return PhaseListViewHolder(layoutPhaseSelectItemBinding)
    }

    override fun getItemCount(): Int {
       return phaseList.size
    }

    override fun onBindViewHolder(viewHolder: PhaseListViewHolder, position: Int) {
        val phaseItem = phaseList[position]

        viewHolder.itemBinding.txtPhaseName.text = phaseItem.name
        if (phaseItem.isSelected){
            viewHolder.itemBinding.itemView.background = viewHolder.itemView.resources.getDrawable(
                R.color.btn_color_blue, null)
            viewHolder.itemBinding.txtPhaseName.setTextColor(viewHolder.itemView.resources
                .getColor(R.color.font_color_white))
        }else{
            if (position % 2 == 0){
                viewHolder.itemBinding.itemView.background = viewHolder.itemView.resources.getDrawable(
                    R.color.bg_color_gray_3, null)
            }else{
                viewHolder.itemBinding.itemView.background = viewHolder.itemView.resources.getDrawable(
                    R.color.font_color_white, null)
            }
            viewHolder.itemBinding.txtPhaseName.setTextColor(viewHolder.itemView.resources
                .getColor(R.color.font_color_black))
        }
        viewHolder.itemView.setOnClickListener {
            phaseListChangeListener.onItemSelected(position, phaseItem.id)
        }
    }

    inner class PhaseListViewHolder(val itemBinding: LayoutPhaseSelectItemBinding):
        RecyclerView.ViewHolder(itemBinding.root)
}