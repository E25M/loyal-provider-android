package pet.loyal.provider

import android.app.Application
import com.cloudinary.android.MediaManager
import com.helpscout.beacon.Beacon

class LoyalProviderApp: Application() {

    override fun onCreate() {
        super.onCreate()

        MediaManager.init(this)

        Beacon.Builder()
            .withBeaconId(getString(R.string.help_scout_beacon_id))
            .build()
    }
}