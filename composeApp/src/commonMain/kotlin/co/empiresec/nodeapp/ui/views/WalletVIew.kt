package co.empiresec.nodeapp.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
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
fun WalletView() {
    var showBalance by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.AccountBalanceWallet, null, tint = BitcoinOrange)
                        Text("Portfolio Overview", style = MaterialTheme.typography.titleMedium)
                    }
                    IconButton(onClick = { showBalance = !showBalance }) {
                        Icon(
                            if (showBalance) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle balance visibility"
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                if (showBalance) {
                    Column {
                        Text(
                            "₿ 2.63621347",
                            style = MaterialTheme.typography.displayMedium,
                            fontFamily = FontFamily.Monospace,
                            color = BitcoinOrange
                        )
                        Text(
                            "≈ $235,234.56 USD",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Text(
                        "••••••••",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WalletStat("BTC Price", "$89,234.56", BlockGreen, Modifier.weight(1f))
                WalletStat("Total Wallets", "3", MaterialTheme.colorScheme.onSurface, Modifier.weight(1f))
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Text("Your Wallets", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                WalletItem("Main Wallet", "Segwit", 0.45821347, 87)
                Spacer(Modifier.height(8.dp))
                WalletItem("Cold Storage", "Native Segwit", 2.15000000, 15)
                Spacer(Modifier.height(8.dp))
                WalletItem("Lightning Wallet", "Lightning", 0.01800000, 234)
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent Transactions", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
                Spacer(Modifier.height(12.dp))
                TransactionItem("Received", 0.05234567, "6 conf", true)
                Spacer(Modifier.height(8.dp))
                TransactionItem("Sent", 0.12000000, "12 conf", false)
                Spacer(Modifier.height(8.dp))
                TransactionItem("Received", 0.00500000, "1 conf", true)
            }
        }

        item {
            StatsCard(modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.Security, null, tint = BlockGreen)
                    Text("Security Status", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SecurityBadge("Encryption", "ENABLED", BlockGreen, Modifier.weight(1f))
                    SecurityBadge("Backup", "RECENT", BlockGreen, Modifier.weight(1f))
                    SecurityBadge("2FA", "OPTIONAL", BitcoinOrange, Modifier.weight(1f))
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun WalletStat(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    StatsCard(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.Monospace, color = color)
    }
}

@Composable
private fun WalletItem(name: String, type: String, balance: Double, txCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.titleSmall)
            Text(type, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("₿ %.8f".format(balance), fontFamily = FontFamily.Monospace, color = BitcoinOrange)
            Text("$txCount tx", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TransactionItem(type: String, amount: Double, confirmations: String, isReceived: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(type, style = MaterialTheme.typography.titleSmall)
            Badge(text = confirmations, color = if (confirmations.contains("6")) BlockGreen else BitcoinOrange)
        }
        Text(
            "${if (isReceived) "+" else "-"}₿ %.8f".format(amount),
            fontFamily = FontFamily.Monospace,
            color = if (isReceived) BlockGreen else BitcoinOrange
        )
    }
}

@Composable
private fun SecurityBadge(label: String, status: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Badge(text = status, color = color)
    }
}
