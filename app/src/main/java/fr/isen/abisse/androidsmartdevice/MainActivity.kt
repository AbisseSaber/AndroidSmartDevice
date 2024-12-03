package fr.isen.abisse.androidsmartdevice

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.abisse.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import fr.isen.abisse.androidsmartdevice.ui.theme.blackColor

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidSmartDeviceTheme {
                val context = LocalContext.current
                val cyanColor = Color(0xFF03DAC6) // Cyan color for the app bars and button

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White, // Set the background color of the scaffold to white
                    topBar = {
                        TopAppBar(
                            title = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "AndroidSmartDevice",
                                        color = blackColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 25.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.smallTopAppBarColors(
                                containerColor = cyanColor // Set the TopAppBar color to cyan
                            )
                        )
                    },
                    bottomBar = {
                        BottomAppBar(
                            containerColor = cyanColor, // Set the BottomAppBar color to cyan
                            contentColor = Color.White // Set the content color to white for icons
                        ) {
                            // You can add icons or actions here in the BottomAppBar
                            IconButton(onClick = { /* Handle your action */ }) {
                                Icon(painter = painterResource(id = R.drawable.phone_icon), contentDescription = "Home", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.weight(1f)) // Adds space between items
                            IconButton(onClick = { /* Handle another action */ }) {
                                Icon(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "Settings", tint = Color.White)
                            }
                        }
                    }
                ) { innerPadding ->
                    MainContentComponent(innerPadding, context, cyanColor) // Pass the cyan color to content
                }
            }
        }
    }
}

@Composable
fun MainContentComponent(innerPadding: PaddingValues, context: android.content.Context, cyanColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color.White), // Make sure the background of the content area is white
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Welcome text with a new size and black color
            Text(
                text = "Welcome Back!",
                fontSize = 40.sp,
                color = Color(0xFF000000), // Black color for the text
                modifier = Modifier.padding(bottom = 30.dp)
            )

            // Bluetooth image with a different size
            Image(
                painter = painterResource(id = R.drawable.ble_icon1),
                contentDescription = "Bluetooth Icon",
                modifier = Modifier
                    .size(180.dp)
                    .padding(bottom = 20.dp)
            )

            // New button with a cyan color
            Button(
                onClick = {
                    context.startActivity(Intent(context, ScanActivity::class.java))
                },
                colors = ButtonDefaults.buttonColors(containerColor = cyanColor), // Set the button color to cyan
                modifier = Modifier
                    .width(220.dp)
                    .height(60.dp)
                    .padding(top = 16.dp)
            ) {
                Text(text = "Start Scanning", color = Color.Black, fontSize = 18.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidSmartDeviceTheme {
        val context = LocalContext.current
        val cyanColor = Color(0xFF03DAC6) // Cyan color for the app bars and button
        MainContentComponent(innerPadding = PaddingValues(0.dp), context = context, cyanColor = cyanColor)
    }
}
