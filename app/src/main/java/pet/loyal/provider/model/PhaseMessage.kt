package pet.loyal.provider.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class PhaseMessage(@SerializedName("phaseMessageId") var phaseMessageId:String?,
                        @SerializedName("status") var status: String?,
                        @SerializedName("_id") var _id: String,
                        @SerializedName("phaseId") var phaseId:Int,
                        @SerializedName("appointmentId") var appointmentId: String?,
                        @SerializedName("message") var message: String,
                        @SerializedName("dateTime") var dateTime:String?,
                        @SerializedName("gallery") var imageGallery: ArrayList<Uri>?,
                        var imageIds: ArrayList<String>?,
                        var editable: Boolean,
                        var type: Type,
                        var isSelected: Boolean){

    enum class Type{SENT_MESSAGE, MESSAGE_TEMPLATE, CUSTOM_MESSAGE}

    constructor(id: String, phaseId: Int, message: String, editable: Boolean,imageGallery: ArrayList<Uri>?)
            : this(null, null,
        id, phaseId, null, message,null, imageGallery, null, editable,
        Type.MESSAGE_TEMPLATE, false)

    constructor(phaseMessageId: String?, status: String?, id: String, phaseId: Int,
                appointmentId: String?, message: String, dateTime: String?,
                imageGallery: ArrayList<Uri>?) : this(phaseMessageId, status, id, phaseId,
        appointmentId, message, dateTime, imageGallery, null, false, Type.SENT_MESSAGE,
        false)

    constructor(id: String, phaseId: Int, appointmentId: String?) : this(null,
        null, id, phaseId, appointmentId, "", null, null,
        null, false, Type.CUSTOM_MESSAGE, false)

    fun getIsCustom() : Boolean{
        if (type == Type.CUSTOM_MESSAGE){
            return true
        }
        return false
    }
}