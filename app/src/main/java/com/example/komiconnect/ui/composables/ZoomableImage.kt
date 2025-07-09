package com.example.komiconnect.ui.composables

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun ZoomableImage(image: Bitmap, size: Int) {
    var openDialog by remember { mutableStateOf(false) }

    Image(
        bitmap = image.asImageBitmap(),
        contentDescription = "Immagine",
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .clickable { openDialog = true },
        contentScale = ContentScale.Fit
    )

    if (openDialog) {
        Dialog(onDismissRequest = { openDialog = false }) {
            Box(
                modifier = Modifier
                    .clickable { openDialog = false },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = "Immagine ingrandita",
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(200.dp)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
