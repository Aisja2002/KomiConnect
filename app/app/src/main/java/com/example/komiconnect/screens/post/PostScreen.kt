package com.example.komiconnect.screens.post

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.komiconnect.R
import com.example.komiconnect.network.ConventionResponse
import com.example.komiconnect.network.PostResponse
import com.example.komiconnect.network.UserResponse
import com.example.komiconnect.ui.Convention
import com.example.komiconnect.ui.KomiConnectRoute
import com.example.komiconnect.ui.Profile
import com.example.komiconnect.ui.TagConstants
import com.example.komiconnect.ui.composables.AppBar
import com.example.komiconnect.ui.tokenToID
import kotlinx.coroutines.CompletableDeferred

@Composable
fun PostScreen(
    postID: Int?,
    state: PostState,
    image: Bitmap?, profileImage: Bitmap?,
    postResponse: PostResponse?, error: String?,
    userResponse: UserResponse?, conventionResponse: ConventionResponse?,
    deletePost: (String, Int?) -> Unit,
    fetchPost: (String, Int?) -> Unit,
    fetchUser: (String, Int?) -> Unit,
    fetchConvention: (String, Int?) -> Unit,
    fetchFavorites: (String, CompletableDeferred<Unit>?) -> Unit,
    fetchLikes: (String, Int?, CompletableDeferred<Unit>?) -> Unit,
    addFavorite: (String, Int?, CompletableDeferred<Unit>?) -> Unit,
    deleteFavorite: (String, Int?, CompletableDeferred<Unit>?) -> Unit,
    addLike: (String, Int?, CompletableDeferred<Unit>?) -> Unit,
    deleteLike: (String, Int?, CompletableDeferred<Unit>?) -> Unit,
    navController: NavController
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(state.token, postID) {
        fetchPost(state.token, postID)
        fetchFavorites(state.token, null)
        fetchLikes(state.token, postID, null)
    }

    LaunchedEffect(postResponse) {
        if (postResponse != null) {
            fetchUser(state.token, postResponse.user)
            fetchConvention(state.token, postResponse.convention)
        }
    }

    val myID = tokenToID(state.token)
    val tagColor = TagConstants.TAG_COLORS_MAP[postResponse?.label ?: ""] ?: Color.Gray

    val isFavorite = remember(state.favorites, postID) {
        postID != null && postID in state.favorites
    }

    val isLiked = remember(state.likes, postID) {
        postID != null && myID in state.likes
    }

    val numLikes = state.likes.size

    AppBar(navController, "Post") { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(6.dp),
                shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.padding(16.dp)
            ) {
                when {
                    postResponse != null -> {
                        Column(modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth()) {
                            PostHeader(
                                profileImage, userResponse, conventionResponse,
                                myID, postResponse, navController
                            ) {
                                deletePost(state.token, postID)
                                navController.navigate(KomiConnectRoute.Home)
                            }

                            PostImage(image, postResponse.label, tagColor)

                            Text(
                                text = postResponse.data.title ?: "Nessun titolo",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )

                            Text(
                                text = postResponse.data.description?.trim().orEmpty(),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Justify,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )

                            PostActions(
                                isLiked = isLiked,
                                isFavorite = isFavorite,
                                tagColor = tagColor,
                                numLikes = numLikes,
                                onLike = {
                                    val d = CompletableDeferred<Unit>()
                                    addLike(state.token, postResponse.id, d)
                                    fetchLikes(state.token, postResponse.id, d)
                                },
                                onUnlike = {
                                    val d = CompletableDeferred<Unit>()
                                    deleteLike(state.token, postResponse.id, d)
                                    fetchLikes(state.token, postResponse.id, d)
                                },
                                onFav = {
                                    val d = CompletableDeferred<Unit>()
                                    addFavorite(state.token, postResponse.id, d)
                                    fetchFavorites(state.token, d)
                                },
                                onUnfav = {
                                    val d = CompletableDeferred<Unit>()
                                    deleteFavorite(state.token, postResponse.id, d)
                                    fetchFavorites(state.token, d)
                                }
                            )
                        }
                    }

                    error != null -> {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = "Post cannot be fetched: $error",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PostHeader(
    profileImage: Bitmap?,
    userResponse: UserResponse?,
    conventionResponse: ConventionResponse?,
    myID: Int?,
    postResponse: PostResponse,
    navController: NavController,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (profileImage != null) {
            Image(
                bitmap = profileImage.asImageBitmap(),
                contentDescription = "User profile",
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(35.dp)
                    .clip(CircleShape)


            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.square_person),
                contentDescription = "Generic user image",
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(35.dp)
                    .clip(CircleShape)

            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = userResponse?.username ?: "Utente sconosciuto",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate(Profile(postResponse.user))
                }
            )
            Text(
                text = conventionResponse?.data?.name ?: "Fiera sconosciuta",
                modifier = Modifier.clickable {
                    navController.navigate(Convention(postResponse.convention))
                }
            )
        }

        IconButton(onClick = {
            if (myID == postResponse.user) onDelete()
        }) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete",
                modifier = Modifier.size(30.dp),
                tint = if (myID == postResponse.user) LocalContentColor.current else Color.Transparent
            )
        }
    }
}

@Composable
fun PostImage(image: Bitmap?, label: String, tagColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        if (image != null) {
            Image(
                bitmap = image.asImageBitmap(),
                contentDescription = "Post Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.image_placeholder),
                contentDescription = "Placeholder",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        ElevatedButton(
            onClick = { },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = tagColor,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp),
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        ) {
            Text(label)
        }
    }
}

@Composable
fun PostActions(
    isLiked: Boolean,
    isFavorite: Boolean,
    tagColor: Color,
    numLikes: Int,
    onLike: () -> Unit,
    onUnlike: () -> Unit,
    onFav: () -> Unit,
    onUnfav: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            IconButton(onClick = { if (!isLiked) onLike() else onUnlike() }) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isLiked) "Dislike Post" else "Like Post",
                    tint = tagColor,
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(text = numLikes.toString())
        }

        IconButton(onClick = { if (!isFavorite) onFav() else onUnfav() }) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = if (isFavorite) "Unbookmark Post" else "Bookmark Post",
                tint = tagColor,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}


