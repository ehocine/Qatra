package com.helic.qatra.view.screens.signin_signup

import android.location.Address
import android.location.Geocoder
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.helic.qatra.R
import com.helic.qatra.data.LocationDetails
import com.helic.qatra.data.viewmodels.MainViewModel
import com.helic.qatra.navigation.Screens
import com.helic.qatra.ui.theme.BackgroundColor
import com.helic.qatra.ui.theme.Blue
import com.helic.qatra.ui.theme.TextColor
import com.helic.qatra.utils.BloodTypeMenu
import com.helic.qatra.utils.Constants.bloodTypeList
import com.helic.qatra.utils.Constants.loadingState
import com.helic.qatra.utils.LoadingState
import com.helic.qatra.utils.registerNewUser
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.IOException
import java.util.*

@Composable
fun RegisterPage(
    navController: NavController,
    mainViewModel: MainViewModel,
    showSnackbar: (String, SnackbarDuration) -> Unit,
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

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    var nameValue by remember { mutableStateOf("") }
    var emailValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var ageValue by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("") }

    loadingState = MutableStateFlow(LoadingState.IDLE)
    val state by loadingState.collectAsState()

    var passwordVisibility by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.clickable { focusManager.clearFocus() },
        color = MaterialTheme.colors.BackgroundColor
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.80f)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .padding(16.dp, 0.dp, 16.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(state = scrollState),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.sign_up),
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.h4.fontSize
                    )
                    Spacer(modifier = Modifier.padding(20.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedTextField(
                            value = nameValue,
                            onValueChange = { nameValue = it },
                            label = {
                                Text(
                                    text = stringResource(R.string.name),
                                    color = MaterialTheme.colors.Blue
                                )
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.name),
                                    color = MaterialTheme.colors.Blue
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = MaterialTheme.colors.Blue)
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        OutlinedTextField(
                            value = emailValue,
                            onValueChange = { emailValue = it },
                            label = {
                                Text(
                                    text = stringResource(R.string.email_address),
                                    color = MaterialTheme.colors.Blue
                                )
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.email_address),
                                    color = MaterialTheme.colors.Blue
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = MaterialTheme.colors.Blue)
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        OutlinedTextField(
                            value = passwordValue,
                            onValueChange = { passwordValue = it },
                            label = {
                                Text(
                                    text = stringResource(R.string.password),
                                    color = MaterialTheme.colors.Blue
                                )
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.password),
                                    color = MaterialTheme.colors.Blue
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = MaterialTheme.colors.Blue),
                            trailingIcon = {
                                IconButton(onClick = {
                                    passwordVisibility = !passwordVisibility
                                }) {
                                    Icon(
                                        if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Password Eye",
                                        tint = MaterialTheme.colors.Blue
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisibility) VisualTransformation.None
                            else PasswordVisualTransformation()
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        OutlinedTextField(
                            value = ageValue,
                            onValueChange = { ageValue = it },
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
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = MaterialTheme.colors.Blue),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        BloodTypeMenu(
                            label = "Your blood type",
                            optionsList = bloodTypeList,
                            onOptionSelected = {
                                bloodType = it
                            })

                        Spacer(modifier = Modifier.padding(10.dp))
                        Button(
                            onClick = {
                                if (userLocation.isEmpty()) {
                                    registerNewUser(
                                        navController = navController,
                                        snackbar = showSnackbar,
                                        context = context,
                                        userName = nameValue,
                                        emailAddress = emailValue,
                                        password = passwordValue,
                                        age = ageValue.toDouble(),
                                        bloodType = bloodType,
                                        location = "",
                                        country = ""
                                    )
                                } else {
                                    registerNewUser(
                                        navController = navController,
                                        snackbar = showSnackbar,
                                        context = context,
                                        userName = nameValue,
                                        emailAddress = emailValue,
                                        password = passwordValue,
                                        age = ageValue.toDouble(),
                                        bloodType = bloodType,
                                        location = "${userLocation[0].locality}, ${userLocation[0].adminArea}, ${userLocation[0].countryName}",
                                        country = userLocation[0].countryName
                                    )
                                }
                            },
                            enabled = state != LoadingState.LOADING,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(50.dp),
                            colors = ButtonDefaults.textButtonColors(
                                backgroundColor = MaterialTheme.colors.Blue,
                                contentColor = Color.White
                            )
                        ) {
                            if (state == LoadingState.LOADING) {
                                CircularProgressIndicator(color = MaterialTheme.colors.TextColor)
                            } else {
                                Text(
                                    text = stringResource(R.string.sign_up),
                                    fontSize = 20.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.padding(15.dp))
                        Row {
                            Text(
                                text = stringResource(R.string.have_an_account_login),
                                fontSize = MaterialTheme.typography.subtitle1.fontSize
                            )
                            Spacer(modifier = Modifier.padding(end = 2.dp))
                            Text(
                                text = "Login",
                                fontSize = MaterialTheme.typography.subtitle1.fontSize,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    navController.navigate(route = Screens.Login.route) {
                                        // popUpTo = navController.graph.startDestination
                                        launchSingleTop = true
                                    }
                                })
                        }
                        Spacer(modifier = Modifier.padding(20.dp))
                    }
                }
            }
        }
    }
}
