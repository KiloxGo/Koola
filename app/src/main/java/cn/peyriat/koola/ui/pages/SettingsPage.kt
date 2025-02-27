package cn.peyriat.koola.ui.pages

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.peyriat.koola.ui.CONFIG
import cn.peyriat.koola.ui.snackbar.SnackbarManager
import cn.peyriat.koola.util.saveConfig
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun SettingsPage(context: Context) {
    val scrollState = rememberScrollState()
    var test1 by remember { mutableStateOf(CONFIG.getBoolean("Test1")) }
    var test2 by remember { mutableStateOf(CONFIG.getBoolean("Test2")) }
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        SettingItem(
            label = "TestSettings1",
            description = "TestDescription1",
            configName = "Test1",
            isChecked = test1,
            onCheckedChange = { test1 = it },
            context = context
        )

        SettingItem(
            label = "TestSettings2",
            description = "TestDescription2",
            configName = "Test2",
            isChecked = test2,
            onCheckedChange = { test2 = it },
            context = context
        )

    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun SettingItem(label: String, description: String, configName: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit, context: Context) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 1.dp)
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = { state ->
                onCheckedChange(state)
                CONFIG.put(configName, state)
                saveConfig(context, CONFIG)
                GlobalScope.launch {
                    SnackbarManager.showSnackbar("$label has been ${if (state) "enabled" else "disabled"}.")
                }
            }
        )
    }
}