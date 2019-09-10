package pet.loyal.provider.model

data class RequestPTBMessage(val id: String, val message:String, val isCustom: Boolean,
                             val isPhaseChange: Boolean, val gallery: ArrayList<String>?){

    constructor(id: String, message: String, isCustom: Boolean, isPhaseChange: Boolean):this(
        id, message, isCustom, isPhaseChange,null
    )
}