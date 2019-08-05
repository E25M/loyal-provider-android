package pet.loyal.provider.view.settings

import pet.loyal.provider.model.Facility

interface OnFacilityClickListener {
    fun onFacilitySelected(position : Int , facility : Facility)
}