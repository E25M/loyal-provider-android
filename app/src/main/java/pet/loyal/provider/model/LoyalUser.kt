package pet.loyal.provider.model

import com.google.gson.annotations.SerializedName

data class LoyalUser(@SerializedName("_id") val _id:String,
                     @SerializedName("firstName") val firstName:String,
                     @SerializedName("lastName") val lastName:String,
                     @SerializedName("email") val email:String,
                     @SerializedName("status") val status:String)