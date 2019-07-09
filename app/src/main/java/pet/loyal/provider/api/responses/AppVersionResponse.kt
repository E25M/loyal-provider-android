package pet.loyal.provider.api.responses

import com.google.gson.annotations.SerializedName
import pet.loyal.client.api.response.AppVersionDataResponse

data class AppVersionResponse(@SerializedName("statusCode") val statusCode: Int,
                              @SerializedName("message") val message: String,
                              @SerializedName("error") val error: String?,
                              @SerializedName("errorMessage") val errorMessage: String?,
                              @SerializedName("data") val data: AppVersionDataResponse?)