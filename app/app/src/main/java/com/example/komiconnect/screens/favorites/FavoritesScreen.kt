package com.example.komiconnect.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.komiconnect.ui.Post
import com.example.komiconnect.ui.TagConstants
import com.example.komiconnect.ui.composables.AppBar
import com.example.komiconnect.ui.composables.PostItem

@Composable
fun FavoritesScreen(state: FavoritesState, allPosts: (String) -> Unit, navController: NavController) {

    LaunchedEffect(Unit) {
        allPosts(state.token)
    }

    AppBar(navController, "Salvati") { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.posts.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp)
                ) {
                    items(state.posts.reversed()) { item ->
                        PostItem(
                            item,
                            state.imagePosts[item.id],
                            TagConstants.TAG_COLORS_MAP,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Post(item.id)) }
                        )
                    }
                }
            } else {
                Text(text = "Non hai ancora dei preferiti")
            }
        }
    }
}
