package pet.loyal.client.api.response

import com.google.gson.annotations.SerializedName
import pet.loyal.provider.model.AppVersion

data class AppVersionDataResponse(@SerializedName("tncVersion") val tncVersion:String,
                                  @SerializedName("isForced") val isForced: Boolean,
                                  @SerializedName("downloadUrl") val downloadUrl: Int)