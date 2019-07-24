package pet.loyal.provider.view.editpetcard

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pet.loyal.provider.R
import pet.loyal.provider.databinding.LayoutEditPatientCardCustomItemBinding
import pet.loyal.provider.databinding.LayoutEditPatientCardItemBinding
import pet.loyal.provider.databinding.LayoutEditPatientCardSentItemBinding
import pet.loyal.provider.model.PhaseMessage
import pet.loyal.provider.util.Constants
import pet.loyal.provider.util.collapse
import pet.loyal.provider.util.expand

class PhaseMessageRecyclerViewAdapter(
    private val phaseMessagesList: List<PhaseMessage>,
    private val phaseMessageItemListener: PhaseMessageItemListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface PhaseMessageItemListener {
        fun onClickAddPhotos(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when(viewType) {
            Constants.view_type_sent_message -> {
                val layoutEditPatientCardItemBinding = LayoutEditPatientCardSentItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return PhaseMessageSentViewHolder(layoutEditPatientCardItemBinding)
            }
            Constants.view_type_custom_message -> {
                val layoutEditPatientCardItemBinding = LayoutEditPatientCardCustomItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return PhaseMessageCustomViewHolder(layoutEditPatientCardItemBinding)
            }
            Constants.view_type_template_message -> {
                val layoutEditPatientCardItemBinding = LayoutEditPatientCardItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return PhaseMessageTemplateViewHolder(layoutEditPatientCardItemBinding)
            }
            else -> {
                val layoutEditPatientCardItemBinding = LayoutEditPatientCardSentItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return PhaseMessageSentViewHolder(layoutEditPatientCardItemBinding)
            }
        }
    }

    override fun getItemCount(): Int {
        return phaseMessagesList.size
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        val itemPhaseMessage = phaseMessagesList[position]

        when(itemPhaseMessage.type){
            PhaseMessage.Type.SENT_MESSAGE -> {
                val viewPhaseMessage = (viewHolder as PhaseMessageSentViewHolder).itemBinding
                viewPhaseMessage.txtSentMessage.text = itemPhaseMessage.message
                viewPhaseMessage.txtDateTime.text = itemPhaseMessage.dateTime
                viewPhaseMessage.btnDropDown.setOnClickListener {
                    if (viewHolder.itemBinding.mainContainer.height == 100) {
                        viewPhaseMessage.btnDropDown.setImageDrawable(viewHolder.itemView.resources
                            .getDrawable(R.drawable.ic_drop_up_white, null))
                        expand(viewHolder.itemBinding.mainContainer, 500, 270)
                    }else{
                        viewPhaseMessage.btnDropDown.setImageDrawable(viewHolder.itemView.resources
                            .getDrawable(R.drawable.ic_drop_down_white, null))
                        collapse(viewHolder.itemBinding.mainContainer, 500, 100)
                    }
                }
                collapse(viewPhaseMessage.mainContainer, 500, 100)
            }
            PhaseMessage.Type.MESSAGE_TEMPLATE -> {
                val viewPhaseMessage = (viewHolder as PhaseMessageTemplateViewHolder).itemBinding
                viewPhaseMessage.edtTxtMessage.setText(itemPhaseMessage.message)
                viewPhaseMessage.btnAddPhoto.setOnClickListener {
                    phaseMessageItemListener.onClickAddPhotos(position)
                }
                viewPhaseMessage.btnDropDown.setOnClickListener {
                    if (viewPhaseMessage.mainContainer.height == 120) {
                        viewPhaseMessage.btnDropDown.setImageDrawable(viewHolder.itemView.resources
                            .getDrawable(R.drawable.ic_drop_up_black, null))
                        expand(viewPhaseMessage.mainContainer, 500, 270)
                    }else{
                        viewPhaseMessage.btnDropDown.setImageDrawable(viewHolder.itemView.resources
                            .getDrawable(R.drawable.ic_drop_down_black, null))
                        collapse(viewPhaseMessage.mainContainer, 500, 120)
                    }
                }
                collapse(viewPhaseMessage.mainContainer, 500, 120)
            }
            PhaseMessage.Type.CUSTOM_MESSAGE -> {
                val viewPhaseMessage = (viewHolder as PhaseMessageCustomViewHolder).itemBinding
                viewPhaseMessage.edtTxtMessage.setText(itemPhaseMessage.message)
                viewPhaseMessage.btnAddPhoto.setOnClickListener {
                    phaseMessageItemListener.onClickAddPhotos(position)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = phaseMessagesList[position]
        return if (item != null) {
            when (item.type){
                PhaseMessage.Type.SENT_MESSAGE -> Constants.view_type_sent_message
                PhaseMessage.Type.MESSAGE_TEMPLATE -> Constants.view_type_template_message
                PhaseMessage.Type.CUSTOM_MESSAGE -> Constants.view_type_custom_message
            }
        }else{
            Constants.view_type_sent_message
        }
    }

    inner class PhaseMessageSentViewHolder(val itemBinding: LayoutEditPatientCardSentItemBinding):
        RecyclerView.ViewHolder(itemBinding.root)

    inner class PhaseMessageTemplateViewHolder(val itemBinding: LayoutEditPatientCardItemBinding):
        RecyclerView.ViewHolder(itemBinding.root)

    inner class PhaseMessageCustomViewHolder(val itemBinding: LayoutEditPatientCardCustomItemBinding):
        RecyclerView.ViewHolder(itemBinding.root)
}