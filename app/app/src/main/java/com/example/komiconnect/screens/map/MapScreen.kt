package com.example.komiconnect.screens.map
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.komiconnect.network.ConventionResponse
import com.example.komiconnect.ui.Convention
import com.example.komiconnect.ui.Coordinates
import com.example.komiconnect.ui.LocationService
import com.example.komiconnect.ui.PermissionStatus
import com.example.komiconnect.ui.composables.AppBar
import com.example.komiconnect.ui.rememberMultiplePermissions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch


@Composable
fun MapScreen(state: MapState, allConventions: (String) -> Unit, navController: NavController) {
    LaunchedEffect(Unit) {
        allConventions(state.token)
    }

    AppBar(navController, "Mappa") { innerPadding ->

        val ctx = LocalContext.current

        var showLocationDisabledAlert by remember { mutableStateOf(false) }
        var showPermissionDeniedAlert by remember { mutableStateOf(false) }
        var showPermissionPermanentlyDeniedSnackbar by remember { mutableStateOf(false) }

        val locationService = remember { LocationService(ctx) }
        var coordinates: Coordinates? by remember { mutableStateOf(null) }

        val scope = rememberCoroutineScope()
        fun getCurrentLocation() = scope.launch {
            try {
                coordinates = locationService.getCurrentLocation()
            } catch (_: IllegalStateException) {
                showLocationDisabledAlert = true
            }
        }

        val locationPermissions = rememberMultiplePermissions(
            listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        ) { statuses ->
            when {
                statuses.any { it.value == PermissionStatus.Granted } -> {
                    getCurrentLocation()
                }
                statuses.all { it.value == PermissionStatus.PermanentlyDenied } ->
                    showPermissionPermanentlyDeniedSnackbar = true
                else ->
                    showPermissionDeniedAlert = true
            }
        }

        @Composable
        fun getLocationOrRequestPermission() {
            if (locationPermissions.statuses.any { it.value.isGranted }) {
                getCurrentLocation()
            } else {
                SideEffect {
                    locationPermissions.launchPermissionRequest()
                }
            }
        }

        getLocationOrRequestPermission()

        val snackbarHostState = remember { SnackbarHostState() }

        if (showLocationDisabledAlert) {
            AlertDialog(
                title = { Text("Location disabled") },
                text = { Text("Location must be enabled to get your coordinates in the app.") },
                confirmButton = {
                    TextButton(onClick = {
                        locationService.openLocationSettings()
                        showLocationDisabledAlert = false
                    }) {
                        Text("Enable")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLocationDisabledAlert = false }) {
                        Text("Dismiss")
                    }
                },
                onDismissRequest = { showLocationDisabledAlert = false }
            )
        }

        if (showPermissionDeniedAlert) {
            AlertDialog(
                title = { Text("Location permission denied") },
                text = { Text("Location permission is required to get your coordinates in the app.") },
                confirmButton = {
                    TextButton(onClick = {
                        locationPermissions.launchPermissionRequest()
                        showPermissionDeniedAlert = false
                    }) {
                        Text("Grant")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPermissionDeniedAlert = false }) {
                        Text("Dismiss")
                    }
                },
                onDismissRequest = { showPermissionDeniedAlert = false }
            )
        }

        if (showPermissionPermanentlyDeniedSnackbar) {
            LaunchedEffect(snackbarHostState) {
                val res = snackbarHostState.showSnackbar(
                    "Location permission is required.",
                    "Go to Settings",
                    duration = SnackbarDuration.Long
                )
                if (res == SnackbarResult.ActionPerformed) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", ctx.packageName, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    if (intent.resolveActivity(ctx.packageManager) != null) {
                        ctx.startActivity(intent)
                    }
                }
                showPermissionPermanentlyDeniedSnackbar = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize().padding(bottom = 156.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            }
        if(locationPermissions.statuses.any { it.value.isGranted }) {
            ExtendedMap(state.conventions, coordinates, navController, innerPadding)
        }
        }
    }

@Composable
fun ExtendedMap(conventions: List<ConventionResponse>, coordinates: Coordinates?, navController: NavController, innerPadding: PaddingValues){
    val romeColosseum = LatLng(41.890251, 12.492231)

    val cameraPositionState = rememberCameraPositionState {
        position = if (coordinates != null) {
            CameraPosition.fromLatLngZoom(LatLng(coordinates.latitude, coordinates.longitude), 10f)
        } else {
            CameraPosition.fromLatLngZoom(romeColosseum, 10f)
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize().zIndex(-10f).padding(innerPadding),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true)
    ) {
        conventions.forEach { convention ->
            val latlong = LatLng(convention.data.coordinates?.latitude ?: 0.0, convention.data.coordinates?.longitude ?: 0.0)
            Marker(
                state = MarkerState(position = latlong),
                title = convention.data.name,
                snippet = "Dal ${convention.data.start} - Al ${convention.data.end}",
                onInfoWindowClick = {
                    navController.navigate(Convention(convention.id))
                }
            )
        }
    }
}

