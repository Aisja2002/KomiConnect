package com.example.komiconnect.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.komiconnect.R
import com.example.komiconnect.ui.KomiConnectRoute
import com.example.komiconnect.ui.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navController: NavController, screentitle: String, page :  @Composable ((PaddingValues) -> Unit)) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = screentitle,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.SansSerif
                        )
                    )
                },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Go Back")
                        }
                    }
                },
                /*
                navigationIcon = {

                    IconButton(onClick = { navController.navigate(KomiConnectRoute.Home) },
                        modifier = Modifier.size(100.dp).padding(start = 16.dp)
                    ) {
                    Image(
                        painter = painterResource(id = R.drawable.komilogo),
                        contentDescription = "KomiConnect Logo",
                        contentScale = ContentScale.Fit
                    )


                }
                    IconButton(onClick = {navController.popBackStack() },
                        modifier = Modifier.padding(start = 16.dp).size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                */

                actions = {
                    IconButton(onClick = { navController.navigate(KomiConnectRoute.Settings) }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    IconButton(onClick = { navController.navigate(Profile(null))}) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(140.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                actions = {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.navigate(KomiConnectRoute.Home) },) {
                            Icon(
                                Icons.Filled.Home,
                                contentDescription = "Home",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        IconButton(onClick = { navController.navigate(KomiConnectRoute.Map) }) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = "Maps",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        FloatingActionButton(
                            onClick = { navController.navigate(KomiConnectRoute.Add) },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add new post")
                        }
                        IconButton(onClick = {navController.navigate(KomiConnectRoute.Search)}) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "Search",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        IconButton(onClick = { navController.navigate(KomiConnectRoute.Favorites) }) {
                            Icon(
                                Icons.Filled.Bookmark,
                                contentDescription = "Favorites",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            )
        },
        content = page
    )
}
