package pet.loyal.provider.api.responses

import com.google.gson.annotations.SerializedName

data class CommonResponse (
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("error") val error: String?,
    @SerializedName("data") val data: String?,
    @SerializedName("errorMessage") val errorMessage: String?
)