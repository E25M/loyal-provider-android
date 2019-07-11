package pet.loyal.provider.api.responses

import com.google.gson.annotations.SerializedName
import pet.loyal.provider.model.LoginUser

data class LoginDataResponse (
    @SerializedName("user") val user: LoginUser,
    @SerializedName("token") val token: String
)