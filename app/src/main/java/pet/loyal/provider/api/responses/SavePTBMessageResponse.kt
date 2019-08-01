package pet.loyal.provider.api.responses

import com.google.gson.annotations.SerializedName

data class SavePTBMessageResponse(@SerializedName("statusCode") val statusCode: Int,
                                  @SerializedName("message") val message: String,
                                  @SerializedName("error") val error: String?,
                                  @SerializedName("errorMessage") val errorMessage: String?)