package com.example.komiconnect.screens.home

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.komiconnect.ui.Post
import com.example.komiconnect.ui.TagConstants
import com.example.komiconnect.ui.composables.AppBar
import com.example.komiconnect.ui.composables.LabelSelector
import com.example.komiconnect.ui.composables.PostItem

@Composable
fun HomeScreen(state: HomeState, allPosts: (String) -> Unit, navController: NavController) {
    var selectedTag by remember { mutableStateOf<String?>(null) }
    val tagOptions = TagConstants.TAG_OPTIONS
    val tagColorsMap = TagConstants.TAG_COLORS_MAP


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
            LabelSelector(
                selectedTag,
                onTagSelected = { selectedTag = it },
                tagOptions = tagOptions,
                tagColorsMap = tagColorsMap
            )

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
                    "Qui non c'è ancora nulla"
                )
            }
        }
    }
}

