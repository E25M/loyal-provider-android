package pet.loyal.provider.view.patient

import pet.loyal.provider.model.Phase

interface OnPhaseClickListener {
    fun onPhaseClick(position : Int , phase : Phase)
    fun onPhaseTurnedOn(position : Int , phase : Phase)
    fun onPhaseTurnedOff(position : Int , phase : Phase)
}