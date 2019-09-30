package pet.loyal.provider

import android.app.Application
import com.cloudinary.android.MediaManager
import com.helpscout.beacon.Beacon
import org.json.JSONObject

class LoyalProviderApp: Application() {

    companion object {
        var startedApp = false
    }

    override fun onCreate() {
        super.onCreate()

        MediaManager.init(this)

        Beacon.Builder()
            .withBeaconId(getString(R.string.help_scout_beacon_id))
            .build()
    }
}