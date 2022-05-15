package com.example.bikegarage.model

import com.google.firebase.database.Exclude
import java.io.Serializable

data class AddBikeModel(
    var bikeName:String? = "",
    var userName:String? = "",
    var userFather:String? = "",
    var userAddress:String? = "",
    var district:String? = "",
    var mobileNo:String? = "",
    var rcNo:String? = "",
    var guaranteeUser:String? = "",
    var guaranteeUserAddress:String? = "",
    var guaranteeUserDistrict:String? = "",
    var guaranteeUserMobile:String? = "",
    var bikeprice:Double? = 0.0,
    var remainingprice:Double? = 0.0,
    var userpayAmountprice:Double? = 0.0,
    @Exclude
    var id:String? = "",
    var imageurl:String? = "",
    var chNo:String? = "",
    var engineNo:String? = "",
    var date:String? = "",

) :Serializable{
}