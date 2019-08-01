package pet.loyal.provider.view.phasechange

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_phase_select_item.view.*
import pet.loyal.provider.R
import pet.loyal.provider.databinding.LayoutPhaseSelectItemBinding
import pet.loyal.provider.model.Phase

class PhaseListRecyclerViewAdapter(val phaseList: ArrayList<Phase>,
                                   val phaseListChangeListener: PhaseListChangeListener):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface PhaseListChangeListener{
        fun onItemSelected(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val listItemCommentBinding = LayoutPhaseSelectItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return PhaseListViewHolder(listItemCommentBinding)
    }

    override fun getItemCount(): Int {
       return phaseList.size
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val phaseItem = phaseList[position]

        viewHolder.itemView.txtPhaseName.text = phaseItem.name
        if (phaseItem.isSelected){
            viewHolder.itemView.background = viewHolder.itemView.resources.getDrawable(
                R.color.btn_color_blue, null)
        }else{
            if (position % 2 == 0){
                viewHolder.itemView.background = viewHolder.itemView.resources.getDrawable(
                    R.color.bg_color_gray_3, null)
            }else{
                viewHolder.itemView.background = viewHolder.itemView.resources.getDrawable(
                    R.color.font_color_white, null)
            }
        }
        viewHolder.itemView.setOnClickListener {
            phaseListChangeListener.onItemSelected(position)
        }
    }

    class PhaseListViewHolder(private val itemBinding: LayoutPhaseSelectItemBinding):
        RecyclerView.ViewHolder(itemBinding.root)
}