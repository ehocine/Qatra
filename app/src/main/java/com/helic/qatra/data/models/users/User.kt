package com.helic.qatra.data.models.users

data class User(
    var userID: String = "",
    var name: String = "",
    var age: Double = 0.0,
    var bloodType: String = "",
    var location: String = "",
    var picture: String = "",
    var about: String = "",
    val email: String = "",
    val country: String = ""
)