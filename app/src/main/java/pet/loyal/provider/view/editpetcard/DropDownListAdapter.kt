package pet.loyal.provider.view.editpetcard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_drop_down.view.*
import kotlinx.android.synthetic.main.list_item_facility.view.*
import pet.loyal.provider.R
import pet.loyal.provider.model.DropDownItem

class DropDownListAdapter(
    val context: Context,
    var dropDownList: ArrayList<DropDownItem>,
    private val dropDownClickListener: DropDownClickListener
) : RecyclerView.Adapter<DropDownListAdapter.DropDownViewHolder>() {

    interface DropDownClickListener{
        fun onItemClick(position: Int)
        fun onItemSelected(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DropDownViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.list_item_drop_down
            , parent,
            false
        )
        return DropDownViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.dropDownList.size
    }

    override fun onBindViewHolder(holder: DropDownViewHolder, position: Int) {
        holder.bindData(context, dropDownList[position], position, dropDownClickListener)
    }


    class DropDownViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(
            context: Context,
            dropDownItem: DropDownItem,
            position: Int,
            dropDownClickListener: DropDownClickListener
        ) {

            itemView.chkBoxDropDown.isChecked = dropDownItem.isSelected

            itemView.txtDropDownText
                .text = dropDownItem.text
            itemView.setOnClickListener {
                dropDownClickListener.onItemSelected(position)
            }
            itemView.chkBoxDropDown.setOnClickListener {
                dropDownClickListener.onItemSelected(position)
            }
        }
    }
}