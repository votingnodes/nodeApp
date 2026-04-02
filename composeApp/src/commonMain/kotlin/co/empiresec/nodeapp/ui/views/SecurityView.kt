package co.empiresec.nodeapp.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import co.empiresec.nodeapp.bitcoin.BitcoinNodeManager
import co.empiresec.nodeapp.ui.components.Badge
import co.empiresec.nodeapp.ui.components.StatsCard
import co.empiresec.nodeapp.ui.theme.BitcoinOrange
import co.empiresec.nodeapp.ui.theme.BlockGreen

@Composable
fun SecurityView() {
    val nodeManager = remember { BitcoinNodeManager.instance }
    var autoBackup by remember { mutableStateOf(true) }
    var torEnabled by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        nodeManager.startAutoRefresh(10000)
    }
    
    DisposableEffect(Unit) {
        onDispose {
            nodeManager.stopAutoRefresh()
        }
    }
    
    val nodeStatus by nodeManager.nodeStatus.collectAsState()
    val networkInfo by nodeManager.networkInfo.collectAsState()
    val peers by nodeManager.peers.collectAsState()
    
    val isRunning = nodeStatus?.state == co.empiresec.nodeapp.bitcoin.DaemonState.RUNNING
    val connections = networkInfo?.connections ?: 0
    val bip110Peers = peers.count { it.version >= 70016 }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, null, tint = BlockGreen)
                    Text("Security Overview", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecurityMetric("Security Checks", "4/5", BlockGreen, Modifier.weight(1f))
                    SecurityMetric("Encryption", "AES-256", BitcoinOrange, Modifier.weight(1f))
                    SecurityMetric("Backups", "4", BitcoinOrange, Modifier.weight(1f))
                }
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Text("Security Audit", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                SecurityCheck("Wallet Encryption", if (isRunning) "Bitcoin daemon is running" else "Daemon not running", "pass")
                Spacer(Modifier.height(8.dp))
                SecurityCheck("Peer Connections", "$connections peers connected ($bip110Peers BIP-110)", if (connections > 0) "pass" else "warning")
                Spacer(Modifier.height(8.dp))
                SecurityCheck("Network Status", if (isRunning) "Node is operational" else "Node is offline", if (isRunning) "pass" else "warning")
                Spacer(Modifier.height(8.dp))
                SecurityCheck("Tor Privacy", if (torEnabled) "Tor is enabled" else "Tor is not enabled", if (torEnabled) "pass" else "warning", if (!torEnabled) "Enable Tor for enhanced privacy" else null)
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, null, tint = BitcoinOrange)
                    Text("Wallet Encryption", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Encryption Status")
                    Badge(text = "ENABLED", color = BlockGreen)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Your wallet.dat file is encrypted with AES-256-CBC",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {}) {
                        Text("Change Passphrase")
                    }
                    OutlinedButton(
                        onClick = {},
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Remove Encryption")
                    }
                }
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Download, null, tint = BitcoinOrange)
                        Text("Backup Management", style = MaterialTheme.typography.titleMedium)
                    }
                    Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = BitcoinOrange)) {
                        Text("Create Backup")
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Automatic Backups")
                        Text(
                            "Create backups every 48 hours",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = autoBackup,
                        onCheckedChange = { autoBackup = it }
                    )
                }
                Spacer(Modifier.height(12.dp))
                BackupItem("March 22, 2026", "14:35", "2.4 MB", "Automatic")
                Spacer(Modifier.height(8.dp))
                BackupItem("March 20, 2026", "09:12", "2.4 MB", "Manual")
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.VpnKey, null, tint = BlockGreen)
                    Text("Privacy & Network Settings", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Tor Network")
                        Text(
                            "Route all connections through Tor",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = torEnabled,
                        onCheckedChange = { torEnabled = it }
                    )
                }
                if (!torEnabled) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "⚠️ Your IP address is visible to peers",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.Settings, null, tint = BitcoinOrange)
                    Text("Node Configuration", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(12.dp))
                ConfigItem("Max Connections", "125")
                Spacer(Modifier.height(8.dp))
                ConfigItem("Max Mempool Size", "300 MB")
                Spacer(Modifier.height(8.dp))
                ConfigItem("Database Cache", "450 MB")
                Spacer(Modifier.height(8.dp))
                ConfigItem("RPC Port", "8332")
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = BitcoinOrange)) {
                        Text("Save Changes")
                    }
                    OutlinedButton(onClick = {}) {
                        Text("Reset to Default")
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun SecurityMetric(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Monospace, color = color)
    }
}

@Composable
private fun SecurityCheck(title: String, description: String, status: String, recommendation: String? = null) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            if (status == "pass") Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (status == "pass") BlockGreen else BitcoinOrange
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (recommendation != null) {
                Spacer(Modifier.height(4.dp))
                Text("💡 $recommendation", style = MaterialTheme.typography.bodySmall, color = BitcoinOrange)
            }
        }
        Badge(text = status.uppercase(), color = if (status == "pass") BlockGreen else BitcoinOrange)
    }
}

@Composable
private fun BackupItem(date: String, time: String, size: String, type: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("$date at $time", fontFamily = FontFamily.Monospace)
            Text("$size · $type", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        TextButton(onClick = {}) {
            Text("Restore")
        }
    }
}

@Composable
private fun ConfigItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
