package pet.loyal.provider.api.responses

import com.google.gson.annotations.SerializedName

class SelfInviteResponse(@SerializedName("statusCode") val statusCode: Int,
                         @SerializedName("message") val message: String,
                         @SerializedName("error") val error: String?,
                         @SerializedName("errorMessage") val errorMessage: String?,
                         @SerializedName("data") val data: SelfInviteDataResponse?)