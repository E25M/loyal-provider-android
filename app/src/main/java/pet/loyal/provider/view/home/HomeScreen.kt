package pet.loyal.provider.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.json.JSONObject
import pet.loyal.provider.R
import pet.loyal.provider.view.editpetcard.EditPetCardFragment
import pet.loyal.provider.view.selfinvite.SelfInviteFragment
import java.util.*

class HomeScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_home_screen)
    }

    private fun loadSelfInviteFragment() {
        val newsFeedFragment = EditPetCardFragment()
        supportFragmentManager.beginTransaction().replace(R.id.mainContainer, newsFeedFragment)
            .disallowAddToBackStack().commit()
    }
}
