package fr.isen.abisse.androidsmartdevice

// Importation des bibliothèques nécessaires
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import fr.isen.abisse.androidsmartdevice.ui.theme.cyanColor
import fr.isen.abisse.androidsmartdevice.ui.theme.blackColor
import fr.isen.abisse.androidsmartdevice.ui.theme.whiteColor
import fr.isen.abisse.androidsmartdevice.ui.theme.grayColor

// Activité principale de ScanActivity, responsable de la gestion du scan BLE
class ScanActivity : ComponentActivity() {
    // Déclaration des variables nécessaires pour le Bluetooth et le scan
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private var scanning = false // Indique si un scan est en cours
    private val handler = Handler() // Handler pour planifier les tâches
    private val scannedDevices = mutableStateListOf<String>()  // Liste des périphériques scannés
    private var scanStatus by mutableStateOf("Waiting to scan...")  // État du scan (texte affiché)

    // Gestionnaire pour demander des permissions utilisateur
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            Toast.makeText(this, "Bluetooth permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Bluetooth permissions required", Toast.LENGTH_SHORT).show()
        }
    }

    // Durée maximale du scan BLE
    private val SCAN_PERIOD: Long = 50000

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation de l'adaptateur Bluetooth via le BluetoothManager
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        // Vérification si Bluetooth est disponible et activé
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Bluetooth error", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialisation du scanner Bluetooth LE
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        // Vérification et demande des permissions Bluetooth nécessaires
        checkAndRequestPermissions()

        // Chargement de l'interface utilisateur via Compose
        setContent {
            AndroidSmartDeviceTheme {
                // Mise en place du composant principal
                Scaffold(
                    modifier = Modifier.background(whiteColor),
                    topBar = {
                        // Barre supérieure de l'application
                        TopAppBar(
                            title = {
                                Text(
                                    text = "AndroidSmartDevice",
                                    color = blackColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 25.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            colors = TopAppBarDefaults.smallTopAppBarColors(
                                containerColor = cyanColor,
                                titleContentColor = blackColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },

                    containerColor = whiteColor
                ) { innerPadding ->
                    // Contenu principal de l'application
                    MainContentComponent(
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(whiteColor),
                        onStartScan = { startBluetoothScan() }, // Action pour démarrer/arrêter le scan
                        scannedDevices = scannedDevices, // Liste des appareils scannés
                        isScanning = scanning, // État du scan en cours
                        scanStatus = scanStatus, // Texte indiquant l'état du scan
                        onScanStatusChanged = { scanStatus = it },  // Mise à jour de l'état du scan
                        onDeviceClick = { deviceName -> // Gestion du clic sur un périphérique
                            val intent = Intent(this@ScanActivity, ConnectionActivity::class.java)
                            intent.putExtra("DEVICE_NAME", deviceName)  // Transmission du nom du périphérique
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }

    // Vérification et demande des permissions nécessaires
    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.BLUETOOTH_SCAN)
        }

        if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.BLUETOOTH_CONNECT)
        }

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            // Si des permissions manquent, les demander
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // Si toutes les permissions sont accordées, démarrer le scan
            startBluetoothScan()
        }
    }

    // Fonction pour démarrer ou arrêter le scan BLE
    private fun startBluetoothScan() {
        if (!scanning) {
            // Si le scan n'est pas en cours, démarrer un scan
            scanning = true
            scanStatus = "Scanning..."
            Toast.makeText(this, "Starting BLE scan...", Toast.LENGTH_SHORT).show()
            scanLeDevice()
        } else {
            // Si le scan est en cours, l'arrêter
            scanning = false
            scanStatus = "Waiting to scan..."
            Toast.makeText(this, "Stopping BLE scan...", Toast.LENGTH_SHORT).show()
            scanLeDevice()
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanLeDevice() {
        if (scanning) {
            // Démarrer le scan
            bluetoothLeScanner.startScan(leScanCallback)
        } else {
            // Arrêter le scan
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

    // Callback pour gérer les résultats du scan BLE
    private val leScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            // Ajouter les périphériques scannés à la liste
            result.device.name?.let { deviceName ->
                if (!scannedDevices.contains(deviceName)) {
                    scannedDevices.add(deviceName)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Toast.makeText(this@ScanActivity, "Scan failed: $errorCode", Toast.LENGTH_SHORT).show()
        }
    }
}

// Partie de l'interface utilisateur
@Composable
fun MainContentComponent(
    modifier: Modifier = Modifier,
    onStartScan: () -> Unit,
    scannedDevices: List<String>,
    isScanning: Boolean,
    scanStatus: String,
    onScanStatusChanged: (String) -> Unit,
    onDeviceClick: (String) -> Unit // Action lors d'un clic sur un périphérique
) {

}

