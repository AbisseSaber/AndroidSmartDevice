package fr.isen.abisse.androidsmartdevice

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.abisse.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import androidx.compose.ui.platform.LocalContext

// Activité principale qui gère la connexion à un périphérique
class ConnectionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupération de l'adresse de l'appareil depuis les intentions
        val deviceAddress = intent.getStringExtra("DEVICE_ADDRESS") ?: "Unknown Address"

        // Configuration de l'interface utilisateur avec Jetpack Compose
        setContent {
            AndroidSmartDeviceTheme {
                ConnectionScreen(deviceAddress = deviceAddress)
            }
        }
    }
}

// Composable principal qui définit l'écran de connexion
@Composable
fun ConnectionScreen(deviceAddress: String) {
    Scaffold(
        topBar = { ConnectionTopAppBar() } // Barre supérieure personnalisée
    ) { innerPadding ->
        ConnectionContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp), // Espacement interne
            deviceAddress = deviceAddress // Passage de l'adresse de l'appareil
        )
    }
}

// Barre supérieure avec un titre personnalisé
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionTopAppBar() {
    TopAppBar(
        title = {
            Text(
                text = "AndroidSmartDevice", // Titre de l'application
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Black, // Couleur de fond
            titleContentColor = Color.White // Couleur du texte
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

// Contenu principal de l'écran
@Composable
fun ConnectionContent(modifier: Modifier, deviceAddress: String) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally // Alignement centré
    ) {
        DeviceAddressSection(deviceAddress = deviceAddress) // Section d'affichage de l'adresse
        Spacer(modifier = Modifier.height(16.dp)) // Espacement
        Divider(color = Color.LightGray, thickness = 1.dp) // Ligne de séparation
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.stop_icon), // Image affichée
            contentDescription = "LED Image",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LedControlSection() // Section de contrôle des LEDs
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))
        ButtonCounterSection() // Section de comptage des clics de boutons
    }
}

// Affiche l'adresse de l'appareil
@Composable
fun DeviceAddressSection(deviceAddress: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Address:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = deviceAddress,
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

// Section de contrôle des LEDs
@Composable
fun LedControlSection() {
    val context = LocalContext.current // Récupère le contexte Android
    LedControlButton(
        label = "Turn ON LEDs",
        onClick = {
            Toast.makeText(context, "LED Command Sent", Toast.LENGTH_SHORT).show() // Message Toast
        }
    )
}

// Section avec deux boutons pour compter les clics
@Composable
fun ButtonCounterSection() {
    val context = LocalContext.current
    ClickCounterButton(
        label = "Check Main Button Clicks",
        onClick = {
            Toast.makeText(context, "Reading Main Button Clicks", Toast.LENGTH_SHORT).show()
        }
    )
    Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
    ClickCounterButton(
        label = "Check Third Button Clicks",
        onClick = {
            Toast.makeText(context, "Reading Third Button Clicks", Toast.LENGTH_SHORT).show()
        }
    )
}

// Bouton pour contrôler les LEDs
@Composable
fun LedControlButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        )
    ) {
        Text(text = label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// Bouton pour compter les clics
@Composable
fun ClickCounterButton(label: String, onClick: () -> Unit) {
    var clickCount by remember { mutableStateOf(0) } // Compteur de clics

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Button(
            onClick = {
                onClick()
                clickCount += 1 // Incrémente le compteur à chaque clic
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text(text = label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            modifier = Modifier
                .size(50.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = clickCount.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold) // Affichage du compteur
        }
    }
}
