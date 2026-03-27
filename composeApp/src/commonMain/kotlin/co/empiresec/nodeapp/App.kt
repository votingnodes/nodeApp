package co.empirsec.nodeapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import co.empiresec.nodeapp.nav.NodeAppNavigation
import co.empiresec.nodeapp.ui.theme.NodeAppTheme

@Composable
fun App() {
    NodeAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            NodeAppNavigation()
        }
    }
}