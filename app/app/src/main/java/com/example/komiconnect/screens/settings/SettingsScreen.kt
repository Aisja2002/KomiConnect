package com.example.komiconnect.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.komiconnect.data.models.Theme
import com.example.komiconnect.ui.KomiConnectRoute
import com.example.komiconnect.ui.composables.AppBar
import kotlinx.coroutines.flow.StateFlow


@Composable
fun SettingsScreen(state: StateFlow<SettingsState>, onLogout: () -> Unit, onThemeSelected: (Theme) -> Unit, navController: NavController) {
    val s = state.collectAsState()
    AppBar(navController, "Settings") {
            innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Scegli il tema:"
            )
            Theme.entries.forEach { theme ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (theme == s.value.theme),
                            onClick = { onThemeSelected(theme) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (theme == s.value.theme),
                        onClick = { onThemeSelected(theme) }
                    )
                    Text(
                        text = theme.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            Text(
                text = "Logout",
                textDecoration = TextDecoration.Underline,
                color = Color(0xffd1001f),
                modifier = Modifier
                    .padding(top = 32.dp)
                    .clickable {
                        onLogout()
                        navController.navigate(KomiConnectRoute.Login) }
            )
        }
    }
}