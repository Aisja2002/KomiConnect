package com.example.komiconnect.screens.add

import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.komiconnect.R
import com.example.komiconnect.network.ApiService
import com.example.komiconnect.network.ConventionResponse
import com.example.komiconnect.network.PostData
import com.example.komiconnect.network.PostRequest
import com.example.komiconnect.ui.KomiConnectRoute
import com.example.komiconnect.ui.TagConstants
import com.example.komiconnect.ui.composables.AppBar
import com.example.komiconnect.ui.rememberCameraLauncher
import com.example.komiconnect.ui.uriToBitmap
import com.image.cropview.CropType
import com.example.komiconnect.ui.uriToBitmap2
import com.image.cropview.ImageCrop
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddPostScreen(state: AddState,
                  fetchAllConventions: (String) -> Unit,
                  uploadPicture: (String, Int, ByteArray) -> Unit,
                  navController: NavController) {
    var expanded by remember { mutableStateOf(false) }
    val conventions = state.conventions
    var selectedConvention by remember { mutableStateOf<ConventionResponse?>(null) }
    var titleText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf<String?>(null) }
    val tagOptions = TagConstants.TAG_OPTIONS
    val tagColorsMap = TagConstants.TAG_COLORS_MAP


    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isImageLoaded by remember { mutableStateOf(false) }
    var loadedImage: Bitmap? by remember { mutableStateOf(null) }
    var imageToUpload: ByteArray? by remember { mutableStateOf(null) }
    var croppedImageToUpload: Bitmap? by remember { mutableStateOf(null) }

    val cameraPicture = rememberCameraLauncher(
        onPictureTaken = { imageUri ->
                    loadedImage = uriToBitmap2(imageUri, context.contentResolver)
                    isImageLoaded = true

        }
    )


    val pickMedia =
        rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
            uri?.let {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flag)

                val imageBitmap = uriToBitmap(context, it)

                if (imageBitmap != null) {
                    loadedImage = imageBitmap
                    isImageLoaded = true
                }
            }
        }

    LaunchedEffect(Unit) {
        fetchAllConventions(state.token)
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
                var aspectRatio = imageWidth / imageHeight
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
                        croppedImageToUpload = croppedBitmap
                        val stream = ByteArrayOutputStream()
                        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        imageToUpload = stream.toByteArray()
                        isImageLoaded = false
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Invia")
                }
            }
        }
    } else {
        AppBar(navController, "Crea Post") { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp)
                    .padding(top = 16.dp).fillMaxSize().verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally)
                    {
                        if(croppedImageToUpload != null) {
                            Image(
                                bitmap = croppedImageToUpload!!.asImageBitmap(),
                                contentDescription = "Generic Post Picture",
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
                        Text(
                            text = "Scegli immagine",
                            textAlign = TextAlign.Center,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.fillMaxWidth()
                                .clickable {
                                    pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                                    isImageLoaded = false
                                }
                        )
                        Text(
                            text = "Scatta foto",
                            textAlign = TextAlign.Center,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                .clickable {
                                    cameraPicture.captureImage()
                                    isImageLoaded = false
                                }
                        )
                    }
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = titleText,
                            onValueChange = { titleText = it },
                            label = { Text("Titolo del post") },
                            modifier = Modifier.fillMaxWidth(0.9f),
                            singleLine = true
                        )

                        Box {
                            OutlinedTextField(
                                value = selectedConvention?.data?.name ?: "A che fiera eri?",
                                onValueChange = { },
                                readOnly = true,
                                trailingIcon = {
                                    androidx.compose.material3.Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown arrow",
                                        Modifier.clickable { expanded = true }
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .clickable { expanded = true },
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Seleziona una fiera") },
                                    onClick = {
                                        selectedConvention = null
                                        expanded = false
                                    }
                                )
                                conventions.forEach { convention ->
                                    DropdownMenuItem(
                                        text = { Text("${convention.data.name}") },
                                        onClick = {
                                            selectedConvention = convention
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = descriptionText,
                            onValueChange = { descriptionText = it },
                            label = { Text("Descrizione") },
                            modifier = Modifier.fillMaxWidth(0.9f),
                            singleLine = false,
                            minLines = 3,
                            maxLines = 10
                        )


                        FlowRow(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            tagOptions.forEach { tag ->
                                val isSelected = selectedTag == tag
                                val customColor =
                                    tagColorsMap[tag] ?: MaterialTheme.colorScheme.primary
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
                                    elevation = ButtonDefaults.elevatedButtonElevation(
                                        defaultElevation = 2.dp
                                    )
                                ) {
                                    Text(text = tag)
                                }

                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { navController.navigate(KomiConnectRoute.Home) },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Annulla")
                            }
                            ElevatedButton(
                                onClick = {
                                    if (loadedImage != null && selectedConvention != null && selectedTag != null && descriptionText != "" && titleText != "") {
                                        coroutineScope.launch {
                                            val postData = PostData(titleText, descriptionText)
                                            val postResponse = PostRequest(
                                                selectedConvention!!.id,
                                                selectedTag!!,
                                                postData
                                            )
                                            val api = ApiService(state.token)
                                            val result = api.addPost(postResponse)
                                            when (result) {
                                                is Success -> {
                                                    Toast.makeText(
                                                        context,
                                                        "Post creato con successo",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    uploadPicture(state.token, result.value.id, imageToUpload!!)
                                                    navController.navigate(KomiConnectRoute.Home)
                                                }

                                                is Failure -> {
                                                    Toast.makeText(
                                                        context,
                                                        "Non è stato possibile creare il post",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Per creare il post, compila prima tutti i campi",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            )
                            {
                                Text("Invia")
                            }
                        }
                    }
                }
            }
        }
    }
}


