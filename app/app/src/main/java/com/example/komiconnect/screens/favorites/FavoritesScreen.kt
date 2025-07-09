package com.example.komiconnect.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.komiconnect.screens.home.HomeState
import com.example.komiconnect.ui.Post
import com.example.komiconnect.ui.composables.AppBar
import com.example.komiconnect.ui.composables.PostItem


@Composable
fun FavoritesScreen(state: FavoritesState, allPosts: (String) -> Unit, navController: NavController) {
    val tagEvento = "Eventi"
    val tagAcquisti = "Acquisti"
    val tagCibo = "Cibo"
    val tagPersone = "Persone"
    val tagOptions = listOf("Eventi", "Cibo", "Persone", "Acquisti")
    val tagColorsMap = mapOf(
        tagEvento to Color(0xFF2196F3),
        tagAcquisti to Color(0xFF4CAF50),
        tagCibo to Color(0xFFFF9800),
        tagPersone to Color(0xFFE91E63)
    )


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
                                    tagColorsMap,
                                    modifier = Modifier.weight(1f),
                                    onClick = { navController.navigate(Post(item.id)) }
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Not a single post found..."
                        )
                    }
                }
            }
        }
