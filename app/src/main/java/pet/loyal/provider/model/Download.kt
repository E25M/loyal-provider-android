package pet.loyal.provider.model

import android.os.Parcel
import android.os.Parcelable

class Download() : Parcelable {

    var progress: Int = 0

    var currentFileSize: Int = 0

    var totalFileSize: Int = 0

    constructor(source: Parcel) : this()

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {}

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Download> = object : Parcelable.Creator<Download> {
            override fun createFromParcel(source: Parcel): Download = Download(source)
            override fun newArray(size: Int): Array<Download?> = arrayOfNulls(size)
        }
    }
}
