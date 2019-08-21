package pet.loyal.provider.view.mainmenu

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pet.loyal.provider.util.showToast

class MainMenuViewModel : ViewModel() {

    var disableAlpha : MutableLiveData<Float> = MutableLiveData()
    var enableAlpha : MutableLiveData<Float> = MutableLiveData()

    init {
        disableAlpha.value = 0.2f
        enableAlpha.value = 1f
    }

    fun startIntent(action : Int , context : Context){
        showToast(context, " number is : $action")
    }

}
