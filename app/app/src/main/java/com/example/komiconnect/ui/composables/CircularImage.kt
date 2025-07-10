package com.example.komiconnect.ui.composables

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun CircularProfileImage(
    imageBitmap: Bitmap?,
    placeholder: Int,
    size: Dp,
    contentDescription: String = "Profile Image",
    contentScale: ContentScale = ContentScale.Crop,
    modifier: Modifier = Modifier
) {
    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = contentScale
        )
    } else {
        Image(
            painter = painterResource(id = placeholder),
            contentDescription = contentDescription,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = contentScale
        )
    }
}

@Composable
fun ZoomableCircularImage(
    image: Bitmap,
    size: Dp = 100.dp,
    dialogSize: Dp = 300.dp,
    modifier: Modifier = Modifier
) {
    var openDialog by remember { mutableStateOf(false) }

    Image(
        bitmap = image.asImageBitmap(),
        contentDescription = "Immagine profilo",
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable { openDialog = true },
        contentScale = ContentScale.Crop
    )

    if (openDialog) {
        Dialog(onDismissRequest = { openDialog = false }) {
            Box(
                modifier = Modifier
                    .clickable { openDialog = false }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = "Immagine ingrandita",
                    modifier = Modifier
                        .size(dialogSize)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}


