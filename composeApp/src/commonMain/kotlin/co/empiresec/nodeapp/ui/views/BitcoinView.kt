package co.empiresec.nodeapp.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop

@Composable
fun BitcoinView() {
    var isRunning by remember { mutableStateOf(false) }
    var logs by remember { mutableStateOf(listOf("Bitcoin node not started")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Control buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (!isRunning) {
                        isRunning = true
                        logs = logs + "Starting Bitcoin node..."
                        // TODO: Implement actual Bitcoin node start
                    }
                },
                enabled = !isRunning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                Spacer(Modifier.width(8.dp))
                Text("Start Bitcoin")
            }

            Button(
                onClick = {
                    if (isRunning) {
                        isRunning = false
                        logs = logs + "Stopping Bitcoin node..."
                        // TODO: Implement actual Bitcoin node stop
                    }
                },
                enabled = isRunning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Stop, contentDescription = "Stop")
                Spacer(Modifier.width(8.dp))
                Text("Stop Bitcoin")
            }

            Text(
                text = if (isRunning) "Running" else "Stopped",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }

        // Logs section
        Text(
            text = "Bitcoin Logs",
            style = MaterialTheme.typography.headlineSmall
        )

        Card(
            modifier = Modifier.fillMaxSize(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(logs) { log ->
                    Text(
                        text = log,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}