package com.se.fintechadvise.DataClasses

data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var country: String = "",
    var password: String = "",
    var phone: String = "",
    var profilePictureUrl: String = "",
    var fcmToken:String="",
    var age:String="",
    var occupation:String="",
    var income:String="",
    var riskTolerance:String="",

) {
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", "")

    //set income,age, risktolerance, and name

}
