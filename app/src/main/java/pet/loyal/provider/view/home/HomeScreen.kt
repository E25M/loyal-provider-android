package pet.loyal.provider.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pet.loyal.provider.R
import pet.loyal.provider.view.selfinvite.SelfInviteFragment

class HomeScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_home_screen)

//        loadSelfInviteFragment()
    }

    private fun loadSelfInviteFragment() {
        val newsFeedFragment = SelfInviteFragment()
        supportFragmentManager.beginTransaction().replace(R.id.mainContainer, newsFeedFragment)
            .disallowAddToBackStack().commit()
    }
}
