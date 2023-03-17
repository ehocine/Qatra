package com.helic.qatra.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.material.SnackbarDuration
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.helic.qatra.R
import com.helic.qatra.data.models.users.User
import com.helic.qatra.data.viewmodels.MainViewModel
import com.helic.qatra.navigation.Screens
import com.helic.qatra.utils.Constants.FIRESTORE_DATABASE
import com.helic.qatra.utils.Constants.TIMEOUT_IN_MILLIS
import com.helic.qatra.utils.Constants.USER_IMAGE_FIELD
import com.helic.qatra.utils.Constants.auth
import com.helic.qatra.utils.Constants.loadingState
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


//Register new user
fun registerNewUser(
    navController: NavController,
    snackbar: (String, SnackbarDuration) -> Unit,
    context: Context,
    userName: String,
    emailAddress: String,
    password: String,
    age: Double,
    bloodType: String,
    location: String,
    country: String
) {
    if (hasInternetConnection(context)) {
        if (emailAddress.isNotEmpty() && password.isNotEmpty() && bloodType.isNotEmpty() && age != 0.0) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withTimeoutOrNull(TIMEOUT_IN_MILLIS) {
                        loadingState.emit(LoadingState.LOADING)
                        auth.createUserWithEmailAndPassword(emailAddress, password).await()
                        loadingState.emit(LoadingState.LOADED)
                        withContext(Dispatchers.Main) {
                            val user = Firebase.auth.currentUser
                            val setUserName = userProfileChangeRequest {
                                displayName = userName
                            }
                            user!!.updateProfile(setUserName).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    user.let {
                                        User(
                                            userID = it.uid,
                                            name = it.displayName!!,
                                            age = age,
                                            bloodType = bloodType,
                                            location = location,
                                            picture = "",
                                            about = "",
                                            email = it.email!!,
                                            country = country
                                        )
                                    }.let { createUser(it) }
                                }
                            }
                            user.sendEmailVerification().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    snackbar(
                                        context.getString(R.string.verification_email_sent),
                                        SnackbarDuration.Short
                                    )
                                }
                            }
                            navController.navigate(Screens.Login.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    } ?: withContext(Dispatchers.Main) {
                        loadingState.emit(LoadingState.ERROR)
                        snackbar(context.getString(R.string.time_out), SnackbarDuration.Short)
                    }

                } catch (e: Exception) {
                    loadingState.emit(LoadingState.ERROR)
                    withContext(Dispatchers.Main) {
                        Log.d("Tag", "Register: ${e.message}")
                    }
                }
            }
        } else {
            snackbar(context.getString(R.string.verify_inputs), SnackbarDuration.Short)
        }
    } else {
        snackbar(context.getString(R.string.device_not_connected), SnackbarDuration.Short)
    }
}

//Sign in existing user
fun signInUser(
    navController: NavController,
    snackbar: (String, SnackbarDuration) -> Unit,
    context: Context,
    emailAddress: String,
    password: String
) {
    if (hasInternetConnection(context)) {
        if (emailAddress.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withTimeoutOrNull(TIMEOUT_IN_MILLIS) {
                        loadingState.emit(LoadingState.LOADING)
                        auth.signInWithEmailAndPassword(emailAddress, password).await()
                        loadingState.emit(LoadingState.LOADED)
                        val user = Firebase.auth.currentUser
                        if (user!!.isEmailVerified) {
                            withContext(Dispatchers.Main) {
                                navController.navigate(Screens.Home.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                snackbar(
                                    context.getString(R.string.email_address_not_verified),
                                    SnackbarDuration.Short
                                )
                            }
                        }
                    } ?: withContext(Dispatchers.Main) {
                        loadingState.emit(LoadingState.ERROR)
                        snackbar(context.getString(R.string.time_out), SnackbarDuration.Short)
                    }
                } catch (e: Exception) {
                    loadingState.emit(LoadingState.ERROR)
                    withContext(Dispatchers.Main) {
                        Log.d("Tag", "Sign in: ${e.message}")
                    }
                }
            }
        } else {
            snackbar(context.getString(R.string.verify_inputs), SnackbarDuration.Short)
        }
    } else {
        snackbar(context.getString(R.string.device_not_connected), SnackbarDuration.Short)
    }
}


// Function to create a new user by getting the ID from the auth system
fun createUser(user: User) {
    val db = Firebase.firestore
//    val newUser = user?.let {
//        User(
//            userID = it.uid,
//            name = it.displayName.toString(),
//            email = it.email.toString(),
//            about = "",
//            picture = "",
//            age = 0.0,
//            bloodType = "",
//            location = "",
//        )
//    }
    db.collection(FIRESTORE_DATABASE).document(user.userID)
        .set(user)
        .addOnCompleteListener { task ->
            Log.d("Tag", "success $task")
        }.addOnFailureListener { task ->
            Log.d("Tag", "Failure $task")
        }
}

//Reset password function
fun resetUserPassword(
    context: Context,
    snackbar: (String, SnackbarDuration) -> Unit,
    emailAddress: String
) {
    if (hasInternetConnection(context)) {
        if (emailAddress.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withTimeoutOrNull(TIMEOUT_IN_MILLIS) {
                        loadingState.emit(LoadingState.LOADING)
                        auth.sendPasswordResetEmail(emailAddress).await()
                        loadingState.emit(LoadingState.LOADED)
                        withContext(Dispatchers.Main) {
                            snackbar(context.getString(R.string.email_sent), SnackbarDuration.Short)
                        }
                    } ?: withContext(Dispatchers.Main) {
                        loadingState.emit(LoadingState.ERROR)
                        snackbar(context.getString(R.string.time_out), SnackbarDuration.Short)
                    }
                } catch (e: Exception) {
                    loadingState.emit(LoadingState.ERROR)
                    withContext(Dispatchers.Main) {
                        Log.d("Tag", "Reset: ${e.message}")
                    }
                }
            }
        } else {
            snackbar(context.getString(R.string.verify_inputs), SnackbarDuration.Short)
        }
    } else {
        snackbar(context.getString(R.string.device_not_connected), SnackbarDuration.Short)
    }
}


fun resendVerificationEmail(
    snackbar: (String, SnackbarDuration) -> Unit,
    context: Context
) {
    val user = auth.currentUser
    if (user != null) {
        if (!user.isEmailVerified) {
            user.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    snackbar(
                        context.getString(R.string.verification_email_sent),
                        SnackbarDuration.Short
                    )
                }
            }.addOnFailureListener {
//                snackbar(
//                    it.message.toString(),
//                    SnackbarDuration.Long
//                )
            }
        } else {
            snackbar(
                context.getString(R.string.email_already_verified),
                SnackbarDuration.Short
            )
        }
    } else {
        snackbar(
            context.getString(R.string.error_occurred),
            SnackbarDuration.Short
        )
    }
}


fun uploadProfilePicture(fileUri: Uri, mainViewModel: MainViewModel) =
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Get current username
            val currentUser = Firebase.auth.currentUser

            // Create a storage reference from our app
            val storageRef = Firebase.storage.reference

            val profileRef =
                currentUser?.let { storageRef.child("${it.uid}/profilePicture/${fileUri.lastPathSegment}") }

            // Upload file
            profileRef?.putFile(fileUri)?.addOnSuccessListener {
                currentUser.let { firebaseUser ->
                    storageRef.child("${firebaseUser.uid}/profilePicture/${mainViewModel.userInfo.value.picture}")
                        .downloadUrl.addOnSuccessListener {
                            Log.d("Tag", "Profile picture: $it")
                            updateProfilePictureUri(ownerImage = it.toString())
                        }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
            }
        }
    }

fun deleteOldProfilePicture(mainViewModel: MainViewModel) {

    val oldFileUri = mainViewModel.userInfo.value.picture

    // Get current username
    val currentUser = Firebase.auth.currentUser

    // Create a storage reference from our app
    val storageRef = Firebase.storage.reference
    currentUser?.let { storageRef.child("${it.uid}/profilePicture/${oldFileUri}") }?.delete()

}

fun updateProfilePictureUri(ownerImage: String) {
    val db = Firebase.firestore
    val currentUser = Firebase.auth.currentUser
    val data = currentUser?.let { db.collection(FIRESTORE_DATABASE).document(it.uid) }
    CoroutineScope(Dispatchers.IO).launch {

        data?.update(USER_IMAGE_FIELD, ownerImage)
            ?.addOnSuccessListener {
            }?.addOnFailureListener {
            }
    }
}

// Function to check is the user is not null and has email verified
fun userLoggedIn(): Boolean {
    val user = Firebase.auth.currentUser
    return user != null && user.isEmailVerified
}