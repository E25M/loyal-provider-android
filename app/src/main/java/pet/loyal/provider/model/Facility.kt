package pet.loyal.provider.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Facility(
    @SerializedName("name") var name: String,
    @SerializedName("displayName") var displayName: String,
    @SerializedName("id") var id: String,
    @SerializedName("status") var status: String,
    @SerializedName("admin") var admin: String?,
    var selected: Boolean = false
) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(displayName)
        parcel.writeString(id)
        parcel.writeString(status)
        parcel.writeString(admin)
        parcel.writeByte(if (selected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Facility> {
        override fun createFromParcel(parcel: Parcel): Facility {
            return Facility(parcel)
        }

        override fun newArray(size: Int): Array<Facility?> {
            return arrayOfNulls(size)
        }
    }

}