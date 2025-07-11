package com.example.komiconnect.ui.composables

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.komiconnect.R
import com.example.komiconnect.network.PostResponse


@Composable
fun PostItem(item: PostResponse, image: Bitmap?, tagColorsMap: Map<String, Color>, modifier: Modifier, onClick: () -> Unit) {
    Card(
        onClick = { onClick() },
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (image != null) {
                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = "Post Picture",
                    modifier = Modifier.aspectRatio(7f / 7f).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.image_placeholder),
                    contentDescription = "Generic Post Picture",
                    modifier = Modifier.aspectRatio(7f / 7f).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp),
                    strokeWidth = 4.dp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.data.title.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                val categoryColor = tagColorsMap[item.label] ?: Color.Gray
                Canvas(
                    modifier = Modifier
                        .size(30.dp)
                        .padding(4.dp)
                ) {
                    drawCircle(color = categoryColor, radius = size.minDimension / 2)
                }
            }
        }
    }
}
