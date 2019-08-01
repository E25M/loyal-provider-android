package pet.loyal.provider.model

import com.google.gson.annotations.SerializedName

data class Facility(
    @SerializedName("name") var name: String,
    @SerializedName("displayName") var displayName: String,
    @SerializedName("id") var id: String,
    @SerializedName("status") var status: String,
    @SerializedName("admin") var admin: String?
)