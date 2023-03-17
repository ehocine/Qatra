package com.helic.qatra.view.screens.main_app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.helic.qatra.components.ItemUserCard
import com.helic.qatra.components.TopBar
import com.helic.qatra.data.viewmodels.MainViewModel
import com.helic.qatra.navigation.Screens
import com.helic.qatra.ui.theme.BackgroundColor
import com.helic.qatra.utils.ErrorLoadingResults
import com.helic.qatra.utils.LoadingList
import com.helic.qatra.utils.LoadingState
import com.helic.qatra.utils.NoResults

@Composable
fun Home(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    snackbar: (String, SnackbarDuration) -> Unit,
) {
    val context = LocalContext.current

    val usersList by mainViewModel.usersList.collectAsState()

    val state by mainViewModel.gettingListOfUsersState.collectAsState()

    LaunchedEffect(key1 = usersList, key2 = mainViewModel.selectedFilterOption) {
        mainViewModel.getListOfUsersFromFirebase(context = context, snackbar = snackbar)
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.BackgroundColor) {
        Column(Modifier.fillMaxSize()) {
            TopBar(
                context = context,
                navController = navController,
                mainViewModel = mainViewModel,
                snackbar = snackbar
            )
            Spacer(modifier = Modifier.height(8.dp))
            when (state) {
                LoadingState.LOADING -> LoadingList()
                LoadingState.ERROR -> ErrorLoadingResults()
                else -> {
                    if (usersList.isEmpty()) {
                        NoResults()
                    } else {
                        LazyColumn {
                            items(usersList) { user ->
                                ItemUserCard(
                                    user = user,
                                    onItemClicked = {
                                        mainViewModel.selectedUser.value = it
                                        navController.navigate(Screens.Details.route)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}