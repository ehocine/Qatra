package com.helic.qatra.view.screens.main_app

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.helic.qatra.R
import com.helic.qatra.components.InfoCard
import com.helic.qatra.components.Title
import com.helic.qatra.components.UserInfoCard
import com.helic.qatra.data.models.users.User
import com.helic.qatra.data.viewmodels.MainViewModel
import com.helic.qatra.ui.theme.BackgroundColor
import com.helic.qatra.ui.theme.Blue
import com.helic.qatra.ui.theme.ButtonTextColor
import com.helic.qatra.ui.theme.TextColor
import com.helic.qatra.utils.sendEmailToUser


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Details(
    navController: NavController,
    mainViewModel: MainViewModel,
    snackbar: (String, SnackbarDuration) -> Unit
) {
    val selectedUser by mainViewModel.selectedUser
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                backgroundColor = MaterialTheme.colors.BackgroundColor,
                contentColor = MaterialTheme.colors.TextColor,
                elevation = 0.dp,
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp, 24.dp)
                            .clickable {
                                navController.navigateUp()
                            },
                        tint = MaterialTheme.colors.TextColor
                    )
                }
            )
        },
        content = {
            DetailsView(context = context, user = selectedUser, snackbar = snackbar)
        }

    )
}

@Composable
fun DetailsView(context: Context, user: User, snackbar: (String, SnackbarDuration) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.BackgroundColor)
    ) {

        // Animal Image
        item {
            user.let {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RectangleShape)
                        .height(250.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.picture)
                        .crossfade(true)
                        .build(),
                    contentDescription = "User Image"
                ) {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
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
                Spacer(modifier = Modifier.height(16.dp))
                UserInfoCard(it.name, it.bloodType, it.location)
            }
        }

        // User about
        item {
            user.let {

                Spacer(modifier = Modifier.height(24.dp))
                Title(title = "About ".plus(user.name))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it.about,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 0.dp),
                    color = colorResource(id = R.color.text),
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Start
                )
            }
        }

        // Quick info
        item {
            user.let {

                Spacer(modifier = Modifier.height(24.dp))
                Title(title = "Quick Info")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 0.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    InfoCard(title = "Age", value = it.age.toString().plus(" yrs"))
                    InfoCard(title = "Blood Type", value = it.bloodType.toString())
                }
            }
        }
        // CTA - Contact me button
        item {
            Spacer(modifier = Modifier.height(36.dp))
            Button(
                onClick = {
                    sendEmailToUser(
                        context = context,
                        emailAddress = user.email,
                        subject = "Seeking help",
                        message = "Hey ${user.name}, I would like to ask for your help, can we get in touch?",
                        snackbar = snackbar
                    )
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
                Text("Contact me")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


