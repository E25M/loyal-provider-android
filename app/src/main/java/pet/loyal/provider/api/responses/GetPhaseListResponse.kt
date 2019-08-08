package pet.loyal.provider.api.responses

import com.google.gson.annotations.SerializedName
import pet.loyal.provider.model.Phase

data class GetPhaseListResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ArrayList<Phase>,
    @SerializedName("error") val error: String,
    @SerializedName("errorMessage") val errorMessage: String
)