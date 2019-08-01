package pet.loyal.provider.api.responses

import com.google.gson.annotations.SerializedName
import pet.loyal.provider.model.PetTrackingAppointment
import pet.loyal.provider.model.Phase

data class PetTrackingBoardDataResponse(
    @SerializedName("appointments") val appointments: ArrayList<PetTrackingAppointment>,
    @SerializedName("phases") val phases: ArrayList<Phase>
)