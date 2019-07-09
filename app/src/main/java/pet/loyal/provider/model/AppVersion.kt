package pet.loyal.provider.model

import com.google.gson.annotations.SerializedName

data class AppVersion(@SerializedName("tncVersion") val tncVersion:String,
                      @SerializedName("isForced") val isForced: Boolean,
                      @SerializedName("downloadUrl") val downloadUrl: Int)