package com.helic.qatra.components

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.helic.qatra.data.viewmodels.MainViewModel
import com.helic.qatra.navigation.Screens
import com.helic.qatra.ui.theme.BackgroundColor
import com.helic.qatra.ui.theme.TextColor
import com.helic.qatra.utils.FilterOption


@Composable
fun TopBar(
    context: Context,
    navController: NavController,
    mainViewModel: MainViewModel,
    snackbar: (String, SnackbarDuration) -> Unit
) {
    LaunchedEffect(key1 = true) {
        mainViewModel.getUserInfo(context = context, snackbar = snackbar)
    }

    val user by mainViewModel.userInfo.collectAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Hey ${user.name},",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.TextColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Find who can help you!",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.TextColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            FilterButton(mainViewModel = mainViewModel, context = context, snackbar = snackbar)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when (mainViewModel.selectedFilterOption.value) {
                    FilterOption.ALL_COUNTRY -> "Country filter set to : All countries"
                    FilterOption.SPECIFIED_COUNTRY -> "Country filter set to : ${user.country}"
                },
                fontSize = 13.sp,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colors.TextColor
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 24.dp, 36.dp, 0.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Icons",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = {
                        navController.navigate(Screens.Profile.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }),
                tint = MaterialTheme.colors.TextColor
            )
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FilterButton(
    mainViewModel: MainViewModel,
    context: Context,
    snackbar: (String, SnackbarDuration) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Icon(
        imageVector = Icons.Default.Tune,
        contentDescription = "Filter Icon",
        modifier = Modifier
            .size(24.dp)
            .clickable(onClick = {
                expanded = true
            }),
        tint = MaterialTheme.colors.TextColor
    )
    DropdownMenu(
        modifier = Modifier.background(MaterialTheme.colors.BackgroundColor),
        expanded = expanded,
        onDismissRequest = { expanded = false }) {
        DropdownMenuItem(onClick = {
            expanded = false
            mainViewModel.selectedFilterOption.value = FilterOption.ALL_COUNTRY
            mainViewModel.getListOfUsersFromFirebase(context = context, snackbar = snackbar)
        }) {
            Row(Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = "All country",
                    tint = MaterialTheme.colors.TextColor
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Text(
                    text = "All country",
                    modifier = Modifier.padding(start = 5.dp),
                    color = MaterialTheme.colors.TextColor
                )
            }
        }
        DropdownMenuItem(onClick = {
            expanded = false
            mainViewModel.selectedFilterOption.value = FilterOption.SPECIFIED_COUNTRY
            mainViewModel.getListOfUsersFromFirebase(context = context, snackbar = snackbar)
        }) {
            Row(Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = "User country",
                    tint = MaterialTheme.colors.TextColor
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Text(
                    text = mainViewModel.userInfo.value.country,
                    modifier = Modifier.padding(start = 5.dp),
                    color = MaterialTheme.colors.TextColor
                )
            }
        }
    }
}
