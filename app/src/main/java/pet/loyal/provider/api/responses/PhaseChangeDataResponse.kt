package pet.loyal.provider.api.responses

import com.google.gson.annotations.SerializedName
import pet.loyal.provider.model.Appointment
import pet.loyal.provider.model.MessageTemplate
import pet.loyal.provider.model.SentMessage

class PhaseChangeDataResponse(@SerializedName("message") val message: String,
                              @SerializedName("appointment") val appointment: Appointment?,
                              @SerializedName("ptbSentMessages") val ptbSentMessages: ArrayList<SentMessage>,
                              @SerializedName("ptbMessageTemplates") val ptbMessageTemplates: ArrayList<MessageTemplate>)