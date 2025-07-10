package com.example.komiconnect.screens.profile

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.komiconnect.R
import com.example.komiconnect.network.PostResponse
import com.example.komiconnect.network.UserData
import com.example.komiconnect.network.UserResponse
import com.example.komiconnect.ui.Post
import com.example.komiconnect.ui.TagConstants
import com.example.komiconnect.ui.composables.AppBar
import com.example.komiconnect.ui.composables.BadgeItem
import com.example.komiconnect.ui.composables.CircularProfileImage
import com.example.komiconnect.ui.composables.PostItem
import com.example.komiconnect.ui.composables.ZoomableCircularImage
import com.example.komiconnect.ui.tokenToID
import com.example.komiconnect.ui.uriToBitmap
import com.image.cropview.CropType
import com.image.cropview.ImageCrop
import java.io.ByteArrayOutputStream


@Composable
fun ProfileScreen(
    userID: Int?,
    state: ProfileState,
    userResponse: UserResponse?, error: String?,
    postResponse: Array<PostResponse>?, postError: String?,
    fetchUser: (String, Int?) -> Unit, saveChanges: (UserData, String) -> Unit,
    uploadPicture: (String,Int,ByteArray) -> Unit,
    fetchPosts: (String, Int?) -> Unit,
    navController: NavController,
) {

    val myID = tokenToID(state.token)
    val context = LocalContext.current

    var isImageLoaded by remember { mutableStateOf(false) }
    var loadedImage: Bitmap? by remember { mutableStateOf(null) }

    val pickMedia =  rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
            uri?.let {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flag)

                val imageBitmap = uriToBitmap(context, it)

                if(imageBitmap != null) {
                    loadedImage = imageBitmap

                    isImageLoaded = true
                }
            }
        }


    LaunchedEffect(state.token, userID) {
        val idForPosts = userID ?: myID
        fetchUser(state.token, userID)
        fetchPosts(state.token, idForPosts)
    }

    val userData = UserData(
        location = userResponse?.data?.location ?: "Luogo non specificato",
        bio = userResponse?.data?.bio ?: "Nessuna biografia"
    )
    var editingMode by remember { mutableStateOf(false) }
    var currentEditableLocation by remember { mutableStateOf(userData.location) }
    var currentEditableBio by remember { mutableStateOf(userData.bio) }
    val scrollState = rememberScrollState()
    val tagColorsMap = TagConstants.TAG_COLORS_MAP

    LaunchedEffect(userResponse) {
        currentEditableLocation = userResponse?.data?.location ?: "Luogo non specificato"
        currentEditableBio = userResponse?.data?.bio ?: "Nessuna biografia"
    }



    if (isImageLoaded) {
        val imageToBeCropped = loadedImage!!
        val imageCrop = ImageCrop(imageToBeCropped)
        val imageWidth = imageToBeCropped.width.toFloat()
        val imageHeight = imageToBeCropped.height.toFloat()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                var aspectRatio = imageWidth/imageHeight
                var screenWidth = LocalConfiguration.current.screenWidthDp.dp
                var targetHeight = screenWidth / aspectRatio
                imageCrop.ImageCropView(
                    cropType = CropType.SQUARE,
                    modifier = Modifier
                        .width(screenWidth * 0.75f)
                        .height(targetHeight * 0.75f)
                )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        isImageLoaded = false
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Annulla")
                }

                Spacer(modifier = Modifier.padding(horizontal = 8.dp))

                Button(
                    onClick = {
                        val croppedBitmap = imageCrop.onCrop()
                        val stream = ByteArrayOutputStream()
                        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        val blob = stream.toByteArray()
                        uploadPicture(state.token, myID!!, blob)
                        fetchUser(state.token, myID)
                        isImageLoaded = false
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Invia")
                }
            }
        }
    } else {
    AppBar(navController, "Profilo") { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(scrollState),
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
                    userResponse != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Utente",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (state.profilePicture != null) {
                                ZoomableCircularImage(
                                    image = state.profilePicture!!,
                                    size = 100.dp
                                )
                            } else {
                                CircularProfileImage(
                                    imageBitmap = null,
                                    placeholder = R.drawable.square_person,
                                    contentDescription = "Generic Profile Picture",
                                    contentScale =  ContentScale.Crop,
                                    size = 100.dp,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                )
                            }


                            if (userID == myID || userID == null && !editingMode) {
                                Text(
                                    text = "Cambia immagine",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier
                                        .clickable {
                                            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                                        }
                                        .padding(top = 8.dp)
                                )
                            }

                            if (editingMode) {
                                OutlinedTextField(
                                    value = currentEditableLocation ?: "",
                                    onValueChange = { currentEditableLocation = it },
                                    label = { Text("Location") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                )
                                OutlinedTextField(
                                    value = currentEditableBio ?: "",
                                    onValueChange = { currentEditableBio = it },
                                    label = { Text("Bio") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    TextButton(onClick = {
                                        currentEditableLocation = userData.location
                                        currentEditableBio = userData.bio
                                        editingMode = false
                                    }) {
                                        Text("Annulla")
                                    }

                                    TextButton(onClick = {
                                        saveChanges(
                                            UserData(
                                                location = currentEditableLocation ?: "",
                                                bio = currentEditableBio ?: ""
                                            ),
                                            state.token
                                        )
                                        editingMode = false
                                    }) {
                                        Text("Salva")
                                    }
                                }
                            } else {

                                Text(
                                    text = userResponse.username,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    textAlign = TextAlign.Center
                                )

                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                Text(
                                    text = userData.location ?: "",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    textAlign = TextAlign.Center
                                )

                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                Text(
                                    text = userData.bio ?: "",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    textAlign = TextAlign.Center
                                )

                                if (userID == myID || userID == null) {
                                    Text(
                                        text = "Modifica profilo",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier
                                            .clickable { editingMode = true }
                                            .padding(top = 16.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    error != null -> {
                        Text(
                            modifier = Modifier.padding(bottom = 16.dp),
                            text = "User cannot be fetched: $error",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (!editingMode){
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Badge",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (state.badges.isEmpty()) {
                            Text("Nessun badge trovato.")
                        } else {
                            val badgesInPairs = state.badges.chunked(2)

                            badgesInPairs.forEach { pair ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    pair.forEach { badge ->
                                        BadgeItem(
                                            item = badge,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (pair.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Post",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    when {
                        postResponse != null && postResponse.isNotEmpty() -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val postsInPairs = postResponse.reversed().toList().chunked(2)

                                postsInPairs.forEach { pair ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    ) {
                                        pair.forEach { postItem ->
                                            PostItem(
                                                postItem,
                                                state.imagePosts[postItem.id],
                                                tagColorsMap,
                                                modifier = Modifier.weight(1f),
                                                onClick = {
                                                    navController.navigate(Post(postItem.id))
                                                },
                                            )
                                        }
                                        if (pair.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }

                        postError != null -> {
                            Text(
                                text = "Errore nel caricamento dei post: $postError",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        else -> {
                            Text(
                                text = "Nessun post trovato.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                    }
                }
            }
        }
    }
