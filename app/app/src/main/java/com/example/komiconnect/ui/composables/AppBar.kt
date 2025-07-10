package com.example.komiconnect.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.komiconnect.ui.KomiConnectRoute
import com.example.komiconnect.ui.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    navController: NavController,
    screentitle: String,
    page: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = { AppTopBar(navController, screentitle) },
        bottomBar = { AppBottomBar(navController) },
        content = page
    )
}



@Composable
fun AppBarActionIcon(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    size: Dp = 30.dp
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            modifier = Modifier.size(size)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(navController: NavController, screentitle: String) {

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
        actions = {
            AppBarActionIcon(
                icon = Icons.Filled.Settings,
                description = "Settings",
                onClick = { navController.navigate(KomiConnectRoute.Settings) }
            )

            AppBarActionIcon(
                icon = Icons.Filled.AccountCircle,
                description = "Profile",
                onClick = { navController.navigate(Profile(null)) }
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    )
}


@Composable
fun AppBottomBar(navController: NavController) {

    BottomAppBar(
        modifier = Modifier.height(140.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppBarActionIcon(
                Icons.Filled.Home,
                "Home",
                onClick = { navController.navigate(KomiConnectRoute.Home) }
            )

            AppBarActionIcon(
                Icons.Filled.LocationOn,
                "Maps",
                onClick = { navController.navigate(KomiConnectRoute.Map) }
            )

            FloatingActionButton(
                onClick = { navController.navigate(KomiConnectRoute.Add) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add new post")
            }

            AppBarActionIcon(
                Icons.Filled.Search,
                "Search",
                onClick = { navController.navigate(KomiConnectRoute.Search) }
            )
            AppBarActionIcon(
                Icons.Filled.Bookmark,
                "Favorites",
                onClick = { navController.navigate(KomiConnectRoute.Favorites)}
            )
        }
    }
}


