package pet.loyal.provider.api.responses

import com.google.gson.annotations.SerializedName

data class PetTrackingBoardResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: PetTrackingBoardDataResponse,
    @SerializedName("error") val error: String,
    @SerializedName("errorMessage") val errorMessage: String
)