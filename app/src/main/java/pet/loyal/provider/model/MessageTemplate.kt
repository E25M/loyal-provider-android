package pet.loyal.provider.model

import com.google.gson.annotations.SerializedName

data class MessageTemplate(@SerializedName("fetchable") val fetchable:Boolean,
                           @SerializedName("fetchableValue") val fetchableValue: String,
                           @SerializedName("notificationType") val notificationType: String,
                           @SerializedName("order") val order:Int,
                           @SerializedName("_id") val _id: String,
                           @SerializedName("phaseId") val phaseId: Int,
                           @SerializedName("message") val message:String)