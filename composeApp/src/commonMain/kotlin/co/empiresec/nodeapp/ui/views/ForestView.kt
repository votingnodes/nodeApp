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
import co.empiresec.nodeapp.ui.components.Badge
import co.empiresec.nodeapp.ui.components.StatsCard
import co.empiresec.nodeapp.ui.theme.BitcoinOrange
import co.empiresec.nodeapp.ui.theme.BlockGreen
import kotlinx.coroutines.delay

@Composable
fun ForestView() {
    var cpuUsage by remember { mutableStateOf(23) }
    var ramUsage by remember { mutableStateOf(1247) }
    var diskStatus by remember { mutableStateOf("Idle") }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            cpuUsage = (15..30).random()
            ramUsage = (1100..1300).random()
            diskStatus = if (diskStatus == "Idle") "Flushing" else "Idle"
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SystemMetricCard(
                    icon = Icons.Default.BarChart,
                    label = "CPU Usage",
                    value = "$cpuUsage%",
                    progress = cpuUsage / 100f,
                    modifier = Modifier.weight(1f)
                )
                SystemMetricCard(
                    icon = Icons.Default.Memory,
                    label = "RAM Usage",
                    value = "$ramUsage MB",
                    progress = ramUsage / 4096f,
                    modifier = Modifier.weight(1f)
                )
                SystemMetricCard(
                    icon = Icons.Default.Storage,
                    label = "Disk I/O",
                    value = "2847 IOPS",
                    progress = 0.6f,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Nature, null, tint = BlockGreen)
                    Text("Utreexo Accumulator", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AccumulatorStat("Proof Set Size", "73.4 MB", BlockGreen, Modifier.weight(1f))
                    AccumulatorStat("Accumulator Roots", "64", BitcoinOrange, Modifier.weight(1f))
                    AccumulatorStat("Proof Efficiency", "99.7%", BitcoinOrange, Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                InfoRow("Latest Root Hash", "0xf3a8b2c1...9d4e5f7a")
                InfoRow("Total UTXOs Tracked", "152,847,392")
                InfoRow("Proof Validations", "834,527")
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
                        Icon(Icons.Default.Storage, null, tint = BitcoinOrange)
                        Text("Disk State", style = MaterialTheme.typography.titleMedium)
                    }
                    Badge(
                        text = diskStatus.uppercase(),
                        color = if (diskStatus == "Idle") BlockGreen else BitcoinOrange
                    )
                }
                Spacer(Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoRow("Total Disk Usage", "1.2 GB")
                    InfoRow("Write Speed", "45.7 MB/s")
                    InfoRow("Read Speed", "89.3 MB/s")
                    InfoRow("Last Flush", "23s ago")
                    InfoRow("Total Flushes", "15,647")
                }
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.FilterList, null, tint = MaterialTheme.colorScheme.error)
                    Text("BIP-110 Filter Statistics", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterStat("Total Filtered", "1,247", Modifier.weight(1f))
                    FilterStat("Last 24h", "342", Modifier.weight(1f))
                    FilterStat("Blocked Ordinals", "287", Modifier.weight(1f))
                }
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.TrendingUp, null, tint = BitcoinOrange)
                    Text("Mempool Floor", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(16.dp))
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("14", style = MaterialTheme.typography.displayMedium, color = BitcoinOrange)
                        Spacer(Modifier.width(8.dp))
                        Text("sat/vB minimum fee", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        "Transactions below this fee rate are filtered by BIP-110 enforcement",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun SystemMetricCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    StatsCard(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = BitcoinOrange, modifier = Modifier.size(16.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(8.dp))
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
private fun AccumulatorStat(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Monospace, color = color)
    }
}

@Composable
private fun FilterStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
    }
}
