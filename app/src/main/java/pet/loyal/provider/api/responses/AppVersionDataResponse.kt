package pet.loyal.client.api.response

import com.google.gson.annotations.SerializedName

data class AppVersionDataResponse(@SerializedName("version") val version:String,
                                  @SerializedName("isForced") val isForced: Boolean,
                                  @SerializedName("downloadUrl") val downloadUrl: String)