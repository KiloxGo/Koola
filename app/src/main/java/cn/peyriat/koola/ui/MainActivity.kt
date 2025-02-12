package cn.peyriat.koola.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.peyriat.koola.ui.pages.DashboardPage
import cn.peyriat.koola.ui.pages.SettingsPage
import cn.peyriat.koola.ui.snackbar.SnackbarManager
import cn.peyriat.koola.ui.theme.ApplicationTheme
import cn.peyriat.koola.util.mergeConfig
import cn.peyriat.koola.util.saveConfig
import org.json.JSONObject

var CONFIG = JSONObject().apply {
    put("Test1", false)
    put("Test2", true)
}

class MainActivity : ComponentActivity() {

    private val items = listOf(
        NavigationItem("Home", Icons.Rounded.Home) { DashboardPage() },
        NavigationItem("Settings", Icons.Rounded.Settings) { SettingsPage(this) },
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        runCatching {
            mergeConfig(this).let {
                if (it == null)
                    saveConfig(this, CONFIG)
                else
                    CONFIG = it
            }
        }.onFailure {
            saveConfig(this, CONFIG)
        }

        setContent {
            var selectedItem by remember { mutableStateOf(items[0]) }

            val windowShape = RoundedCornerShape(12.dp)
            ApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = windowShape,
                    color = MaterialTheme.colorScheme.background.copy(alpha = 1F),
                ) {
                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(
                                hostState = SnackbarManager.snackbarHostState,
                                snackbar = {
                                    Snackbar(
                                        snackbarData = it,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            )
                        },
                        bottomBar = {
                            Card(
                                modifier = Modifier
                                    .padding(start = 80.dp, bottom = 16.dp)
                                    .height(72.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    items.forEach { item ->
                                        NavigationRailItem(
                                            selected = selectedItem == item,
                                            onClick = { selectedItem = item },
                                            icon = {
                                                Icon(
                                                    item.icon,
                                                    contentDescription = item.title,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            },
                                            label = { Text(item.title) },
                                            colors = NavigationRailItemDefaults.colors(
                                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    ) { padding ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 1.dp,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Box(modifier = Modifier.padding(24.dp)) {
                                    AnimatedContent(
                                        targetState = selectedItem,
                                        transitionSpec = {
                                            fadeIn(animationSpec = tween(300)).plus(
                                                scaleIn(
                                                    animationSpec = tween(300),
                                                    initialScale = 0.95F
                                                )
                                            ).togetherWith(
                                                fadeOut(animationSpec = tween(300)) + scaleOut(
                                                    animationSpec = tween(300),
                                                    targetScale = 1.05F
                                                )
                                            )
                                        }
                                    ) { item ->
                                        item.screen()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}