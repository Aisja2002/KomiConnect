package com.example.komiconnect.post

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.komiconnect.network.ApiService
import com.example.komiconnect.network.ConventionResponse
import com.example.komiconnect.network.PostResponse
import com.example.komiconnect.network.UserResponse
import com.example.komiconnect.ui.Convention
import com.example.komiconnect.ui.KomiConnectNavGraph
import com.example.komiconnect.ui.KomiConnectRoute
import com.example.komiconnect.ui.Profile
import com.example.komiconnect.ui.TokenToID
import com.example.komiconnect.ui.composables.AppBar
import com.example.komiconnect.ui.composables.ZoomableImage
import kotlinx.coroutines.CompletableDeferred

@Composable
fun PostScreen(
    postID: Int?,
    state: PostState,
    image: Bitmap?, profileImage: Bitmap?,
    postResponse: PostResponse?, error: String?,
    userResponse: UserResponse?, userError: String?,
    conventionResponse: ConventionResponse?, conventionError: String?,
    deletePost: (String, Int?) -> Unit,
    fetchPost: (String, Int?) -> Unit,
    fetchUser: (String, Int?) -> Unit,
    fetchConvention: (String, Int?) -> Unit,
    fetchFavorites: (String, CompletableDeferred<Unit>?) -> Unit,
    fetchLikes: (String, Int?, CompletableDeferred<Unit>?) -> Unit,
    addFavorite: (String, Int?, CompletableDeferred<Unit>?) -> Unit,
    deleteFavorite: (String, Int?, CompletableDeferred<Unit>?) -> Unit,
    addLike: (String, Int?, CompletableDeferred<Unit>?) -> Unit,
    deleteLike: (String, Int?,CompletableDeferred<Unit>?) -> Unit,
    navController: NavController
) {


    val tagColorsMap = mapOf(
        "Eventi" to Color(0xFF2196F3),
        "Acquisti" to Color(0xFF4CAF50),
        "Cibo" to Color(0xFFFF9800),
        "Persone" to Color(0xFFE91E63)
    )

    var scrollState = rememberScrollState()


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


    val myID = TokenToID(state.token)
    val tagColor = tagColorsMap[postResponse?.label ?: ""] ?: Color.Gray

    var allFavorites = state.favorites
    var isFavorite = remember(allFavorites, postID) {
        postID != null && postID in allFavorites
    }

    var allLikes = state.likes
    var isLiked = remember(allLikes, postID) {
        postID != null && myID in allLikes
    }
    var numLikes = allLikes.size

    AppBar(navController, "Post") { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxWidth().verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                shape = RoundedCornerShape(
                    topStart = 15.dp,
                    topEnd = 15.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                modifier = Modifier.padding(16.dp)
            ) {
                when {
                    postResponse != null -> {
                        Column(
                            modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth()
                        ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {

                                        if (profileImage != null) {
                                            Image(
                                                bitmap = profileImage.asImageBitmap(),
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
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = userResponse?.username ?: "Username non trovato",
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.clickable{
                                                    navController.navigate(Profile(postResponse.user))
                                                }
                                            )
                                            Text(
                                                text = conventionResponse?.data?.name.toString(),
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.clickable{
                                                    navController.navigate(Convention(postResponse.convention))
                                                }
                                            )
                                        }
                                        if (myID == postResponse.user) {
                                            IconButton(onClick = {
                                                deletePost(state.token, postID)
                                                navController.navigate(KomiConnectRoute.Home)
                                            }) {
                                                Icon(
                                                    Icons.Filled.Delete,
                                                    contentDescription = "Delete",
                                                    modifier = Modifier.size(30.dp),
                                                )
                                            }
                                        } else {

                                            IconButton(onClick = { }) {
                                                Icon(
                                                    Icons.Filled.Delete,
                                                    contentDescription = "Delete",
                                                    modifier = Modifier.size(30.dp).alpha(0f),
                                                )
                                            }
                                        }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(7f/7f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                if (image != null) {
                                    Image(
                                        bitmap = image.asImageBitmap(),
                                            contentDescription = "Post Picture",
                                            modifier = Modifier.fillMaxWidth().aspectRatio(7f / 7f)
                                                .padding(16.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.image_placeholder),
                                        contentDescription = "Generic Post Picture",
                                        modifier = Modifier.fillMaxWidth().aspectRatio(7f / 7f)
                                            .padding(16.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }


                                ElevatedButton(
                                    onClick = {
                                    },
                                    shape = RoundedCornerShape(50),
                                    colors =
                                        ButtonDefaults.elevatedButtonColors(
                                            containerColor = tagColor,
                                            contentColor = Color.White
                                        ),
                                    elevation = ButtonDefaults.elevatedButtonElevation(
                                        defaultElevation = 2.dp
                                    ),
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                        .padding(24.dp)
                                ) {
                                    Text(postResponse.label)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = postResponse.data.title ?: "Nessun titolo",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                )
                            }
                            Text(
                                text = postResponse.data.description?.trim().toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Justify,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 16.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    if (!isLiked) {
                                        IconButton(onClick = {
                                            val completableDeferred = CompletableDeferred<Unit>()
                                            addLike(state.token, postResponse.id, completableDeferred)
                                            fetchLikes(state.token, postResponse.id, completableDeferred)
                                        }) {
                                            Icon(
                                                imageVector = Icons.Outlined.FavoriteBorder,
                                                contentDescription = "Like Post",
                                                tint = tagColor,
                                                modifier = Modifier.size(30.dp)
                                            )
                                        }
                                        Text(
                                            text = numLikes.toString()
                                        )
                                    } else {
                                        IconButton(onClick = {
                                            val completableDeferred = CompletableDeferred<Unit>()
                                            deleteLike(state.token, postResponse.id, completableDeferred)
                                            fetchLikes(state.token, postResponse.id, completableDeferred)
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Favorite,
                                                contentDescription = "Dislike Post",
                                                tint = tagColor,
                                                modifier = Modifier.size(30.dp)
                                            )
                                        }
                                        Text(
                                            text = numLikes.toString()
                                        )
                                    }
                                }
                                if (!isFavorite) {
                                    IconButton(onClick = {
                                        val completableDeferred = CompletableDeferred<Unit>()
                                        addFavorite(state.token, postResponse.id, completableDeferred)
                                        fetchFavorites(state.token, completableDeferred)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Outlined.BookmarkBorder,
                                            contentDescription = "Bookmark Post",
                                            tint = tagColor,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                } else {
                                    IconButton(onClick = {
                                        val completableDeferred = CompletableDeferred<Unit>()
                                        deleteFavorite(state.token, postResponse.id, completableDeferred)
                                        fetchFavorites(state.token, completableDeferred)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Bookmark,
                                            contentDescription = "Unbookmark Post",
                                            tint = tagColor,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                                }
                            }
                        }

                    error != null -> {
                        Text(
                            modifier = Modifier.padding(bottom = 16.dp),
                            text = "Post cannot be fetched: $error",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}