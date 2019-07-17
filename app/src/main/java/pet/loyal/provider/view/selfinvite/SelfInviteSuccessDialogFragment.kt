package pet.loyal.provider.view.selfinvite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import pet.loyal.provider.R

class SelfInviteSuccessDialogFragment : DialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_self_invite_success, container, false)
        val btnOk = view.findViewById<Button>(R.id.btnSelfInviteOk)
        btnOk.setOnClickListener {
            dismiss()
        }
        return view
    }

    //     load the image once the image is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

}