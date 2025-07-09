package com.example.komiconnect.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.komiconnect.R
import com.example.komiconnect.network.PostResponse
import com.example.komiconnect.ui.KomiConnectRoute
import com.example.komiconnect.ui.Post
import com.example.komiconnect.ui.composables.AppBar
import com.example.komiconnect.ui.composables.PostItem

@Composable
fun HomeScreen(state: HomeState, allPosts: (String) -> Unit, navController: NavController) {
    var selectedTag by remember { mutableStateOf<String?>(null) }
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

    val filteredPosts = remember(state.posts, selectedTag) {
        if (selectedTag == null) {
            state.posts
        } else {
            state.posts.filter { post ->
                post.label == selectedTag
            }
        }
    }


    AppBar(navController, "Home") { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tagOptions) { tag ->
                    val isSelected = selectedTag == tag
                    val customColor = tagColorsMap[tag] ?: MaterialTheme.colorScheme.primary
                    val buttonColors = if (isSelected) {
                        ButtonDefaults.elevatedButtonColors(
                            containerColor = customColor,
                            contentColor = Color.White
                        )
                    } else {
                        ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    ElevatedButton(
                        onClick = {
                            selectedTag = if (isSelected) null else tag
                        },
                        shape = RoundedCornerShape(50),
                        colors = buttonColors,
                        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(text = tag)
                    }
                }
            }

            if (state.posts.isNotEmpty()) {
                if (filteredPosts.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp)
                    ) {
                        items(filteredPosts.reversed()) { item ->
                            PostItem(
                                item,
                                state.imagePosts[item.id],
                                tagColorsMap,
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate(Post(item.id)) }
                            )
                        }
                    }
                }
            } else {
                Text(
                    "Not a single post found..."
                )
            }
        }
    }
}

