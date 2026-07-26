package com.example.smart_watch.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.smart_watch.presentation.theme.Smart_watchTheme
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.smart_watch.presentation.SocketManager
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SocketManager.socket.connect()
        setContent {
            WearApp("Android")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketManager.socket.disconnect()
    }
}

@Composable
fun WearApp(greetingName: String) {

    var heartRate by remember { mutableStateOf((60..100).random()) }
    var spo2 by remember { mutableStateOf((95..100).random()) }
    var steps by remember { mutableStateOf((1000..10000).random()) }
    var battery by remember { mutableStateOf((40..100).random()) }
    var sync by remember {
        mutableStateOf(
            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(Date())
        )
    }

    val listState = rememberScalingLazyListState()

    Smart_watchTheme {
        AppScaffold {
            ScreenScaffold(scrollState = listState) {
                ScalingLazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {

                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(bottom = 12.dp, top = 16.dp)
                        ) {
                            Text(
                                text = "Mi Salud",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = "Conectado",
                                    tint = Color(0xFF4CAF50), // Verde sutil
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Emparejamiento hoy a las $sync",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        MetricCard("Frecuencia", "$heartRate RPM", Icons.Filled.Favorite, Color(0xFFEF5350))
                    }

                    item {
                        MetricCard("Oxígeno Sangre", "$spo2 %", Icons.Filled.WaterDrop, Color(0xFF42A5F5))
                    }

                    item {
                        MetricCard("Pasos", "$steps", Icons.Filled.DirectionsWalk, Color(0xFFFFA726))
                    }

                    item {
                        MetricCard("Batería", "$battery %", Icons.Filled.BatteryFull, Color(0xFF66BB6A))
                    }

                    item {
                        Button(
                            onClick = {
                                heartRate = (60..100).random()
                                spo2 = (95..100).random()
                                steps = (1000..10000).random()
                                battery = (40..100).random()
                                sync = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                                envioDatos(
                                    heartRate,
                                    spo2,
                                    steps,
                                    battery,
                                    sync
                                )

                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Actualizar",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Actualizar",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun envioDatos(
    frecuencia: Int,
    oxigeno: Int,
    pasos: Int,
    bateria: Int,
    hora: String
){
    val json = JSONObject()

    json.put("Frecuencia", frecuencia)
    json.put("oxigeno", oxigeno)
    json.put("pasos", pasos)
    json.put("bateria", bateria)
    json.put("hora", hora)

    SocketManager.socket.emit("Datos", json)
    Log.d("SocketInfo", "Intentando enviar: $json")

}


@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color
) {
    Card(
        onClick = { },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}