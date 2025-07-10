package com.example.komiconnect.screens.login


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.komiconnect.R
import com.example.komiconnect.network.NetworkAPI
import com.example.komiconnect.ui.KomiConnectRoute
import com.example.komiconnect.ui.composables.LabeledTextField
import com.example.komiconnect.ui.composables.PasswordField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLogin: (String) -> Unit,
    navController: NavController
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val netAPI = NetworkAPI()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.komilogo),
                contentDescription = "KomiConnect Logo",
                modifier = Modifier
                    .height(100.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 24.dp),
                thickness = 2.dp,
                color = Color.LightGray
            )

            Text(
                text = "Login",
                style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            LabeledTextField(
                value = username,
                onValueChange = { username = it },
                label = "Inserisci username",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            )

            PasswordField(
                password = password,
                onPasswordChange = { password = it },
                passwordVisible = passwordVisible,
                onVisibilityChange = { passwordVisible = !passwordVisible },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    performLogin(
                        coroutineScope, username, password,
                        context, netAPI, onLogin, navController
                    )
                },
                enabled = username.isNotBlank() && password.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
            ) {
                Text("Invia")
            }

            Text(
                text = "Se non sei ancora registrato, registrati qui.",
                textDecoration = TextDecoration.Underline,
                color = Color(0xFF1E88E5),
                modifier = Modifier
                    .padding(top = 32.dp)
                    .clickable { navController.navigate(KomiConnectRoute.Register) }
            )
        }
    }
}



private fun performLogin(
    coroutineScope: CoroutineScope,
    username: String,
    password: String,
    context: Context,
    netAPI: NetworkAPI,
    onLogin: (String) -> Unit,
    navController: NavController
) {
    coroutineScope.launch {
        val (message, success) = netAPI.Login(username, password)
        if (success) {
            Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
            onLogin(message)
            navController.navigate(KomiConnectRoute.Home)
        } else {
            Toast.makeText(context, "Authentication failed. $message", Toast.LENGTH_LONG).show()
        }
    }
}

