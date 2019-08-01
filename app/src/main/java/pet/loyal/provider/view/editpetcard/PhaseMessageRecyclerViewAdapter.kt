package pet.loyal.provider.view.editpetcard

import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.adapters.TextViewBindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
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
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), PhaseMessageGalleryRecyclerViewAdapter.ImageItemListener {

    interface PhaseMessageItemListener {
        fun onClickAddPhotos(view: View, position: Int, messageId: String)
        fun onClickDelete(position: Int, positionImage: Int, messageId: String)
        fun onClickImage(position: Int, positionImage: Int, messageId: String)
        fun onClickTick(isChecked: Boolean, position: Int, messageId: String)
        fun onEditMessage(message: String, position: Int, messageId: String)
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
                        expandView(viewPhaseMessage, viewHolder)
                    }else{
                        collapseView(viewPhaseMessage, viewHolder)
                    }
                }

                val recyclerViewAdapter = PhaseMessageGalleryRecyclerViewAdapter(
                    itemPhaseMessage._id,
                    position,
                    itemPhaseMessage.imageGallery,
                    this)
                viewPhaseMessage.recyclerViewImageGallery.layoutManager = LinearLayoutManager(
                    viewPhaseMessage.recyclerViewImageGallery.context,
                    LinearLayoutManager.HORIZONTAL,
                    false)
                viewPhaseMessage.recyclerViewImageGallery.setHasFixedSize(true)
                viewPhaseMessage.recyclerViewImageGallery.adapter = recyclerViewAdapter

                collapseView(viewPhaseMessage, viewHolder)
            }
            PhaseMessage.Type.MESSAGE_TEMPLATE -> {

                val viewPhaseMessage = (viewHolder as PhaseMessageTemplateViewHolder).itemBinding
                if (itemPhaseMessage.editable){
                    var message = itemPhaseMessage.message
                    message = message.replace("<span>&lt;", "<")
                    message = message.replace("&gt;</span>", ">")

                    val spannable = SpannableStringBuilder(message)
                    spannable.setSpan(ForegroundColorSpan(viewPhaseMessage.txtMessage.context
                        .resources.getColor(R.color.font_color_blue)), message.indexOf("<"),
                        message.indexOf(">") + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

                    viewPhaseMessage.edtTxtMessage.text = spannable
                    viewPhaseMessage.layoutMessage.visibility = View.VISIBLE
                    viewPhaseMessage.txtMessage.visibility = View.INVISIBLE
                }else{
                    viewPhaseMessage.txtMessage.text = itemPhaseMessage.message
                    viewPhaseMessage.layoutMessage.visibility = View.INVISIBLE
                    viewPhaseMessage.txtMessage.visibility = View.VISIBLE
                }

                viewPhaseMessage.btnAddPhoto.setOnClickListener {
                    phaseMessageItemListener.onClickAddPhotos(viewPhaseMessage.btnAddPhoto, position,
                        itemPhaseMessage._id)
                }
                viewPhaseMessage.btnDropDown.setOnClickListener {
                    if (viewPhaseMessage.mainContainer.height == 120) {
                        expandView(viewPhaseMessage, viewHolder)
                    }else{
                        collapseView(viewPhaseMessage, viewHolder)
                    }
                }

                viewPhaseMessage.edtTxtMessage.addTextChangedListener(object : TextWatcher{

                    override fun afterTextChanged(s: Editable?) {
                        phaseMessageItemListener.onEditMessage(s.toString(), position,
                            itemPhaseMessage._id)}
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int,
                                                   after: Int){}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int,
                                               count: Int){}
                })

                viewPhaseMessage.chkBoxTicketMessage.setOnCheckedChangeListener {
                        buttonView, isChecked ->
                    phaseMessageItemListener.onClickTick(isChecked, position, itemPhaseMessage._id)
                }

                val recyclerViewAdapter = PhaseMessageGalleryRecyclerViewAdapter(
                    itemPhaseMessage._id,
                    position,
                    itemPhaseMessage.imageGallery,
                    this)
                viewPhaseMessage.recyclerViewImageGallery.layoutManager = LinearLayoutManager(
                    viewPhaseMessage.recyclerViewImageGallery.context,
                    LinearLayoutManager.HORIZONTAL,
                    false)
                viewPhaseMessage.recyclerViewImageGallery.setHasFixedSize(true)
                viewPhaseMessage.recyclerViewImageGallery.adapter = recyclerViewAdapter

                if (itemPhaseMessage.imageGallery.isNullOrEmpty()) {
                    collapseView(viewPhaseMessage, viewHolder)
                }
            }
            PhaseMessage.Type.CUSTOM_MESSAGE -> {
                val viewPhaseMessage = (viewHolder as PhaseMessageCustomViewHolder).itemBinding
                viewPhaseMessage.edtTxtMessage.setText(itemPhaseMessage.message)
                viewPhaseMessage.btnAddPhoto.setOnClickListener {
                    phaseMessageItemListener.onClickAddPhotos(viewPhaseMessage.btnAddPhoto,
                        position, itemPhaseMessage._id)
                }

                viewPhaseMessage.edtTxtMessage.addTextChangedListener(object : TextWatcher{

                    override fun afterTextChanged(s: Editable?) {
                        updateCountValue(s.toString(), viewPhaseMessage.txtRemainingTextCount)
                        phaseMessageItemListener.onEditMessage(s.toString(), position,
                            itemPhaseMessage._id)}
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int,
                                                   after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int
                    ) {}
                })


                val recyclerViewAdapter = PhaseMessageGalleryRecyclerViewAdapter(
                    itemPhaseMessage._id,
                    position,
                    itemPhaseMessage.imageGallery,
                    this)
                viewPhaseMessage.recyclerViewImageGallery.layoutManager = LinearLayoutManager(
                    viewPhaseMessage.recyclerViewImageGallery.context,
                    LinearLayoutManager.HORIZONTAL,
                    false)
                viewPhaseMessage.recyclerViewImageGallery.setHasFixedSize(true)
                viewPhaseMessage.recyclerViewImageGallery.adapter = recyclerViewAdapter
            }
        }
    }

    private fun updateCountValue(message: String, countView: TextView){
        var messageTextCounter:String = ""
        when {
            Constants.custom_message_character_limit - message.length == 1 ->
                messageTextCounter = "${Constants.custom_message_character_limit - message.length} " +
                        "character remaining"
            Constants.custom_message_character_limit - message.length < 0 -> {

            }
            else -> messageTextCounter = "${100 - message.length} characters remaining"
        }
        countView.text = messageTextCounter
    }

    private fun expandView(
        viewPhaseMessage: LayoutEditPatientCardItemBinding,
        viewHolder: RecyclerView.ViewHolder
    ) {
        viewPhaseMessage.btnDropDown.setImageDrawable(
            viewHolder.itemView.resources
                .getDrawable(R.drawable.ic_drop_up_black, null)
        )
        expand(viewPhaseMessage.mainContainer, 500, 270)
    }

    private fun expandView(
        viewPhaseMessage: LayoutEditPatientCardSentItemBinding,
        viewHolder: PhaseMessageSentViewHolder
    ) {
        viewPhaseMessage.btnDropDown.setImageDrawable(
            viewHolder.itemView.resources
                .getDrawable(R.drawable.ic_drop_up_white, null)
        )
        expand(viewHolder.itemBinding.mainContainer, 500, 270)
    }

    private fun collapseView(
        viewPhaseMessage: LayoutEditPatientCardSentItemBinding,
        viewHolder: RecyclerView.ViewHolder
    ) {
        viewPhaseMessage.btnDropDown.setImageDrawable(
            viewHolder.itemView.resources
                .getDrawable(R.drawable.ic_drop_down_white, null)
        )
        collapse(viewPhaseMessage.mainContainer, 500, 100)
    }

    private fun collapseView(
        viewPhaseMessage: LayoutEditPatientCardItemBinding,
        viewHolder: RecyclerView.ViewHolder
    ) {
        viewPhaseMessage.btnDropDown.setImageDrawable(
            viewHolder.itemView.resources
                .getDrawable(R.drawable.ic_drop_down_black, null)
        )
        collapse(viewPhaseMessage.mainContainer, 500, 120)
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

    override fun onClickImage(positionImage: Int, position: Int, messageId: String) {
        phaseMessageItemListener.onClickImage(positionImage, position, messageId)
    }

    override fun onClickDelete(positionImage: Int, position: Int, messageId: String) {
        phaseMessageItemListener.onClickDelete(positionImage, position, messageId)
    }
}