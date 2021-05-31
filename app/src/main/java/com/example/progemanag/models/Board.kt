package com.example.progemanag.models

import android.os.Parcel
import android.os.Parcelable

data class Board (
 val name: String = "",
 val image: String = "",
 val createdBy: String = "",
 val assignedBy: ArrayList<String> = ArrayList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.createStringArrayList()!!

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel){
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(createdBy)
        writeStringList(assignedBy)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}