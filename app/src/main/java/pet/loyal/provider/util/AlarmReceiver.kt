package pet.loyal.provider.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent!!.action == "pet.loyal.provider.reset") {
            context!!.sendBroadcast(Intent("pet.loyal.provider.reset2"))
        }
    }
}