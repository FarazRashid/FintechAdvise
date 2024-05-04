package com.se.fintechadvise.DataClasses

import android.os.Parcel
import android.os.Parcelable

data class Transaction(
    val transactionId: String,
    val name: String,
    var transactionCategory: String,
    val transactionAmount: String,
    val transactionDate: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(transactionId)
        parcel.writeString(name)
        parcel.writeString(transactionCategory)
        parcel.writeString(transactionAmount)
        parcel.writeString(transactionDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Transaction> {
        override fun createFromParcel(parcel: Parcel): Transaction {
            return Transaction(parcel)
        }

        override fun newArray(size: Int): Array<Transaction?> {
            return arrayOfNulls(size)
        }
    }
}