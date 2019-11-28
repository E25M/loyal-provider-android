package pet.loyal.provider.view.patient.card

import android.content.Context
import android.graphics.LightingColorFilter
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_pet_card.view.*
import pet.loyal.provider.R
import pet.loyal.provider.model.PetTrackingAppointment
import pet.loyal.provider.util.*
import java.text.SimpleDateFormat

class PatientCardsAdapter(
    val context: Context,
    var cardsList: ArrayList<PetTrackingAppointment>,
    val onPetCardClickListener: OnPetCardClickListener
) :
    RecyclerView.Adapter<PatientCardsAdapter.PetCardViewHolder>(), OnMessagesClickListener {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetCardViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.list_item_pet_card_new,
            parent,
            false
        )
        return PetCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cardsList.size
    }

    fun updateList(arrayList: ArrayList<PetTrackingAppointment>) {
        this.cardsList.clear()
        this.cardsList.addAll(arrayList)
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: PetCardViewHolder, position: Int) {

        val card = cardsList[position]
        holder.itemView.txt_last_name_list_item_pet_card.text =
            card.petName + ", "
        holder.itemView.txt_first_name_list_item_pet_card.text =
            card.parentLastName

        holder.itemView.txt_date_time_lis_item_pet_card.text =
            formatDate(card.dateTime, SimpleDateFormat("hh:mm a"))

        if (card.petImage != null) {
            if (!TextUtils.isEmpty(card.petImage)) {
                Picasso.get().load(Constants.url_cloudinary_pet_profile + card.petImage)
                    .error(R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo).into(holder.itemView.img_pet_)
            }
        }

        holder.itemView.txt_phase_list_item_pet_card.text = getPhaseName(card.phase)
        val bgColor: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPhaseColors(card.phase, context!!)
        } else {
            getPhaseColorsOld(card.phase, context!!)
        }

        val drawableBgType = context.resources.getDrawable(R.drawable.bg_general, null)
        val colorFilter = LightingColorFilter(bgColor, bgColor)
        drawableBgType.colorFilter = colorFilter
        holder.itemView.txt_phase_list_item_pet_card.background = drawableBgType
        holder.itemView.view_colored_header.setBackgroundColor(bgColor)

//        holder.itemView.reyclerview_cards_list_item_pet_cards.layoutManager =
//            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
//        val adapter = PTBSentMessagesAdapter(
//            context
//            , card.ptbSentMessages
//            , position
//            , this
//        )
//        if (card.ptbSentMessages.size > 0) {
//            holder.itemView.reyclerview_cards_list_item_pet_cards.adapter = adapter
//        }else{
//            holder.itemView.reyclerview_cards_list_item_pet_cards.visibility = View.GONE
//        }
        holder.itemView.constraint_layout_pet_card_container.setOnClickListener {
            onPetCardClickListener?.onPerCardClick(card, position)
        }
//        holder.itemView.reyclerview_cards_list_item_pet_cards.setOnClickListener {
//            onPetCardClickListener?.onPerCardClick(card, position)
//        }
    }


    class PetCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onMessageClick(position: Int) {
        onPetCardClickListener?.onPerCardClick(this.cardsList[position], position)
    }

}