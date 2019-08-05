package pet.loyal.provider.api.responses

import com.google.gson.annotations.SerializedName
import pet.loyal.provider.model.Facility

data class GetFacilityResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("error") val error: String?,
    @SerializedName("data") val data: ArrayList<Facility>,
    @SerializedName("errorMessage") val errorMessage: String?
)
