package pet.loyal.provider.util

import android.content.Intent

interface EditPetCardPermissionListener {
    fun onPermissionGranted(granted : Boolean , requestCode: Int, grantResults: IntArray)
    fun onActivityResultListener(requestCode: Int, resultCode: Int, data: Intent?)
}