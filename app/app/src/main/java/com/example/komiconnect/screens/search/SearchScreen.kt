package com.example.komiconnect.screens.search

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.komiconnect.R
import com.example.komiconnect.network.ConventionResponse
import com.example.komiconnect.network.UserResponse
import com.example.komiconnect.ui.Convention
import com.example.komiconnect.ui.Profile
import com.example.komiconnect.ui.composables.AppBar
import java.nio.file.WatchEvent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(state: SearchState,  allUsers: (String) -> Unit, allConventions: (String) -> Unit, navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    val users = state.users
    val conventions = state.conventions

    LaunchedEffect(Unit) {
        allUsers(state.token)
        allConventions(state.token)
    }

    val filteredUsers = remember(state.users, searchText) {
        if (searchText == "") {
            state.users
        } else {
            state.users.filter { user ->
                user.username.lowercase().contains(searchText.lowercase())
            }
        }
    }

    val filteredConventions = remember(state.conventions, searchText) {
        if (searchText == "") {
            state.conventions
        } else {
            state.conventions.filter { c ->
                if (c.data.name != null) {
                    c.data.name.lowercase().contains(searchText.lowercase())
                } else {
                    false
                }
            }
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

            LazyColumn() {
                items(filteredUsers.toList()) { user ->
                    profileView(user = user, state.imageUsers[user.id], navController = navController)
                }

                items(filteredConventions.toList()) { convention ->
                    conventionView(convention,state.imageConventions[convention.id] , navController)
                }
            }
        }
    }
}

                @Composable
                fun profileView (user: UserResponse, image:Bitmap?, navController: NavController){
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        shape = RoundedCornerShape(
                            topStart = 15.dp,
                            topEnd = 15.dp,
                            bottomStart = 15.dp,
                            bottomEnd = 15.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                        modifier = Modifier.padding(16.dp).fillMaxWidth(0.90f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            if (image != null) {
                                Image(
                                    bitmap = image.asImageBitmap(),
                                    contentDescription = "Convention Profile Picture",
                                    modifier = Modifier.height(35.dp).padding(start = 16.dp).clip(CircleShape),
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.square_person),
                                    contentDescription = "Generic Square Image",
                                    modifier = Modifier.height(35.dp).padding(start = 16.dp).clip(CircleShape),
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = user.username,
                                modifier = Modifier.clickable {
                                    navController.navigate(Profile(user.id))
                                }
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "Utente",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                    }
                }

@Composable
fun conventionView (convention: ConventionResponse, image: Bitmap?, navController: NavController){
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    shape = RoundedCornerShape(
                        topStart = 15.dp,
                        topEnd = 15.dp,
                        bottomStart = 15.dp,
                        bottomEnd = 15.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    modifier = Modifier.padding(16.dp).fillMaxWidth(0.90f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        if (image != null) {
                            Image(
                                bitmap = image.asImageBitmap(),
                                contentDescription = "Convention Profile Picture",
                                modifier = Modifier.height(35.dp).padding(start = 16.dp).clip(CircleShape),
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.square_person),
                                contentDescription = "Generic Square Image",
                                modifier = Modifier.height(35.dp).padding(start = 16.dp).clip(CircleShape),
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = convention.data.name.toString(),
                            modifier = Modifier.clickable {
                                navController.navigate(Convention(convention.id))
                            }
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Fiera",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }
            }