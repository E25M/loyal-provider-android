package pet.loyal.provider.view.patient.card

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_pet_card_messages.view.*
import pet.loyal.provider.R
import pet.loyal.provider.model.SentMessage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PTBSentMessagesAdapter(val context: Context, var messageList: ArrayList<SentMessage>) :
    RecyclerView.Adapter<PTBSentMessagesAdapter.MessageViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.list_item_pet_card_messages, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bindData(messageList[position], position, context)
    }


    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val defaultFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val displayFormat = SimpleDateFormat("MM/dd/yyyy")
        val timeDisplayFormat = SimpleDateFormat("HH:MM Z")

        fun bindData(message: SentMessage, position: Int, context: Context) {
            itemView.txt_msg_list_item_sent_message.text = message.message
            itemView.txt_date_time_list_item_sent_message.text =
                formatTime(message.dateTime) + " " + formatDate(message.dateTime)

            if (position % 2 != 0) {
                itemView.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.bg_color_gray_3
                    )
                )
            }
        }

        fun formatDate(dateString: String): String {
            val date = defaultFormat.parse(dateString)
            return displayFormat.format(date)
        }

        fun reFormatDate(dateString: String): String {
            val date = displayFormat.parse(dateString)
            return defaultFormat.format(date)
        }

        fun formatTime(dateString: String): String {
            val date = defaultFormat.parse(dateString)
            return timeDisplayFormat.format(date)
        }

    }

}