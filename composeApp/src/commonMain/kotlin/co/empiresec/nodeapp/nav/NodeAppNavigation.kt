package co.empiresec.nodeapp.nav

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import co.empiresec.nodeapp.ui.components.ConsensusSplitDialog
import co.empiresec.nodeapp.ui.views.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Pulse : Screen("pulse", "Pulse", Icons.Default.Home)
    object Forest : Screen("forest", "Forest", Icons.Default.AccountTree)
    object Vault : Screen("vault", "Vault", Icons.Default.Lock)
    object Wallet : Screen("wallet", "Wallet", Icons.Default.AccountBalanceWallet)
    object Explorer : Screen("explorer", "Explorer", Icons.Default.Search)
    object Monitor : Screen("monitor", "Monitor", Icons.Default.BarChart)
    object Security : Screen("security", "Security", Icons.Default.Security)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NodeAppNavigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Pulse) }
    var showConsensusDialog by remember { mutableStateOf(false) }

    val screens = listOf(
        Screen.Pulse,
        Screen.Forest,
        Screen.Vault,
        Screen.Wallet,
        Screen.Explorer,
        Screen.Monitor,
        Screen.Security
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Column {
                            Text(
                                "SOVEREIGN NODE",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                "@securitybrahh",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                },
                actions = {
                    OutlinedButton(
                        onClick = { showConsensusDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Watchdog Test")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = screens.indexOf(currentScreen),
                containerColor = MaterialTheme.colorScheme.background,
                edgePadding = 16.dp,
                divider = {}
            ) {
                screens.forEach { screen ->
                    Tab(
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen },
                        text = { Text(screen.title) },
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            // Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (currentScreen) {
                    Screen.Pulse -> PulseView()
                    Screen.Forest -> ForestView()
                    Screen.Vault -> VaultView()
                    Screen.Wallet -> WalletView()
                    Screen.Explorer -> ExplorerView()
                    Screen.Monitor -> MonitorView()
                    Screen.Security -> SecurityView()
                }
            }
        }
    }

    if (showConsensusDialog) {
        ConsensusSplitDialog(
            onDismiss = { showConsensusDialog = false }
        )
    }
}
