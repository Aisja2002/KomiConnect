package com.example.komiconnect.screens.search

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.komiconnect.R
import com.example.komiconnect.network.ConventionResponse
import com.example.komiconnect.network.UserResponse
import com.example.komiconnect.ui.Convention
import com.example.komiconnect.ui.Profile
import com.example.komiconnect.ui.composables.AppBar
import com.example.komiconnect.ui.composables.SearchCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(state: SearchState,  allUsers: (String) -> Unit, allConventions: (String) -> Unit, navController: NavController) {
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        allUsers(state.token)
        allConventions(state.token)
    }

    val filteredUsers = remember(state.users, searchText) {
        state.users.filter { it.username.contains(searchText, ignoreCase = true) }
    }

    val filteredConventions = remember(state.conventions, searchText) {
        state.conventions.filter {
            it.data.name?.contains(searchText, ignoreCase = true) == true
        }
    }



    AppBar(navController, "Cerca") { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Cerca utenti o fiere") },
                modifier = Modifier.fillMaxWidth(0.9f).padding(16.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cerca"
                    )
                },
                singleLine = true
            )

            LazyColumn {
                items(filteredUsers.toList()) { user ->
                    ProfileView(user = user, state.imageUsers[user.id], navController = navController)
                }

                items(filteredConventions.toList()) { convention ->
                    ConventionView(convention,state.imageConventions[convention.id] , navController)
                }
            }
        }
    }
}

@Composable
fun ProfileView(user: UserResponse, image: Bitmap?, navController: NavController) {
    SearchCard(
        imageBitmap = image,
        fallbackImageRes = R.drawable.square_person,
        title = user.username,
        tag = "Utente",
        onClick = { navController.navigate(Profile(user.id)) }
    )
}

@Composable
fun ConventionView(convention: ConventionResponse, image: Bitmap?, navController: NavController) {
    val conventionName = convention.data.name ?: "Senza nome"
    SearchCard(
        imageBitmap = image,
        fallbackImageRes = R.drawable.square_person,
        title = conventionName,
        tag = "Fiera",
        onClick = { navController.navigate(Convention(convention.id)) }
    )
}
