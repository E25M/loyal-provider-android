package pet.loyal.provider.view.mainmenu

import android.content.Context
import androidx.lifecycle.ViewModel
import pet.loyal.provider.util.showToast

class MainMenuViewModel : ViewModel() {



    fun startIntent(action : Int , context : Context){
        showToast(context, " number is : $action")
    }

}
