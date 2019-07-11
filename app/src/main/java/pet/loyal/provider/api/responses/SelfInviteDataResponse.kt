package pet.loyal.provider.api.responses

import com.google.gson.annotations.SerializedName

class SelfInviteDataResponse(@SerializedName("_id") val id:String,
                             @SerializedName("firstName") val firstName:String,
                             @SerializedName("lastName") val lastName:String,
                             @SerializedName("email") val email:String,
                             @SerializedName("status") val status:String,
                             @SerializedName("loggedFirstTime") val loggedFirstTime:Boolean)