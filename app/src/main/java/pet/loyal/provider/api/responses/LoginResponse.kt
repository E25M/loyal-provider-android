package pet.loyal.provider.api.responses

import com.google.gson.annotations.SerializedName

class LoginResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: LoginDataResponse,
    @SerializedName("error") val error: String,
    @SerializedName("errorMessage") val errorMessage: String
)