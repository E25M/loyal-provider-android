package pet.loyal.provider.util

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import androidx.annotation.RequiresApi
import android.view.animation.DecelerateInterpolator
import android.animation.ValueAnimator
import android.view.View
import pet.loyal.provider.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun getRequestHeaders(token: String): HashMap<String, String> {
    val hashMap = hashMapOf<String, String>()
    hashMap["Content-type"] = "application/json"
    hashMap["Authorization"] = token
    return hashMap
}

fun getRequestHeadersWithToken(token: String, pushToken: String): HashMap<String, String> {
    val hashMap = getRequestHeaders(token)
    hashMap["pushToken"] = pushToken
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

fun showAlertDialog(context: Context, title: String, message: String) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton(
        context.getString(R.string.btn_name_ok)
    ) { dialogInterface, _ -> dialogInterface.dismiss() }
    builder.create().show()
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

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidPhone(phone: CharSequence): Boolean {
    return android.util.Patterns.PHONE.matcher(phone).matches()
}

@RequiresApi(Build.VERSION_CODES.M)
fun getPhaseColors(phaseId: Int, context: Context): Int {
    return when (phaseId) {
        1 -> context.resources.getColor(R.color.phase_color_expected, null)
        2 -> context.resources.getColor(R.color.phase_color_check_in, null)
        3 -> context.resources.getColor(R.color.phase_color_diagnostics, null)
        4 -> context.resources.getColor(R.color.phase_color_pre_op, null)
        5 -> context.resources.getColor(R.color.phase_color_procedure, null)
        6 -> context.resources.getColor(R.color.phase_color_dental, null)
        7 -> context.resources.getColor(R.color.phase_color_surgery, null)
        8 -> context.resources.getColor(R.color.phase_color_recovery, null)
        9 -> context.resources.getColor(R.color.phase_color_hospitalized, null)
        10 -> context.resources.getColor(R.color.phase_color_boarding, null)
        11 -> context.resources.getColor(R.color.phase_color_discharge, null)
        12 -> context.resources.getColor(R.color.phase_color_completed, null)
        else -> context.resources.getColor(R.color.bg_color_black, null)
    }
}

fun getPhaseColorsOld(phaseId: Int, context: Context): Int {
    return when (phaseId) {
        1 -> context.resources.getColor(R.color.phase_color_expected)
        2 -> context.resources.getColor(R.color.phase_color_check_in)
        3 -> context.resources.getColor(R.color.phase_color_diagnostics)
        4 -> context.resources.getColor(R.color.phase_color_pre_op)
        5 -> context.resources.getColor(R.color.phase_color_procedure)
        6 -> context.resources.getColor(R.color.phase_color_dental)
        7 -> context.resources.getColor(R.color.phase_color_surgery)
        8 -> context.resources.getColor(R.color.phase_color_recovery)
        9 -> context.resources.getColor(R.color.phase_color_hospitalized)
        10 -> context.resources.getColor(R.color.phase_color_boarding)
        11 -> context.resources.getColor(R.color.phase_color_discharge)
        12 -> context.resources.getColor(R.color.phase_color_completed)
        else -> context.resources.getColor(R.color.bg_color_black)
    }
}

fun expand(v: View, duration: Int, targetHeight: Int) {

    val prevHeight = v.height

    v.visibility = View.VISIBLE
    val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
    valueAnimator.addUpdateListener { animation ->
        v.layoutParams.height = animation.animatedValue as Int
        v.requestLayout()
    }
    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.duration = duration.toLong()
    valueAnimator.start()
}

fun collapse(v: View, duration: Int, targetHeight: Int) {
    val prevHeight = v.height
    val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.addUpdateListener { animation ->
        v.layoutParams.height = animation.animatedValue as Int
        v.requestLayout()
    }
    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.duration = duration.toLong()
    valueAnimator.start()
}

fun getDateString(dateAndTime: String): String {

    // Create a DateFormatter object for displaying date in specified format.
    val receivedFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val sendFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

    receivedFormat.timeZone = TimeZone.getTimeZone("UTC")

    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar = Calendar.getInstance()
    try {
        calendar.time = receivedFormat.parse(dateAndTime)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    val sendDate = sendFormat.format(calendar.time)
//    val relativeTime = DateUtils.getRelativeTimeSpanString(calendar.timeInMillis)
//    val sendFormatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    return sendDate.toString()
}

fun getTimeString(dateAndTime: String): String {

    // Create a DateFormatter object for displaying date in specified format.
    val receivedFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val sendFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
//    val sendFormatDay = SimpleDateFormat("EEEE", Locale.getDefault())

    receivedFormat.timeZone = TimeZone.getTimeZone("UTC")

    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar = Calendar.getInstance()
    try {
        calendar.time = receivedFormat.parse(dateAndTime)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return sendFormat.format(calendar.time)
}

fun getCurrentDateString(): String {

    // Create a DateFormatter object for displaying date in specified format.
//    val receivedFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val sendFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

//    receivedFormat.timeZone = TimeZone.getTimeZone("UTC")

    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar = Calendar.getInstance()
//    try {
//        calendar.time = receivedFormat.parse(dateAndTime)
//    } catch (e: ParseException) {
//        e.printStackTrace()
//    }
    val sendDate = sendFormat.format(calendar.time)
//    val relativeTime = DateUtils.getRelativeTimeSpanString(calendar.timeInMillis)
//    val sendFormatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    return sendDate.toString()
}

fun getCurrentTimeString(): String {

    // Create a DateFormatter object for displaying date in specified format.
//    val receivedFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val sendFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
//    val sendFormatDay = SimpleDateFormat("EEEE", Locale.getDefault())

//    receivedFormat.timeZone = TimeZone.getTimeZone("UTC")

    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar = Calendar.getInstance()
//    try {
//        calendar.time = receivedFormat.parse(dateAndTime)
//    } catch (e: ParseException) {
//        e.printStackTrace()
//    }

    return sendFormat.format(calendar.time)
}

