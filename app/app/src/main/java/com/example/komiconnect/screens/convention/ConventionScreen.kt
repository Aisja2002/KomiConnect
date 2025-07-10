package com.example.komiconnect.screens.convention

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import java.time.ZoneId
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.komiconnect.R
import com.example.komiconnect.network.ConventionCoordinates
import com.example.komiconnect.network.ConventionData
import com.example.komiconnect.network.ConventionResponse
import com.example.komiconnect.ui.composables.AppBar
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import androidx.core.net.toUri
import com.example.komiconnect.ui.composables.CircularProfileImage
import com.example.komiconnect.ui.composables.ZoomableCircularImage
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun ConventionScreen(
    conventionID: Int?,
    state: ConventionState,
    conventionResponse: ConventionResponse?, error: String?,
    fetchConvention: (String, Int?) -> Unit,
    navController: NavController
) {

    val verticalScroll = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(state.token, conventionID) {
        if (conventionID != null) {
            fetchConvention(state.token, conventionID)
        }
    }

    val conventionData = ConventionData(
        name = conventionResponse?.data?.name?: "Nome non specificato",
        start = conventionResponse?.data?.start?: "Inizio non specificato",
        end = conventionResponse?.data?.end?: "Fine non specificata",
        location = conventionResponse?.data?.location ?: "Luogo non specificato",
        website = conventionResponse?.data?.website ?: "Sito non specificato",
        coordinates = conventionResponse?.data?.coordinates?: ConventionCoordinates(0.0, 0.0)
    )


    AppBar(navController, "Profilo") { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding).fillMaxWidth().verticalScroll(verticalScroll),
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
                    conventionResponse != null -> {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = 16.dp, end = 16.dp, start = 16.dp)
                            ) {
                                Text(
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    text = "Fiera",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(end = 16.dp, start = 16.dp, bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
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
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                            ) {
                                Text(
                                    text = "${conventionResponse.data.name}",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
                                )
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                Text(
                                    text = "${conventionResponse.data.location}",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                                )

                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${conventionResponse.data.website}",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth().clickable{
                                            val website = conventionResponse.data.website
                                            val intent = Intent(Intent.ACTION_VIEW, website?.toUri())
                                            context.startActivity(intent)
                                        },
                                    textDecoration = TextDecoration.Underline
                                )
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${conventionResponse.data.start} - ${conventionResponse.data.end}",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                                )

                                Text(
                                    text = "Aggiungi al calendario",
                                    textAlign = TextAlign.Center,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth()
                                        .clickable {
                                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                            val startDate = LocalDate.parse(conventionResponse.data.start.toString(), formatter)
                                            val endDate = LocalDate.parse(conventionResponse.data.end.toString(), formatter)
                                            val startMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                            val endMillis = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

                                            val intent = Intent(Intent.ACTION_INSERT).apply {
                                                data = CalendarContract.Events.CONTENT_URI
                                                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                                                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                                                putExtra(CalendarContract.Events.ALL_DAY, true)
                                                putExtra(CalendarContract.Events.TITLE, conventionResponse.data.name ?: "Evento")
                                                putExtra(CalendarContract.Events.EVENT_LOCATION, conventionResponse.data.location ?: "")
                                                putExtra(CalendarContract.Events.DESCRIPTION, "Aggiunto da KomiConnect")
                                            }
                                            context.startActivity(intent)
                                        }
                                )
                            }
                        }
                    }

                    (error != null) -> {
                        Text(
                            modifier = Modifier.padding(bottom = 16.dp),
                            text = "Convention cannot be fetched$error",
                            fontWeight = FontWeight.Bold
                        )
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
                    .fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Posizione",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    ConventionMap(conventionData)
                }
            }
        }
    }
}



@Composable
fun ConventionMap(data: ConventionData?){
    val markerPosition = LatLng(data?.coordinates?.latitude ?: 0.0, data?.coordinates?.longitude ?: 0.0)

    val cameraPositionState = CameraPositionState (
        position = if (data?.coordinates != null) {
            CameraPosition.fromLatLngZoom(markerPosition, 16f)
        } else {
            CameraPosition.fromLatLngZoom(markerPosition, 16f)
        }
    )

    GoogleMap(
        modifier = Modifier.fillMaxSize().height(250.dp),
        cameraPositionState = cameraPositionState
    ) {

            Marker(
                state = MarkerState(position = markerPosition)
            )
        }
    }

