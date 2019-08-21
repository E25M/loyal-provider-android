package pet.loyal.provider.model

import com.google.gson.annotations.SerializedName

data class Appointment(@SerializedName("_id") val id:String,
                       @SerializedName("phase") val phase: Int,
                       @SerializedName("phaseHistory") val phaseHistory: ArrayList<Int>,
                       @SerializedName("type") val type:String,
                       @SerializedName("dateTime") val dateTime: String,
                       @SerializedName("petId") val petId: String,
                       @SerializedName("petBirthday") val petBirthday:String,
                       @SerializedName("petGender") val petGender: String,
                       @SerializedName("petImage") val petImage: String,
                       @SerializedName("petName") val petName:String,
                       @SerializedName("petSpecies") val petSpecies: String,
                       @SerializedName("petBreed") val petBreed: String,
                       @SerializedName("parentId") val parentId:String,
                       @SerializedName("parentName") val parentName: String,
                       @SerializedName("parentFirstName") val parentFirstName: String,
                       @SerializedName("parentFastName") val parentLastName:String,
                       @SerializedName("parentEmail") val parentEmail: String)