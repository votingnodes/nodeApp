package co.empiresec.nodeapp.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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

@Composable
fun ExplorerView() {
    var searchQuery by remember { mutableStateOf("") }

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
                    Icon(Icons.Default.Search, null, tint = BitcoinOrange)
                    Text("Blockchain Explorer", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search by block height, hash, transaction ID, or address...") },
                    trailingIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NetworkStat("Latest Block", "834,527", "2 min ago", Modifier.weight(1f))
                NetworkStat("Avg Block Time", "9.8 min", "Last 6 blocks", Modifier.weight(1f))
                NetworkStat("Mempool Size", "47.8k", "89.4 MB", Modifier.weight(1f))
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
                        Icon(Icons.Default.Cloud, null, tint = BlockGreen)
                        Text("ElectrumX Server Statistics", style = MaterialTheme.typography.titleMedium)
                    }
                    Badge(text = "ONLINE", color = BlockGreen)
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ElectrumStat("Connections", "1,247", Modifier.weight(1f))
                    ElectrumStat("Requests/sec", "23.4", Modifier.weight(1f))
                    ElectrumStat("Response Time", "12.3ms", Modifier.weight(1f))
                    ElectrumStat("Cache Hit", "87.6%", Modifier.weight(1f))
                }
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Text("Recent Blocks", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                BlockItem("834527", "2 min ago", 2847, 1.32f, "Foundry USA")
                Spacer(Modifier.height(8.dp))
                BlockItem("834526", "12 min ago", 3124, 1.45f, "AntPool")
                Spacer(Modifier.height(8.dp))
                BlockItem("834525", "23 min ago", 2956, 1.38f, "F2Pool")
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Text("Large Transactions", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                TransactionItem("a1b2c3d4e5f6...", 457.89234567, "5 min ago")
                Spacer(Modifier.height(8.dp))
                TransactionItem("f9e8d7c6b5a4...", 312.45678901, "18 min ago")
                Spacer(Modifier.height(8.dp))
                TransactionItem("1234567890ab...", 198.76543210, "42 min ago")
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun NetworkStat(label: String, value: String, subtitle: String, modifier: Modifier = Modifier) {
    StatsCard(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.Monospace)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ElectrumStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontFamily = FontFamily.Monospace, color = BitcoinOrange)
    }
}

@Composable
private fun BlockItem(height: String, time: String, txCount: Int, size: Float, miner: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("#$height", fontFamily = FontFamily.Monospace)
                Badge(text = miner, color = BlockGreen)
            }
            Text("$txCount tx · ${size} MB", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun TransactionItem(txid: String, amount: Double, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(txid, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace)
            Text(time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text("₿ %.8f".format(amount), fontFamily = FontFamily.Monospace, color = BitcoinOrange)
    }
}
