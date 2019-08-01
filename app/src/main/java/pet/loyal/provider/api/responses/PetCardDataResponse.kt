package pet.loyal.client.api.response

import com.google.gson.annotations.SerializedName
import pet.loyal.provider.model.Appointment
import pet.loyal.provider.model.MessageTemplate
import pet.loyal.provider.model.Phase
import pet.loyal.provider.model.SentMessage

data class PetCardDataResponse(@SerializedName("appointment") val appointment:Appointment?,
                               @SerializedName("ptbSentMessages") val ptbSentMessages: ArrayList<SentMessage>,
                               @SerializedName("ptbMessageTemplates") val ptbMessageTemplates: ArrayList<MessageTemplate>,
                               @SerializedName("phases") val phases: ArrayList<Phase>)