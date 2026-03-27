package co.empiresec.nodeapp.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import co.empiresec.nodeapp.ui.components.Badge
import co.empiresec.nodeapp.ui.components.StatsCard
import co.empiresec.nodeapp.ui.theme.BitcoinOrange
import co.empiresec.nodeapp.ui.theme.BlockGreen
import co.empiresec.nodeapp.ui.theme.DarkBackground
import kotlinx.coroutines.delay

data class PeerInfo(
    val ip: String,
    val taproot: Boolean,
    val bip110: Boolean,
    val inbound: Boolean,
    val ping: Int
)

@Composable
fun PulseView() {
    var syncProgress by remember { mutableStateOf(87.3f) }
    var isEnforced by remember { mutableStateOf(true) }
    var networkIn by remember { mutableStateOf(1247) }
    var networkOut by remember { mutableStateOf(892) }

    // Simulate updates
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            syncProgress = (syncProgress + 0.1f).coerceAtMost(100f)
            if (syncProgress >= 100f) syncProgress = 87.3f

            networkIn = (500..2000).random()
            networkOut = (300..1500).random()
        }
    }

    val peers = remember {
        listOf(
            PeerInfo("45.132.246.15", true, true, false, 45),
            PeerInfo("23.94.21.187", true, true, true, 32),
            PeerInfo("178.128.45.92", true, false, false, 78),
            PeerInfo("104.248.156.201", false, false, true, 125),
            PeerInfo("188.166.23.47", true, true, false, 56),
            PeerInfo("142.93.156.89", true, true, true, 41)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Status Card
        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // Circular Progress
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = syncProgress / 100f,
                            modifier = Modifier.size(180.dp),
                            color = BitcoinOrange,
                            strokeWidth = 8.dp,
                            trackColor = MaterialTheme.colorScheme.outline
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%.1f%%".format(syncProgress),
                                style = MaterialTheme.typography.displaySmall,
                                color = BitcoinOrange
                            )
                            Text(
                                text = "Utreexo Sync",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Consensus Info
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "CONSENSUS MODE",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Badge(
                            text = if (isEnforced) "BIP-110 ENFORCED" else "LEGACY",
                            color = if (isEnforced) BlockGreen else BitcoinOrange,
                            modifier = Modifier
                        )
                        TextButton(onClick = { isEnforced = !isEnforced }) {
                            Text("Toggle Mode →")
                        }

                        Spacer(Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            InfoRow("Block Height", "834,527")
                            InfoRow("Network", "mainnet")
                            InfoRow("Uptime", "14d 7h 23m")
                            InfoRow("Last Block", "2min ago", BlockGreen)
                            InfoRow("Version", "Floresta v0.4.0")
                        }
                    }
                }
            }
        }

        // Blockchain Info Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    icon = Icons.Default.Settings,
                    label = "Difficulty",
                    value = "79.35T",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    icon = Icons.Default.Storage,
                    label = "Chain Size",
                    value = "573.8 GB",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    icon = Icons.Default.Wifi,
                    label = "Connections",
                    value = "6",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Mempool Info
        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Receipt, null, tint = BitcoinOrange)
                    Text(
                        "Mempool Information",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Transactions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "47,823",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Size",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "89.4 MB",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Min Fee",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "14 sat/vB",
                            style = MaterialTheme.typography.headlineMedium,
                            color = BitcoinOrange
                        )
                    }
                }
            }
        }

        // Network Traffic
        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Cloud, null, tint = BlockGreen)
                    Text(
                        "Network Traffic",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NetworkTrafficCard(
                        label = "Received",
                        value = "$networkIn KB/s",
                        total = "234.5 GB",
                        color = BlockGreen,
                        modifier = Modifier.weight(1f)
                    )
                    NetworkTrafficCard(
                        label = "Transmitted",
                        value = "$networkOut KB/s",
                        total = "187.3 GB",
                        color = BitcoinOrange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Best Block Hash
        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Best Block Hash",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Badge(
                        text = "Height: 834,527",
                        color = BlockGreen
                    )
                }
                Spacer(Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = DarkBackground
                ) {
                    Text(
                        "00000000000000000002a7c4c1e48d76c5a37902165a270156b7a8d72728a054",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = BitcoinOrange
                    )
                }
            }
        }

        // Peer Map
        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.People, null, tint = BitcoinOrange)
                        Text(
                            "Connected Peers",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BlockGreen)
                            )
                            Text(
                                "BIP-110: ${peers.count { it.bip110 }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        // Peer Cards
        items(peers) { peer ->
            PeerCard(peer)
        }

        // Bottom spacing
        item {
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = valueColor
        )
    }
}

@Composable
private fun MetricCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    StatsCard(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = BitcoinOrange,
                modifier = Modifier.size(16.dp)
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun NetworkTrafficCard(
    label: String,
    value: String,
    total: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = FontFamily.Monospace,
                color = color
            )
            Text(
                "Total: $total",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PeerCard(peer: PeerInfo) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = DarkBackground,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    peer.ip,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace
                )
                Badge(
                    text = if (peer.inbound) "IN" else "OUT",
                    color = if (peer.inbound) Color(0xFF4A90E2) else Color(0xFF9B59B6)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (peer.taproot) {
                    Badge(text = "Bit 2", color = BitcoinOrange)
                }
                if (peer.bip110) {
                    Badge(text = "Bit 4", color = BlockGreen)
                }
            }
            Text(
                "Ping: ${peer.ping}ms",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
