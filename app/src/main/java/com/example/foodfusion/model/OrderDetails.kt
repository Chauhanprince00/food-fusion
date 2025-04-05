package com.example.foodfusion.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class OrderDetails ():Serializable{
    var userUid:String? = null
    var userName:String? = null
    var foodNames:MutableList<String>? = null
    var foodImages:MutableList<String>? = null
    var foodPrices:MutableList<String>? = null
    var foodQuentities:MutableList<Int>? = null
    var address:String? = null
    var totalPrice:String? = null
    var phoneNumber:String? = null
    var orderAccepted:Boolean = false
    var dispatched:Boolean = false
    var Delivered:Boolean = false
    var itemPushKey:String? = null
    var currentTime:Long = 0

    constructor(parcel: Parcel) : this() {
        userUid = parcel.readString()
        userName = parcel.readString()
        address = parcel.readString()
        totalPrice = parcel.readString()
        phoneNumber = parcel.readString()
        orderAccepted = parcel.readByte() != 0.toByte()
        dispatched = parcel.readByte() != 0.toByte()
        Delivered = parcel.readByte() != 0.toByte()
        itemPushKey = parcel.readString()
        currentTime = parcel.readLong()
    }

    constructor(
        userID: String,
        name: String,
        foodItemName: ArrayList<String>,
        foodItemprice: ArrayList<String>,
        foodItemImage: ArrayList<String>,
        quentity: ArrayList<Int>,
        address: String,
        totalamount:String,
        phone: String,
        time: Long,
        itempushkey: String?,
        b: Boolean,
        b1: Boolean,
        b3:Boolean
    ) : this(){
        this.userUid = userID
        this.userName = name
        this.foodNames = foodItemName
        this.foodPrices = foodItemprice
        this.foodImages = foodItemImage
        this.foodQuentities = quentity
        this.address = address
        this.totalPrice = totalamount
        this.phoneNumber = phone
        this.currentTime = time
        this.itemPushKey = itempushkey
        this.orderAccepted =orderAccepted
        this.dispatched = dispatched
        this.Delivered = Delivered


    }

     fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userUid)
        parcel.writeString(userName)
        parcel.writeString(address)
        parcel.writeString(totalPrice)
        parcel.writeString(phoneNumber)
        parcel.writeByte(if (orderAccepted) 1 else 0)
        parcel.writeByte(if (dispatched) 1 else 0)
        parcel.writeByte(if (Delivered) 1 else 0)
        parcel.writeString(itemPushKey)
        parcel.writeLong(currentTime)
    }

   fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderDetails> {
        override fun createFromParcel(parcel: Parcel): OrderDetails {
            return OrderDetails(parcel)
        }

        override fun newArray(size: Int): Array<OrderDetails?> {
            return arrayOfNulls(size)
        }
    }
}