package com.example.komiconnect.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items

@Composable
fun LabelSelector(
    selectedTag: String?,
    onTagSelected: (String?) -> Unit,
    tagOptions: List<String>,
    tagColorsMap: Map<String, Color>
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
                onClick = { onTagSelected(if (isSelected) null else tag) },
                shape = RoundedCornerShape(50),
                colors = buttonColors,
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp)
            ) {
                Text(text = tag)
            }
        }
    }
}
