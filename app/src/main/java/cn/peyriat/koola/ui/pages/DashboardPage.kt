package cn.peyriat.koola.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardPage() {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        Text(
            "Home",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Icon(
                    Icons.Rounded.Home,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Dashboard",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "那我问你，你是男的女的？如果你是女的你说这样的话，啊，那我问你，你你你是女孩子，那我问你，那那你那头顶是不是不是尖的？那我那我问你，你头顶是尖的呢？还是秃顶的，啊，还是，啊，染黄色染红色的，那我问你，啊，还是戴假发的，如果是如果你是男的那我问你，啊，你说我的头是尖的，那我问你那你是不是秃头？那你是不是光头？啊？你是光头还是有头发的，啊？那我问你，我头顶，我我头尖怎么了？我就尖怎么了？哎，我就尖怎么了？我就头顶尖怎么了？哎，我头顶尖你难道你看不惯么？我头顶就是尖的怎么了？我就是尖，我就是要尖怎么了，啊，你看不惯吗，啊？",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}