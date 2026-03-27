package co.empiresec.nodeapp.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

data class LightningChannel(
    val peer: String,
    val capacity: Long,
    val localBalance: Long,
    val remoteBalance: Long,
    val status: String,
    val uptime: Float
)

@Composable
fun VaultView() {
    val channels = remember {
        listOf(
            LightningChannel("034a5e...7f2d", 5_000_000, 3_200_000, 1_800_000, "active", 99.8f),
            LightningChannel("02f3b1...9e4c", 2_000_000, 500_000, 1_500_000, "active", 98.5f),
            LightningChannel("03c9d8...1a7b", 10_000_000, 8_500_000, 1_500_000, "active", 99.9f),
            LightningChannel("02a1f7...8c3e", 1_000_000, 750_000, 250_000, "pending", 0f)
        )
    }

    val totalCapacity = channels.sumOf { it.capacity }
    val totalLocal = channels.sumOf { it.localBalance }
    val totalRemote = channels.sumOf { it.remoteBalance }

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
                    Icon(Icons.Default.Bolt, null, tint = BitcoinOrange)
                    Text("Lightning Network Daemon", style = MaterialTheme.typography.titleMedium)
                    Badge(text = "SYNCED", color = BlockGreen)
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LndStat("Total Capacity", formatSats(totalCapacity), Modifier.weight(1f))
                    LndStat("Active Channels", "${channels.count { it.status == "active" }}", Modifier.weight(1f))
                    LndStat("Fees Earned", "100K sats", Modifier.weight(1f))
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PaymentStat("Total Sent", "180.0M", BitcoinOrange, Modifier.weight(1f))
                PaymentStat("Total Received", "104.0M", BlockGreen, Modifier.weight(1f))
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Text("Channel Health", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))

                // Aggregate Balance
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Aggregate Balance", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Local: ${formatSats(totalLocal)} / Remote: ${formatSats(totalRemote)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth().height(24.dp)) {
                        Surface(
                            modifier = Modifier
                                .weight(totalLocal.toFloat() / totalCapacity)
                                .fillMaxHeight(),
                            color = BlockGreen
                        ) {}
                        Surface(
                            modifier = Modifier
                                .weight(totalRemote.toFloat() / totalCapacity)
                                .fillMaxHeight(),
                            color = BitcoinOrange
                        ) {}
                    }
                }
            }
        }

        items(channels) { channel ->
            ChannelCard(channel)
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Wifi, null, tint = BlockGreen)
                        Text("Backend Link (Electrum → Floresta)", style = MaterialTheme.typography.titleMedium)
                    }
                    Badge(text = "CONNECTED", color = BlockGreen)
                }
                Spacer(Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoRow("Endpoint", "127.0.0.1:50001")
                    InfoRow("Latency", "12ms")
                    InfoRow("LND Block Height", "834,527")
                    InfoRow("Floresta Height", "834,527")
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun LndStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun PaymentStat(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    StatsCard(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.headlineLarge, fontFamily = FontFamily.Monospace, color = color)
        Text("sats", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ChannelCard(channel: LightningChannel) {
    StatsCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(channel.peer, fontFamily = FontFamily.Monospace)
            Badge(
                text = channel.status.uppercase(),
                color = if (channel.status == "active") BlockGreen else BitcoinOrange
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().height(16.dp)) {
            val localPercent = channel.localBalance.toFloat() / channel.capacity
            val remotePercent = channel.remoteBalance.toFloat() / channel.capacity
            Surface(
                modifier = Modifier.weight(localPercent).fillMaxHeight(),
                color = BlockGreen
            ) {}
            Surface(
                modifier = Modifier.weight(remotePercent).fillMaxHeight(),
                color = BitcoinOrange
            ) {}
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Local: ${formatSats(channel.localBalance)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Remote: ${formatSats(channel.remoteBalance)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (channel.status == "active") {
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Uptime: ${channel.uptime}%", style = MaterialTheme.typography.bodySmall)
                Text("Capacity: ${formatSats(channel.capacity)}", style = MaterialTheme.typography.bodySmall)
            }
        }
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

private fun formatSats(sats: Long): String {
    return when {
        sats >= 1_000_000 -> "%.2fM".format(sats / 1_000_000.0)
        sats >= 1_000 -> "%.1fK".format(sats / 1_000.0)
        else -> sats.toString()
    }
}
