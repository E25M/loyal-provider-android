package pet.loyal.provider.model

import com.google.gson.annotations.SerializedName

data class SentMessage(@SerializedName("phaseMessageId") val phaseMessageId:String,
                       @SerializedName("status") val status: String,
                       @SerializedName("_id") val _id: String,
                       @SerializedName("phaseId") val phaseId:Int,
                       @SerializedName("appointmentId") val appointmentId: String,
                       @SerializedName("message") val message: String,
                       @SerializedName("dateTime") val dateTime:String,
                       @SerializedName("gallery") val gallery:ArrayList<String>?)