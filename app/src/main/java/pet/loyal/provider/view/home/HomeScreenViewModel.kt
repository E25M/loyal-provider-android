package pet.loyal.provider.view.home

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeScreenViewModel : ViewModel() {

    var toolbarVisibility = MutableLiveData<Int>()

    init {
        toolbarVisibility.value = View.VISIBLE
    }

}