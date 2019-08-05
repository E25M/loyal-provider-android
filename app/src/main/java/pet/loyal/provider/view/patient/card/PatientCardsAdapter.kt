package pet.loyal.provider.view.patient.card

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_edit_patiant_card.view.*
import kotlinx.android.synthetic.main.list_item_pet_card.view.*
import kotlinx.android.synthetic.main.list_item_phase.view.*
import pet.loyal.provider.R
import pet.loyal.provider.model.PetTrackingAppointment
import pet.loyal.provider.util.formatDate
import pet.loyal.provider.util.getPhaseColors
import java.lang.StringBuilder
import java.text.SimpleDateFormat

class PatientCardsAdapter(
    val context: Context,
    var cardsList: ArrayList<PetTrackingAppointment>,
    val onPetCardClickListener: OnPetCardClickListener
) :
    RecyclerView.Adapter<PatientCardsAdapter.PetCardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetCardViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.list_item_pet_card,
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
//        holder.bindData(cardsList[position], position, onPetCardClickListener , context)
        val card = cardsList[position]
        holder.itemView.txt_last_name_list_item_pet_card.text =
            card.parentLastName + ", "
        holder.itemView.txt_first_name_list_item_pet_card.text =
            card.petSpecies + " , " + card.petBreed

        holder.itemView.txt_date_time_lis_item_pet_card.text =
            formatDate(card.dateTime, SimpleDateFormat("MM/dd/yyyy ',' HH:mm 'Z'"))
        holder.itemView.btn_list_item_phase_name.text = card.type
        holder.itemView.btn_list_item_phase_name.setBackgroundColor(
            getPhaseColors(
                card.phase,
                context
            )
        )

        holder.itemView.recyclerViewMessages.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val adapter = PTBSentMessagesAdapter(context, card.ptbSentMessages)
        holder.itemView.recyclerViewMessages.adapter = adapter
        holder.itemView.setOnClickListener {
            if (onPetCardClickListener!= null){
                onPetCardClickListener.onPerCardClick(card, position)
            }
        }

    }


    class PetCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        fun bindData(
//            card: PetTrackingAppointment,
//            position: Int,
//            onPetCardClickListener: OnPetCardClickListener,
//            context : Context
//        ) {
//            itemView.txt_last_name_list_item_pet_card.text =
//                card.parentLastName + ", "
//            itemView.txt_first_name_list_item_pet_card.text =
//                card.petSpecies + " , " + card.petBreed
//
//            itemView.txt_date_time_lis_item_pet_card.text =
//                formatDate(card.dateTime , SimpleDateFormat("MM/dd/yyyy ',' HH:mm 'Z'"))
//            itemView.btn_list_item_phase_name.text = card.type
//            itemView.btn_list_item_phase_name.setBackgroundColor(getPhaseColors(card.phase, context))
//
//        }

    }

}