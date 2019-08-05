package pet.loyal.provider.view.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.layout_dialog_facility.*
import pet.loyal.provider.R
import pet.loyal.provider.model.Facility

class FacilityFragment : DialogFragment(), OnFacilityClickListener {

    lateinit var containerView: View

    var dialogListener: OnFacilityClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_dialog_facility, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        containerView = view.findViewById(R.id.constraint_layout_facility_main_container)
        if (targetFragment != null && targetRequestCode == 3) {
            dialogListener = targetFragment as OnFacilityClickListener
        }
        recyclerview_facility_list.layoutManager =
            LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(400, 400)
        loadData()
    }

    private fun loadData() {
        if (arguments != null) {
            if (arguments!!.containsKey("data")) {
                val breedList: ArrayList<Facility> =
                    arguments!!.getParcelableArrayList<Facility>("data")
                if (breedList != null && breedList.size > 0) {
                    val tempList = ArrayList<Facility>()
                    tempList.addAll(breedList)
                    refreshData(tempList)
                    edittext_facility_name.addTextChangedListener(object : TextWatcher {

                        override fun afterTextChanged(textPhrase: Editable?) {
                            if (TextUtils.isEmpty(textPhrase.toString())) {
                                tempList.clear()
                                tempList.addAll(breedList)
                            } else {
                                tempList.clear()
                                val iterator = breedList.iterator()
                                while (iterator.hasNext()) {
                                    val breed = iterator.next()
                                    if (breed.name.contains(textPhrase.toString(), true)) {
                                        tempList.add(breed)
                                    } else {
                                        tempList.remove(breed)
                                    }
                                }
                            }
                            refreshData(tempList)
                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                        }

                        override fun beforeTextChanged(
                            p0: CharSequence?,
                            p1: Int,
                            p2: Int,
                            p3: Int
                        ) {

                        }

                    })
                } else {
                    handleError()
                }
            } else {
                handleError()
            }
        } else {
            handleError()
        }
    }

    private fun handleError() {
        val snackbar = Snackbar.make(containerView, R.string.error_common, Snackbar.LENGTH_LONG)
        snackbar.setAction("RETRY") {
            loadData()
        }
        snackbar.show()
    }

    override fun onFacilitySelected(position: Int, facility: Facility) {
        if (recyclerview_facility_list.adapter != null) {
            val adapter = recyclerview_facility_list.adapter as FacilityAdapter
            adapter.updateFacility(facility)
            dialogListener?.onFacilitySelected(position, facility)
        }
        this.dismiss()
    }

    private fun refreshData(breedList: ArrayList<Facility>?) {
        val adapter = FacilityAdapter(context!!, breedList!!, this)
        recyclerview_facility_list.adapter = adapter
    }

}