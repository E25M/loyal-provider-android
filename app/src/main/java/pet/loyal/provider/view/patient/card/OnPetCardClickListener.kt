package pet.loyal.provider.view.patient.card

import pet.loyal.provider.model.PetTrackingAppointment

interface OnPetCardClickListener {
    fun onPerCardClick(card : PetTrackingAppointment , position : Int)
}