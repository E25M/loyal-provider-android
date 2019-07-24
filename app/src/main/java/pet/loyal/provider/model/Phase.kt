package pet.loyal.provider.model

import com.google.gson.annotations.SerializedName

data class Phase (
    @SerializedName("id") val id : String,
    @SerializedName("name") val name : String
)

