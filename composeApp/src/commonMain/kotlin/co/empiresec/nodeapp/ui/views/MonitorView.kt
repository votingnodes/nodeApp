package co.empiresec.nodeapp.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
fun MonitorView() {
    val nodeManager = remember { BitcoinNodeManager.instance }
    
    LaunchedEffect(Unit) {
        nodeManager.startAutoRefresh(3000)
    }
    
    DisposableEffect(Unit) {
        onDispose {
            nodeManager.stopAutoRefresh()
        }
    }
    
    val nodeStatus by nodeManager.nodeStatus.collectAsState()
    val blockChainInfo by nodeManager.blockChainInfo.collectAsState()
    val networkInfo by nodeManager.networkInfo.collectAsState()
    
    val isRunning = nodeStatus?.state == co.empiresec.nodeapp.bitcoin.DaemonState.RUNNING
    val blockCount = blockChainInfo?.blocks ?: 0
    val connections = networkInfo?.connections ?: 0
    val diskUsage = blockChainInfo?.sizeOnDisk ?: 0
    
    val cpuUsage = if (isRunning) 21 else 0
    val ramUsage = if (isRunning) 1247 else 0

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
                    Icon(Icons.Default.MonitorHeart, null, tint = if (isRunning) BlockGreen else BitcoinOrange)
                    Text("System Health", style = MaterialTheme.typography.titleMedium)
                    Badge(text = if (isRunning) "RUNNING" else "STOPPED", color = if (isRunning) BlockGreen else BitcoinOrange)
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SystemHealth("Block Height", "%,d".format(blockCount), if (blockCount > 0) 1f else 0f, Modifier.weight(1f))
                    SystemHealth("Connections", connections.toString(), (connections.coerceAtMost(50) / 50f), Modifier.weight(1f))
                    SystemHealth("Disk Usage", if (diskUsage > 0) "%.1f GB".format(diskUsage / 1_000_000_000) else "--", (diskUsage.coerceAtMost(500_000_000_000L) / 500_000_000_000f), Modifier.weight(1f))
                }
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Text("CPU Usage History", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                // Placeholder for chart - would use a charting library in production
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "CPU: $cpuUsage%",
                        style = MaterialTheme.typography.displayMedium,
                        color = BitcoinOrange
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ChartStat("Current", "$cpuUsage%", BitcoinOrange, Modifier.weight(1f))
                    ChartStat("Average", "21%", MaterialTheme.colorScheme.onSurface, Modifier.weight(1f))
                    ChartStat("Peak (24h)", "47%", MaterialTheme.colorScheme.error, Modifier.weight(1f))
                }
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Text("Network Bandwidth (24h)", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BandwidthStat("Downloaded", "234.5 GB", BlockGreen, Modifier.weight(1f))
                    BandwidthStat("Uploaded", "187.3 GB", BitcoinOrange, Modifier.weight(1f))
                }
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Text("Disk Usage Distribution", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                val totalDiskGB = diskUsage / 1_000_000_000.0
                val blockchainGB = totalDiskGB * 0.95
                val indexesGB = totalDiskGB * 0.04
                val walletGB = totalDiskGB * 0.01
                DiskUsageItem("Blockchain", "%.1f GB".format(blockchainGB), BitcoinOrange)
                Spacer(Modifier.height(8.dp))
                DiskUsageItem("Indexes", "%.1f GB".format(indexesGB), BlockGreen)
                Spacer(Modifier.height(8.dp))
                DiskUsageItem("Wallet Data", "%.1f GB".format(walletGB), MaterialTheme.colorScheme.error)
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Text("Node Uptime (Last 7 Days)", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UptimeStat("7-Day Average", "99.7%", Modifier.weight(1f))
                    UptimeStat("Total Uptime", "14d 7h", Modifier.weight(1f))
                    UptimeStat("Downtime Events", "2", Modifier.weight(1f))
                }
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.Warning, null, tint = BitcoinOrange)
                    Text("Recent System Events", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(12.dp))
                SystemAlert("Mempool size approaching limit", "287.3 MB / 300 MB", "warning")
                Spacer(Modifier.height(8.dp))
                SystemAlert("New peer connection established", "BIP-110 compatible", "info")
                Spacer(Modifier.height(8.dp))
                SystemAlert("Automatic backup completed", "2.4 MB backed up", "success")
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun SystemHealth(label: String, value: String, progress: Float, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.Monospace)
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            color = BitcoinOrange,
            trackColor = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun ChartStat(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, fontFamily = FontFamily.Monospace, color = color)
    }
}

@Composable
private fun BandwidthStat(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, shape = CircleShape)
            )
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Monospace, color = color)
    }
}

@Composable
private fun DiskUsageItem(name: String, size: String, color: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, shape = CircleShape)
            )
            Text(name)
        }
        Text(size, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun UptimeStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.Monospace, color = BlockGreen)
    }
}

@Composable
private fun SystemAlert(message: String, details: String, level: String) {
    val color = when (level) {
        "warning" -> BitcoinOrange
        "info" -> androidx.compose.ui.graphics.Color(0xFF4A90E2)
        "success" -> BlockGreen
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Circle,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(message, style = MaterialTheme.typography.bodyMedium)
            Text(details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
