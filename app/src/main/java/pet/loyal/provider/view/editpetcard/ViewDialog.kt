package pet.loyal.provider.view.editpetcard

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import pet.loyal.provider.R
import pet.loyal.provider.model.DropDownItem
import pet.loyal.provider.model.Spannable
import java.lang.StringBuilder

class ViewDialog : DropDownListAdapter.DropDownClickListener{

    val dropDownList = ArrayList<DropDownItem>()
    lateinit var recyclerView:RecyclerView

    interface ViewDialogListener{
        fun onUpdateEditText(message: String, replaceValue: String, replaceOldValue: String, placeHolder: String, index: Int,
                             textView: TextView, messageId: String, position: Int, no: Int,spannableList:ArrayList<Spannable>)
        fun onUpdateListView(message: String, valueList: ArrayList<String>, replaceValue: String, replaceOldValue: String,
                             textView: TextView, messageId: String, position: Int)
    }

    fun showDialog(activity: Context, message: String, replaceValue: String, replaceOldValue: String,
                   placeHolder: String, index: Int, textView: TextView, viewDialogListener:
                   ViewDialogListener, messageId: String, position: Int, no: Int,
                   spannableList:ArrayList<Spannable>) {

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_change_phase_message)

        if (dialog != null) {
            val width = 400
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
        }

//        val textTitle = dialog.findViewById(R.id.txtTitle) as TextView

        val textContact = dialog.findViewById(R.id.txtContact) as TextInputEditText

        textContact.hint = placeHolder
        if (replaceOldValue.isNotEmpty() && replaceOldValue != "<ENTER VALUE>"){
            textContact.setText(replaceOldValue)
        }

        val layoutContact = dialog.findViewById(R.id.layoutContact) as TextInputLayout

        val buttonUpdate = dialog.findViewById(R.id.btnUpdate) as Button

        buttonUpdate.setOnClickListener {
            viewDialogListener.onUpdateEditText(message, textContact.text.toString(),
                replaceOldValue, placeHolder, index, textView, messageId, position, no, spannableList)
            dialog.dismiss()
        }

        val buttonCancel = dialog.findViewById(R.id.btnCancel) as Button
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showListDialog(activity: Context, message: String, valueList: ArrayList<String>,
                       replaceOldValue: String, textView: TextView,
                       viewDialogListener: ViewDialogListener, messageId: String, position: Int){

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_change_list_phase_message)
        if (dialog != null) {
            val width = 400
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
        }

        valueList.forEach {
            dropDownList.add(DropDownItem(false, it))
        }

        if (replaceOldValue.contains(",")) {
            val oldSelectedList = replaceOldValue.split(",")
            dropDownList.iterator().forEach {dropDownItem ->
                oldSelectedList.forEach { oldSelectedItem ->
                    if (dropDownItem.text == oldSelectedItem){
                        dropDownItem.isSelected = true
                        return@forEach
                    }
                }
            }
        }

        recyclerView = dialog.findViewById(R.id.listViewChangeList) as RecyclerView
        val adapter = DropDownListAdapter(activity, dropDownList, this)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        val buttonUpdate = dialog.findViewById(R.id.btnUpdate) as Button
        buttonUpdate.setOnClickListener {
            val selectedText = StringBuilder()
            dropDownList.forEach {
                if (it.isSelected) {
                    selectedText.append(it.text + ",")
                }
            }

            if (selectedText.toString().isNotEmpty()) {
                viewDialogListener.onUpdateListView(
                    message, valueList,
                    selectedText.toString().substring(0, selectedText.length - 1), replaceOldValue,
                    textView, messageId, position
                )
            }else{
                viewDialogListener.onUpdateListView(
                    message, valueList,
                    "dropDown", replaceOldValue,
                    textView, messageId, position
                )
            }
            dialog.dismiss()
        }

        val buttonCancel = dialog.findViewById(R.id.btnCancel) as Button
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onItemClick(position: Int) {}

    override fun onItemSelected(position: Int) {
        dropDownList.iterator().forEach {
            if (it == dropDownList[position]){
                it.isSelected = !it.isSelected
            }
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }
}