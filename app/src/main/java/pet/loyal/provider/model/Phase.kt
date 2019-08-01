package pet.loyal.provider.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Phase(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    var isSelected: Boolean
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString(),
        1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(name)
        writeInt((if (isSelected) 1 else 0))
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Phase> = object : Parcelable.Creator<Phase> {
            override fun createFromParcel(source: Parcel): Phase = Phase(source)
            override fun newArray(size: Int): Array<Phase?> = arrayOfNulls(size)
        }
    }
}
