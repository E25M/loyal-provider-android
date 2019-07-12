package pet.loyal.provider.api.responses

import com.google.gson.annotations.SerializedName
import pet.loyal.provider.model.LoyalUser

class SelfInviteDataResponse(@SerializedName("data") val data: LoyalUser,
                             @SerializedName("status") val status:String)