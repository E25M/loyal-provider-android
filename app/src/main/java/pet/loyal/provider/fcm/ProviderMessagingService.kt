package pet.loyal.provider.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pet.loyal.provider.util.PreferenceManager
import java.util.*

class ProviderMessagingService : FirebaseMessagingService() {

    lateinit var preferenceManager: PreferenceManager

    override fun onNewToken(token: String?) {

       if (token != null){
           preferenceManager = PreferenceManager(this)
           preferenceManager.setPushToken(token)
           preferenceManager.setDeviceId(getDeviceId())
       }
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
    }


    private fun getDeviceId() : String {
        return UUID.randomUUID().toString()
    }
}