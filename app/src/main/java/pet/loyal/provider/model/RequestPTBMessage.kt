package pet.loyal.provider.model

data class RequestPTBMessage(val id: String, val message:String, val isCustom: Boolean,
                             val gallery: ArrayList<String>?)