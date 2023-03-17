package com.helic.qatra.view.screens.main_app

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.helic.qatra.R
import com.helic.qatra.components.Title
import com.helic.qatra.data.LocationDetails
import com.helic.qatra.data.models.users.User
import com.helic.qatra.data.viewmodels.MainViewModel
import com.helic.qatra.navigation.Screens
import com.helic.qatra.ui.theme.BackgroundColor
import com.helic.qatra.ui.theme.Blue
import com.helic.qatra.ui.theme.ButtonTextColor
import com.helic.qatra.ui.theme.TextColor
import com.helic.qatra.utils.BloodTypeMenu
import com.helic.qatra.utils.Constants.bloodTypeList
import com.helic.qatra.utils.deleteOldProfilePicture
import com.helic.qatra.utils.uploadProfilePicture
import java.io.IOException
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditProfileDetails(
    navController: NavController,
    mainViewModel: MainViewModel,
    snackbar: (String, SnackbarDuration) -> Unit,
    location: LocationDetails?
) {
    val context = LocalContext.current

    val geoCoder = Geocoder(context, Locale.getDefault())
    var userLocation: List<Address> = listOf()
    try {
        if (location != null) {
            userLocation =
                geoCoder.getFromLocation(
                    location.latitude.toDouble(),
                    location.longitude.toDouble(),
                    1
                )
        }
    } catch (e: IOException) {

    }

    val user by mainViewModel.userInfo.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                backgroundColor = MaterialTheme.colors.BackgroundColor,
                contentColor = MaterialTheme.colors.TextColor,
                elevation = 0.dp,
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                navController.navigate(Screens.Profile.route) {
                                    popUpTo(navController.graph.findStartDestination().id)
                                    launchSingleTop = true
                                }
                            },
                        tint = MaterialTheme.colors.TextColor
                    )
                }
            )
        },
        content = {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.BackgroundColor) {
                ProfileDetails(
                    user = user,
                    mainViewModel = mainViewModel,
                    navController = navController,
                    context = context,
                    newUserLocation = userLocation,
                    snackbar = snackbar
                )
            }
        }
    )
}

@Composable
fun ProfileDetails(
    user: User?,
    mainViewModel: MainViewModel,
    navController: NavController,
    context: Context,
    snackbar: (String, SnackbarDuration) -> Unit,
    newUserLocation: List<Address>
) {

    var userName by remember { mutableStateOf(user!!.name) }
    var userBio by remember { mutableStateOf(user!!.about) }
    var userBloodType by remember { mutableStateOf(user!!.bloodType) }
    var userAge by remember { mutableStateOf(user!!.age.toString()) }
    var userLocation by remember { mutableStateOf(user!!.location) }
    var userCountry by remember { mutableStateOf(user!!.country) }

    var imageUri by remember {
        mutableStateOf(Uri.parse(user?.picture))
    }
    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            mainViewModel.pictureChanged.value = true
        } else {
            mainViewModel.pictureChanged.value = false
        }

    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.BackgroundColor)
    ) {
        // Basic details
        item {
            Box(Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(16.dp))
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .size(250.dp)
                        .clip(CircleShape)
                        .align(Alignment.TopCenter),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri.toString())
                        .crossfade(true)
                        .error(R.drawable.account)
                        .build(),
                    contentDescription = "User Image"
                ) {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Loading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colors.TextColor)
                        }
                    } else {
                        SubcomposeAsyncImageContent(
                            modifier = Modifier.clip(RectangleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                IconButton(onClick = {
                    launcher.launch("image/*")
                }, Modifier.align(Alignment.BottomEnd)) {
                    Icon(imageVector = Icons.Default.Image, contentDescription = "Profile picture")
                }
            }
        }
        item {
            user.let {
                Spacer(modifier = Modifier.height(24.dp))
                Title(title = "My Name")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = {
                        Text(
                            text = "Change your name",
                            color = MaterialTheme.colors.Blue
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Change your name",
                            color = MaterialTheme.colors.Blue
                        )
                    },
                    maxLines = 1,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 0.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colors.Blue
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Title(title = "Your Age")
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = userAge,
                onValueChange = { userAge = it },
                label = {
                    Text(
                        text = "Your age in years",
                        color = MaterialTheme.colors.Blue
                    )
                },
                placeholder = {
                    Text(
                        text = "Your age in years",
                        color = MaterialTheme.colors.Blue
                    )
                },
                maxLines = 1,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp, 16.dp, 0.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.Blue
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(modifier = Modifier.height(16.dp))

        }

        // My blood type
        item {
            user.let {
                Spacer(modifier = Modifier.height(24.dp))
                Title(title = "My Blood Type")
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 0.dp)
                ) {
                    BloodTypeMenu(
                        label = userBloodType,
                        optionsList = bloodTypeList,
                        onOptionSelected = {
                            userBloodType = it
                        })
                }
            }
        }

        // My story details
        item {
            user.let {
                Spacer(modifier = Modifier.height(24.dp))
                Title(title = "My Story")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = userBio,
                    onValueChange = { userBio = it },
                    label = {
                        Text(
                            text = "Change your bio",
                            color = MaterialTheme.colors.Blue
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Change your bio",
                            color = MaterialTheme.colors.Blue
                        )
                    },
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 0.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colors.Blue
                    )
                )
            }
        }

        //TODO: Add a button to load the location from GPS (a dialog is going to be shown as a loading "Success and error")
        // and a text to show the current location and eventually the new one.

        item {
            Spacer(modifier = Modifier.height(36.dp))
            Button(
                onClick = {
                    // TODO: Load a location from GPS
                    mainViewModel.startLocationUpdate()
                    if (newUserLocation.isNotEmpty()) {
                        userLocation =
                            "${newUserLocation[0].locality}, ${newUserLocation[0].adminArea}, ${newUserLocation[0].countryName}"
                        userCountry = newUserLocation[0].countryName
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(16.dp, 0.dp, 16.dp, 0.dp),
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = MaterialTheme.colors.Blue,
                    contentColor = MaterialTheme.colors.ButtonTextColor
                )
            ) {
                Text("Location")
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = userLocation,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp, 16.dp, 0.dp),
                color = MaterialTheme.colors.TextColor,
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Start
            )
        }

        item {
            Spacer(modifier = Modifier.height(36.dp))
            Button(
                onClick = {
                    imageUri?.let {
                        mainViewModel.updateUserDetails(
                            context = context,
                            userName = userName,
                            userAbout = userBio,
                            userPicture = it.lastPathSegment.toString(),
                            userBloodType = userBloodType,
                            userAge = userAge.toDouble(),
                            userLocation = userLocation,
                            userCountry = userCountry,
                            snackbar = snackbar
                        )
                        //TODO
                        //If the user changed the picture
                        if (mainViewModel.pictureChanged.value) {
                            deleteOldProfilePicture(mainViewModel = mainViewModel)
                            uploadProfilePicture(fileUri = it, mainViewModel = mainViewModel)
                        }
                    }
                    navController.navigate(Screens.Profile.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(16.dp, 0.dp, 16.dp, 0.dp),
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = MaterialTheme.colors.Blue,
                    contentColor = MaterialTheme.colors.ButtonTextColor
                )
            ) {
                Text("Save")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}