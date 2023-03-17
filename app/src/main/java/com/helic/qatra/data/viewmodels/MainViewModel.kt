package com.helic.qatra.data.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.helic.qatra.LocationData
import com.helic.qatra.R
import com.helic.qatra.data.models.users.User
import com.helic.qatra.utils.Constants.FIRESTORE_DATABASE
import com.helic.qatra.utils.Constants.USERNAME_FIELD
import com.helic.qatra.utils.Constants.USER_AGE_FIELD
import com.helic.qatra.utils.Constants.USER_BIO_FIELD
import com.helic.qatra.utils.Constants.USER_BLOOD_TYPE_FIELD
import com.helic.qatra.utils.Constants.USER_COUNTRY_FIELD
import com.helic.qatra.utils.Constants.USER_IMAGE_FIELD
import com.helic.qatra.utils.Constants.USER_LOCATION_FIELD
import com.helic.qatra.utils.FilterOption
import com.helic.qatra.utils.LoadingState
import com.helic.qatra.utils.hasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {


    private val locationData = LocationData(application)
    fun getLocationData() = locationData
    fun startLocationUpdate() {
        locationData.startLocationUpdates()
    }


    var selectedUser: MutableState<User> = mutableStateOf(User())

    @SuppressLint("MutableCollectionMutableState")
    private var _usersList: MutableStateFlow<MutableList<User>> =
        MutableStateFlow(mutableListOf())
    var usersList = _usersList.asStateFlow()

    var gettingListOfUsersState = MutableStateFlow(LoadingState.IDLE)

    private var _userInfo: MutableStateFlow<User> = MutableStateFlow(User())
    var userInfo = _userInfo.asStateFlow()

    var selectedFilterOption: MutableState<FilterOption> =
        mutableStateOf(FilterOption.ALL_COUNTRY)

    fun getUserInfo(context: Context, snackbar: (String, SnackbarDuration) -> Unit) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser
        val data = currentUser?.let { db.collection(FIRESTORE_DATABASE).document(it.uid) }
        if (hasInternetConnection(getApplication<Application>())) {
            if (currentUser != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        data?.addSnapshotListener { value, error ->
                            if (error != null) {
//                                snackbar("Error occurred: $error", SnackbarDuration.Short)
                                return@addSnapshotListener
                            }
                            if (value != null && value.exists()) {
                                _userInfo.value =
                                    value.toObject(User::class.java) ?: User()
                            } else {
                                snackbar(
                                    context.getString(R.string.error_occurred),
                                    SnackbarDuration.Short
                                )
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            snackbar(
                                getApplication<Application>().getString(R.string.error_occurred),
                                SnackbarDuration.Short
                            )
                        }
                    }
                }
            }
        } else {
            snackbar(context.getString(R.string.device_not_connected), SnackbarDuration.Short)
        }
    }

    var pictureChanged: MutableState<Boolean> = mutableStateOf(false)

    fun updateUserDetails(
        context: Context,
        userName: String,
        userAbout: String,
        userPicture: String,
        userBloodType: String,
        userAge: Double,
        userLocation: String,
        userCountry: String,
        snackbar: (String, SnackbarDuration) -> Unit
    ) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser
        val data = currentUser?.let { db.collection(FIRESTORE_DATABASE).document(it.uid) }

        if (hasInternetConnection(getApplication<Application>())) {
            if (currentUser != null) {
                CoroutineScope(Dispatchers.IO).launch {

                    var image = false
                    if (pictureChanged.value) { // the picture was changed
                        data?.update(USER_IMAGE_FIELD, userPicture)
                            ?.addOnSuccessListener {
                                image = true
                            }?.addOnFailureListener {

                                image = false
                            }
                    } else {// the picture was not changed, we proceed anyway
                        image = true
                    }

                    var name = false
                    data?.update(USERNAME_FIELD, userName)
                        ?.addOnSuccessListener {
                            name = true

                        }?.addOnFailureListener {
                            name = false
                        }
                    var about = false
                    data?.update(USER_BIO_FIELD, userAbout)
                        ?.addOnSuccessListener {
                            about = true

                        }?.addOnFailureListener {
                            about = false
                        }

                    var bloodType = false
                    data?.update(USER_BLOOD_TYPE_FIELD, userBloodType)
                        ?.addOnSuccessListener {
                            bloodType = true

                        }?.addOnFailureListener {
                            bloodType = false
                        }

                    var age = false
                    data?.update(USER_AGE_FIELD, userAge)
                        ?.addOnSuccessListener {
                            age = true

                        }?.addOnFailureListener {
                            age = false
                        }

                    var location = false
                    data?.update(USER_LOCATION_FIELD, userLocation)
                        ?.addOnSuccessListener {
                            location = true

                        }?.addOnFailureListener {
                            location = false
                        }
                    var country = false
                    data?.update(USER_COUNTRY_FIELD, userCountry)
                        ?.addOnSuccessListener {
                            country = true

                        }?.addOnFailureListener {
                            country = false
                        }

//                    if (image && name && about && bloodType && age) {
//                        snackbar(
//                            "Details updated successfully",
//                            SnackbarDuration.Short
//                        )
//                    } else {
//                        Log.d("Tag", "image: $image")
//                        Log.d("Tag", "Name: $name")
//                        Log.d("Tag", "about: $about")
//                        Log.d("Tag", "bloodType: $bloodType")
//                        Log.d("Tag", "age: $age")
//                        snackbar(
//                            "Something went wrong",
//                            SnackbarDuration.Short
//                        )
//                    }
                }
            }
        } else {
            snackbar(context.getString(R.string.device_not_connected), SnackbarDuration.Short)
        }
    }

    fun getListOfUsersFromFirebase(
        context: Context,
        snackbar: (String, SnackbarDuration) -> Unit
    ) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser
        val data = db.collection(FIRESTORE_DATABASE)

        if (hasInternetConnection(getApplication<Application>())) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    gettingListOfUsersState.emit(LoadingState.LOADING)
                    val query: Query
                    if (currentUser != null) {
                        query = if (selectedFilterOption.value == FilterOption.ALL_COUNTRY) {
                            data
                        } else {
                            data.whereEqualTo(
                                "country",
                                _userInfo.value.country
                            )
                        }
                        query.whereNotEqualTo("userID", currentUser.uid)
                            .addSnapshotListener { value, error ->
                                if (error != null) {
                                    snackbar("Error occurred: $error", SnackbarDuration.Short)
                                    return@addSnapshotListener
                                }
                                if (value != null) {
                                    _usersList.value = value.toObjects(User::class.java)
                                } else {
                                    snackbar(
                                        context.getString(R.string.error_occurred),
                                        SnackbarDuration.Short
                                    )
                                }
                            }
                    }
                    gettingListOfUsersState.emit(LoadingState.LOADED)
                } catch (e: Exception) {
                    e.printStackTrace()
                    gettingListOfUsersState.emit(LoadingState.ERROR)
                    withContext(Dispatchers.Main) {
                        snackbar(
                            context.getString(R.string.error_occurred),
                            SnackbarDuration.Short
                        )
                    }
                }
            }
        } else {
            snackbar(context.getString(R.string.device_not_connected), SnackbarDuration.Short)
        }
    }
}