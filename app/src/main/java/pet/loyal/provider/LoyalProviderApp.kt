package pet.loyal.provider

import android.app.Application
import com.cloudinary.android.MediaManager

class LoyalProviderApp: Application() {

    override fun onCreate() {
        super.onCreate()

        MediaManager.init(this)
    }
}