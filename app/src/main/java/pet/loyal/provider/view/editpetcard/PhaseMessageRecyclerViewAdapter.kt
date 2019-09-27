package pet.loyal.provider.view.editpetcard

import android.app.TimePickerDialog
import android.content.Context
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pet.loyal.provider.R
import pet.loyal.provider.databinding.LayoutEditPatientCardCustomItemBinding
import pet.loyal.provider.databinding.LayoutEditPatientCardItemBinding
import pet.loyal.provider.databinding.LayoutEditPatientCardPhaseChangeItemBinding
import pet.loyal.provider.databinding.LayoutEditPatientCardSentItemBinding
import pet.loyal.provider.model.PhaseMessage
import pet.loyal.provider.util.*
import java.util.*
import kotlin.collections.ArrayList

class PhaseMessageRecyclerViewAdapter(
    private val phaseMessagesList: List<PhaseMessage>,
    private val phaseMessageItemListener: PhaseMessageItemListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), PhaseMessageGalleryRecyclerViewAdapter.ImageItemListener,ViewDialog.ViewDialogListener {

    interface PhaseMessageItemListener {
        fun onClickAddPhotos(view: View, position: Int, messageId: String)
        fun onClickDelete(position: Int, positionImage: Int, messageId: String)
        fun onClickImage(position: Int, positionImage: Int, messageId: String)
        fun onClickTick(isChecked: Boolean, position: Int, messageId: String)
        fun onClickTickCustom(isChecked: Boolean, position: Int, messageId: String)
        fun onEditMessage(message: String, position: Int, messageId: String)
        fun onEditMessage(message: Spannable, position: Int, messageId: String)
        fun onEditMessageCustom(message: String, position: Int, messageId: String)
        fun onAddCustomMessage(position: Int)
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
            Constants.view_type_phase_change_message -> {
                val layoutEditPatientCardItemBinding = LayoutEditPatientCardPhaseChangeItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )

                return PhaseMessagePhaseChangeViewHolder(layoutEditPatientCardItemBinding)
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
                viewPhaseMessage.txtDateTime.text = getTimeString(itemPhaseMessage.dateTime!!) + ", " + getDateString(itemPhaseMessage.dateTime!!)
                viewPhaseMessage.mainContainer.setOnClickListener {
                    if (viewHolder.itemBinding.mainContainer.height == 100) {
                        if (itemPhaseMessage.imageGallery != null && itemPhaseMessage.imageGallery!!.size > 0) {
                            expandView(viewPhaseMessage, viewHolder)
                        }
                    }else{
                        collapseView(viewPhaseMessage, viewHolder)
                    }
                }

                viewPhaseMessage.btnDropDown.setOnClickListener {
                    if (viewHolder.itemBinding.mainContainer.height == 100) {
                        if (itemPhaseMessage.imageGallery != null && itemPhaseMessage.imageGallery!!.size > 0) {
                            expandView(viewPhaseMessage, viewHolder)
                        }
                    }else{
                        collapseView(viewPhaseMessage, viewHolder)
                    }
                }

                if (itemPhaseMessage.imageGallery != null
                            && itemPhaseMessage.imageGallery!!.size > 0){
                    viewPhaseMessage.btnDropDown.visibility = View.VISIBLE
                }else{
                    viewPhaseMessage.btnDropDown.visibility = View.INVISIBLE
                }

                val recyclerViewAdapter = PhaseMessageGalleryRecyclerViewAdapter(
                    Constants.view_type_sent_message,
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

                var message:String
                if (itemPhaseMessage.messageSpan != null){
                    viewPhaseMessage.txtMessage.text = itemPhaseMessage.messageSpan
                    viewPhaseMessage.txtMessage.movementMethod = LinkMovementMethod.getInstance()

                }else if (itemPhaseMessage.editable && itemPhaseMessage.control != null){

                    when(itemPhaseMessage.control){

                        "timePicker" -> {
                            itemPhaseMessage.message = itemPhaseMessage.message.replace(
                                itemPhaseMessage.control!!, "<SELECT TIME>")
                            itemPhaseMessage.control = "<SELECT TIME>"
                        }
                        "dropDown" -> {
                            itemPhaseMessage.message = itemPhaseMessage.message.replace(
                                itemPhaseMessage.control!!, "<PLEASE SELECT>")
                            itemPhaseMessage.control = "<PLEASE SELECT>"
                        }
                        "textField" -> {
                            itemPhaseMessage.message = itemPhaseMessage.message.replace(
                                itemPhaseMessage.control!!, "<ENTER VALUE>")
                            itemPhaseMessage.control = "<ENTER VALUE>"
                        }
                    }

                    message = itemPhaseMessage.message

                    val spannable = SpannableString(message)
                    val indexes = getIndexes(message, itemPhaseMessage.control!!)

                   when(itemPhaseMessage.control){

                       "<SELECT TIME>" -> {
                           var count = 0
                           indexes.forEach { _ ->
                               spannable.setSpan(
                                   object :ClickableSpan(){
                                       override fun onClick(widget: View) {
                                            showTimePicker(viewPhaseMessage.txtMessage.context,
                                                message,
                                                viewPhaseMessage.txtMessage,
                                                itemPhaseMessage.control!!, itemPhaseMessage._id)
                                       }
                                   },
                                   indexes[count], indexes[count] +
                                           itemPhaseMessage.control!!.length,
                                   Spannable.SPAN_INCLUSIVE_INCLUSIVE
                               )
                           }
                       }

                       "<PLEASE SELECT>" -> {
                           val values = itemPhaseMessage.value
                           var valueList = ArrayList<String>()
                           if (values != null) {
                               if (values.contains(",")) {
                                   valueList = values.split(",") as ArrayList<String>
                               } else {
                                   valueList.add(values)
                               }
                           }

                           var count = 0
                           indexes.forEach { _ ->
                               spannable.setSpan(
                                   object :ClickableSpan(){
                                       override fun onClick(widget: View) {
                                           ViewDialog().showListDialog(widget.context,
                                               message,
                                               valueList,
                                               itemPhaseMessage.control!!,
                                               viewPhaseMessage.txtMessage,
                                               this@PhaseMessageRecyclerViewAdapter, itemPhaseMessage._id, position)
                                       }
                                   },
                                   indexes[count], indexes[count] +
                                           itemPhaseMessage.control!!.length,
                                   Spannable.SPAN_INCLUSIVE_INCLUSIVE
                               )
                           }
                       }

                       "<ENTER VALUE>" -> {
                           val placeHolders = itemPhaseMessage.placeholder
                           var placeHolderList = ArrayList<String>()
                           if (placeHolders != null) {
                               if (placeHolders.contains(",")) {
                                   placeHolderList = placeHolders.split(",") as ArrayList<String>
                               } else {
                                   placeHolderList.add(placeHolders)
                               }
                           }

                           var count = 0
                           val spannableList = ArrayList<pet.loyal.provider.model.Spannable>()
                           indexes.forEach { _ ->
                               val placeHolder = placeHolderList[count]
                               val countLocal = count
                               val indexStart = indexes[count]
                               val indexEnd = indexes[count] + itemPhaseMessage.control!!.length
                               val messageLocal = message
                               val clickableSpan = object : ClickableSpan(){
                                   override fun onClick(widget: View) {
                                       ViewDialog().showDialog(widget.context,
                                           messageLocal,
                                           "",
                                           itemPhaseMessage.control!!,
                                           placeHolder,
                                           indexStart,
                                           viewPhaseMessage.txtMessage,
                                           this@PhaseMessageRecyclerViewAdapter,
                                           itemPhaseMessage._id, position, countLocal, spannableList)
                                   }
                               }

                               spannableList.add(pet.loyal.provider.model.Spannable(indexStart, indexEnd, clickableSpan,
                                   itemPhaseMessage.control!!
                               ))
                               spannable.setSpan(clickableSpan, indexStart, indexEnd,
                                   Spannable.SPAN_INCLUSIVE_INCLUSIVE
                               )
                               count++
                           }
                       }
                       else -> {}
                   }

                   viewPhaseMessage.txtMessage.text = spannable
                    viewPhaseMessage.txtMessage.movementMethod = LinkMovementMethod.getInstance()
                }else{
                    viewPhaseMessage.txtMessage.text = itemPhaseMessage.message
//                    viewPhaseMessage.txtMessage.visibility = View.VISIBLE
                }

                viewPhaseMessage.btnDropDown.setOnClickListener {
                    if (viewPhaseMessage.mainContainer.height == 120) {
                        expandView(viewPhaseMessage, viewHolder)
                    }else{
                        collapseView(viewPhaseMessage, viewHolder)
                    }
                }

                viewPhaseMessage.btnAddPhoto.setOnClickListener {
                    phaseMessageItemListener.onClickAddPhotos(viewPhaseMessage.btnAddPhoto, position,
                        itemPhaseMessage._id)
                }
                viewPhaseMessage.mainContainer.setOnClickListener {
                    if (viewPhaseMessage.mainContainer.height == 120) {
                        expandView(viewPhaseMessage, viewHolder)
                    }else{
                        collapseView(viewPhaseMessage, viewHolder)
                    }
                }

                viewPhaseMessage.chkBoxTicketMessage.setOnCheckedChangeListener {
                        buttonView, isChecked ->
                    phaseMessageItemListener.onClickTick(isChecked, position, itemPhaseMessage._id)
                }

                val recyclerViewAdapter = PhaseMessageGalleryRecyclerViewAdapter(
                    Constants.view_type_template_message,
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
                if (itemPhaseMessage.isSelected){
                    expandView(viewPhaseMessage, viewHolder)
                }
                viewPhaseMessage.chkBoxTicketMessage.isChecked = itemPhaseMessage.isSelected
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
                        phaseMessageItemListener.onEditMessageCustom(s.toString(), position,
                            itemPhaseMessage._id)
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int,
                                                   after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int
                    ) {}
                })

                if ((itemPhaseMessage.imageGallery != null
                            && itemPhaseMessage.imageGallery!!.size > 0)
                    || !viewPhaseMessage.edtTxtMessage.text.isNullOrEmpty()){
                    showCheckedCustomMessage(viewPhaseMessage)
                }else{
                    showUncheckedCustomMessage(viewPhaseMessage)
                }

                viewPhaseMessage.chkBoxTicketMessage.setOnCheckedChangeListener { _, isChecked ->

                    if (position == phaseMessagesList.size - 1) {
                        if (isChecked) {
                            showCheckedCustomMessage(viewPhaseMessage)

                        } else {
                            showUncheckedCustomMessage(viewPhaseMessage)
                        }
                        phaseMessageItemListener.onClickTickCustom(isChecked, position, itemPhaseMessage._id)
                    }
                }

                if (position != phaseMessagesList.size - 1){
                    showCheckedCustomMessage(viewPhaseMessage)
                    viewPhaseMessage.btnAddMessage.visibility = View.GONE
                    viewPhaseMessage.chkBoxTicketMessage.isChecked = false
                }else{
                    if(((itemPhaseMessage.imageGallery != null
                        && itemPhaseMessage.imageGallery!!.size > 0)
                        || !viewPhaseMessage.edtTxtMessage.text.isNullOrEmpty())){
                    showCheckedCustomMessage(viewPhaseMessage)
                    viewPhaseMessage.btnAddMessage.visibility = View.VISIBLE
                    viewPhaseMessage.chkBoxTicketMessage.isChecked = true
                    }else{
                        showUncheckedCustomMessage(viewPhaseMessage)
                        viewPhaseMessage.btnAddMessage.visibility = View.VISIBLE
                        viewPhaseMessage.chkBoxTicketMessage.isChecked = false
                    }
                }

                viewPhaseMessage.btnAddMessage.setOnClickListener {
                    if (!viewPhaseMessage.edtTxtMessage.text.isNullOrEmpty()){
                        phaseMessageItemListener.onAddCustomMessage(position)
                        notifyDataSetChanged()
                    }
                }

                val recyclerViewAdapter = PhaseMessageGalleryRecyclerViewAdapter(
                    Constants.view_type_custom_message,
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
            PhaseMessage.Type.PHASE_CHANGE -> {
                val viewPhaseMessage = (viewHolder as PhaseMessagePhaseChangeViewHolder).itemBinding
                viewPhaseMessage.txtSentMessage.text = itemPhaseMessage.message
//                viewPhaseMessage.txtDateTime.text = getTimeString(itemPhaseMessage.dateTime!!) + ", " + getDateString(itemPhaseMessage.dateTime!!)
            }
        }
    }

    private fun getIndexes(message: String, value: String): Array<Int>{
        val indexes = ArrayList<Int>()
        var index = message.indexOf(value)
        while (index >= 0){
            indexes.add(index)
            index = message.indexOf(value, index + 1)
        }
        return indexes.toTypedArray()
    }

    private fun showTimePicker(context: Context, text: String, textView: TextView,
                               replaceValue:String, messageId: String) {
        var c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        val tpd = TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hour, minute->
            val message = text.replace(replaceValue, getTimeStringWithAMPM(hour, minute))
            val spannable = SpannableString(message)
            spannable.setSpan(
                object :ClickableSpan(){
                    override fun onClick(widget: View) {
                        showTimePicker(widget.context,
                            text,
                            textView,
                            replaceValue, messageId)
                    }
                },
                message.indexOf(getTimeStringWithAMPM(hour, minute)), message.indexOf(getTimeStringWithAMPM(hour, minute)) +
                        getTimeStringWithAMPM(hour, minute).length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            textView.text = spannable
            phaseMessageItemListener.onEditMessage(spannable, 0,
                            messageId)
        }, hour, minute, false)
        tpd.show()
    }

    private fun showCheckedCustomMessage(viewPhaseMessage: LayoutEditPatientCardCustomItemBinding) {
        viewPhaseMessage.txtAddCustomMessage.visibility = View.GONE
        viewPhaseMessage.btnAddPhoto.visibility = View.VISIBLE
        viewPhaseMessage.layoutMessage.visibility = View.VISIBLE
        viewPhaseMessage.recyclerViewImageGallery.visibility = View.VISIBLE
    }

    private fun showUncheckedCustomMessage(viewPhaseMessage: LayoutEditPatientCardCustomItemBinding) {
        viewPhaseMessage.txtAddCustomMessage.visibility = View.VISIBLE
        viewPhaseMessage.btnAddPhoto.visibility = View.GONE
        viewPhaseMessage.layoutMessage.visibility = View.GONE
        viewPhaseMessage.recyclerViewImageGallery.visibility = View.GONE
    }

    private fun updateCountValue(message: String, countView: TextView){
        var messageTextCounter = ""
        when {
            Constants.custom_message_character_limit - message.length == 1 ->
                messageTextCounter = "${Constants.custom_message_character_limit - message.length} " +
                        countView.resources.getString(R.string.text_character_remaining)
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
        expand(viewPhaseMessage.mainContainer, 500, 250)
    }

    private fun expandView(
        viewPhaseMessage: LayoutEditPatientCardSentItemBinding,
        viewHolder: PhaseMessageSentViewHolder
    ) {
        viewPhaseMessage.btnDropDown.setImageDrawable(
            viewHolder.itemView.resources
                .getDrawable(R.drawable.ic_drop_up_white, null)
        )
        expand(viewHolder.itemBinding.mainContainer, 500, 240)
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
                PhaseMessage.Type.PHASE_CHANGE -> Constants.view_type_phase_change_message
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

    inner class PhaseMessagePhaseChangeViewHolder(val itemBinding: LayoutEditPatientCardPhaseChangeItemBinding):
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onClickImage(positionImage: Int, position: Int, messageId: String) {
        phaseMessageItemListener.onClickImage(positionImage, position, messageId)
    }

    override fun onClickDelete(positionImage: Int, position: Int, messageId: String) {
        phaseMessageItemListener.onClickDelete(positionImage, position, messageId)
    }

    override fun onUpdateEditText(message: String, replaceValue: String, replaceOldValue: String,
                                  placeHolder: String, index: Int, textView: TextView,
                                  messageId: String, position: Int, no: Int,
                                  spannableList: ArrayList<pet.loyal.provider.model.Spannable>) {

        val messageSpan = message.replaceRange(index, index + replaceOldValue.length, replaceValue)
        val spannable = SpannableString(messageSpan)

        var indexChange = replaceOldValue.length - replaceValue.length
        var count = 0
        spannableList.iterator().forEach { spannableObject ->
            
            var indexStart: Int
            var indexEnd: Int
            when {
                count < no -> {
                    indexStart = spannableObject.start
                    indexEnd = spannableObject.end
                    val countLocal = count

                    val clickableSpan = object : ClickableSpan(){
                        override fun onClick(widget: View) {
                            ViewDialog().showDialog(widget.context,
                                messageSpan,
                                "",
                                spannableObject.replaceOldValue,
                                placeHolder,
                                indexStart,
                                textView,
                                this@PhaseMessageRecyclerViewAdapter,
                                messageId, position, countLocal, spannableList)
                        }
                    }

                    spannable.setSpan(
                        clickableSpan, indexStart, indexEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    spannableList[count] = pet.loyal.provider.model.Spannable(indexStart, indexEnd, clickableSpan, spannableObject.replaceOldValue)
                }
                count == no -> {
                    indexStart = spannableObject.start
                    indexEnd = spannableObject.end - indexChange
                    val countLocal = count

                    val clickableSpan = object :ClickableSpan(){
                        override fun onClick(widget: View) {
                            ViewDialog().showDialog(widget.context,
                                messageSpan,
                                "",
                                replaceValue,
                                placeHolder,
                                indexStart,
                                textView,
                                this@PhaseMessageRecyclerViewAdapter,
                                messageId, position, countLocal, spannableList)
                        }
                    }
                    spannable.setSpan(
                        clickableSpan, indexStart, indexEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )

                    spannableList[count] = pet.loyal.provider.model.Spannable(indexStart, indexEnd, clickableSpan, replaceValue)
                }
                else -> {
                    indexStart = spannableObject.start - indexChange
                    indexEnd = spannableObject.end - indexChange
                    val countLocal = count

                    val clickableSpan = object :ClickableSpan(){
                        override fun onClick(widget: View) {
                            ViewDialog().showDialog(widget.context,
                                messageSpan,
                                "",
                                spannableObject.replaceOldValue,
                                placeHolder,
                                indexStart,
                                textView,
                                this@PhaseMessageRecyclerViewAdapter,
                                messageId, position, countLocal, spannableList)
                        }
                    }
                    spannable.setSpan(
                        clickableSpan, indexStart, indexEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )

                    spannableList[count] = pet.loyal.provider.model.Spannable(indexStart, indexEnd, clickableSpan, spannableObject.replaceOldValue)
                }
            }
            count++
        }
        textView.text = spannable
        phaseMessageItemListener.onEditMessage(spannable, position, messageId)
    }

    override fun onUpdateListView(message: String, valueList: ArrayList<String>, replaceValue: String,
                                  replaceOldValue: String, textView: TextView, messageId: String, position: Int){
        val messageSpan = message.replace(replaceOldValue, replaceValue)
        val spannable = SpannableString(messageSpan)
        spannable.setSpan(
            object :ClickableSpan(){
                override fun onClick(widget: View) {
                    ViewDialog().showListDialog(widget.context,
                        messageSpan,
                        valueList,
                        replaceValue,
                        textView,
                        this@PhaseMessageRecyclerViewAdapter, messageId, position)
                }
            },
            messageSpan.indexOf(replaceValue), messageSpan.indexOf(replaceValue) +
                    replaceValue.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        textView.text = spannable
        phaseMessageItemListener.onEditMessage(spannable, position,
            messageId)
    }
}