package pet.loyal.provider.view.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_facility.view.*
import pet.loyal.provider.R
import pet.loyal.provider.model.Facility
import pet.loyal.provider.view.settings.FacilityAdapter.*

class FacilityAdapter(
    val context: Context,
    var facilityList: ArrayList<Facility>,
    val onFacilityClickListener: OnFacilityClickListener
) : RecyclerView.Adapter<FacilityViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.list_item_facility
            , parent,
            false
        )
        return FacilityViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.facilityList.size
    }

    override fun onBindViewHolder(holder: FacilityViewHolder, position: Int) {
        holder.bindData(context, facilityList[position],position, onFacilityClickListener)
    }


    fun updateFacility(facility: Facility) {
        val iterator = this.facilityList.iterator()
        while (iterator.hasNext()) {
            val current = iterator.next()
            current.selected = facility.id == current.id
        }
        notifyDataSetChanged()
    }

    class FacilityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(
            context: Context,
            facility: Facility,
            position: Int,
            onFacilityClickListener: OnFacilityClickListener
        ) {

            if (facility.selected) {
                itemView.card_view_list_item_facility.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.font_color_blue
                    )
                )
                itemView.txt_facility_name_list_item.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.font_color_white
                    )
                )
            } else {
                itemView.card_view_list_item_facility.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.font_color_white
                    )
                )
                itemView.txt_facility_name_list_item.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.font_color_black
                    )
                )
            }
            itemView.txt_facility_name_list_item.text = facility.displayName
            itemView.setOnClickListener {
                if (onFacilityClickListener!= null){
                    onFacilityClickListener.onFacilitySelected(position, facility)
                }
            }

        }
    }
}