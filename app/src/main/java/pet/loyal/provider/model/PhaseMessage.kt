package pet.loyal.provider.model

import com.google.gson.annotations.SerializedName

data class PhaseMessage(@SerializedName("phaseMessageId") var phaseMessageId:String?,
                        @SerializedName("status") var status: String?,
                        @SerializedName("_id") var _id: String,
                        @SerializedName("phaseId") var phaseId:Int,
                        @SerializedName("appointmentId") var appointmentId: String?,
                        @SerializedName("message") var message: String,
                        @SerializedName("dateTime") var dateTime:String?,
                        var imageGallery: ArrayList<String>?,
                        var type: Type){

    enum class Type{SENT_MESSAGE, MESSAGE_TEMPLATE, CUSTOM_MESSAGE}

    constructor(id: String, phaseId: Int, message: String) : this(null, null,
        id, phaseId, null, message,null,null,
        Type.MESSAGE_TEMPLATE)

    constructor(phaseMessageId: String?, status: String?, id: String, phaseId: Int,
                appointmentId: String?, message: String, dateTime: String?) : this(phaseMessageId,
        status, id, phaseId, appointmentId, message, dateTime, null, Type.SENT_MESSAGE)
}