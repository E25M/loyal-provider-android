package pet.loyal.provider.view.dialog

import android.content.Intent

interface FragmentActivityControlListener {
    fun onPermissionGranted(granted : Boolean , requestCode: Int)
    fun onActivityResultListener(requestCode: Int, resultCode: Int, data: Intent?)
}