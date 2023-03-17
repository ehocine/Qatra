package com.helic.qatra.utils

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow

object Constants {

    const val TIMEOUT_IN_MILLIS = 10000L

    var loadingState = MutableStateFlow(LoadingState.IDLE)
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    const val ROOT_ROUTE = "root"
    const val AUTHENTICATION_ROUTE = "authentication_root"
    const val MAIN_SCREEN_ROUTE = "main_screen_root"

    const val FIRESTORE_DATABASE = "data"

    val bloodTypeList = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    const val USERNAME_FIELD = "name"
    const val USER_BIO_FIELD = "about"
    const val USER_IMAGE_FIELD = "picture"
    const val USER_BLOOD_TYPE_FIELD = "bloodType"
    const val USER_AGE_FIELD = "age"
    const val USER_LOCATION_FIELD = "location"
    const val USER_COUNTRY_FIELD = "country"

}