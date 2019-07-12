package pet.loyal.provider.model

import com.google.gson.annotations.SerializedName

data class LoginUser (
    @SerializedName("type") val type: String,
    @SerializedName("_id") val id: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("username") val userName: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String,
    @SerializedName("designation") val fbConnected: Boolean,
    @SerializedName("avatar") val avatar: String
)
