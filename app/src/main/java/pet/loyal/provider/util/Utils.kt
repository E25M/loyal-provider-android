package pet.loyal.provider.util

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap


fun getRequestHeaders(token: String): HashMap<String, String> {
    val hashMap = hashMapOf<String, String>()
    hashMap["Content-type"] = "application/json"
    hashMap["Authorization"] = token
    return hashMap
}

fun isJSONValid(jsonString: String): Boolean {
    try {
        JSONObject(jsonString)
    } catch (ex: JSONException) {
        return false
    }
    return true
}

fun showPopupWithFinish(context: Context, message: String, title: String) {
    val aDialog = AlertDialog.Builder(context)
        .setMessage(message)
        .setTitle(title)
        .setCancelable(false)
        .setPositiveButton(android.R.string.ok) { _, _ ->
            (context as AppCompatActivity).finishAffinity()
        }
        .create()
    aDialog.show()
}

fun showPopup(context: Context, message: String, title: String) {
    val aDialog = AlertDialog.Builder(context)
        .setMessage(message)
        .setTitle(title)
        .setPositiveButton(android.R.string.ok) { _, _ ->

        }
        .create()
    aDialog.show()
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

// method to check if the device has an active internet connection
fun isConnected(context: Context): Boolean {
    var connected = false
    return try {
        val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = cm.activeNetworkInfo
        connected = nInfo != null && nInfo.isAvailable && nInfo.isConnected
        connected
    } catch (e: Exception) {
        false
    }
}
