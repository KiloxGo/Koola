package cn.peyriat.koola.util

import android.content.Context
import cn.peyriat.koola.ui.CONFIG
import org.json.JSONObject
import java.io.FileInputStream

fun readConfig(context: Context): JSONObject? {
    runCatching {
        val inputStream: FileInputStream = context.openFileInput("koola-config.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return JSONObject(jsonString)
    }.onFailure {
        return null
    }
    return null
}

fun mergeConfig(context: Context): JSONObject? {
    val savedConfig = readConfig(context) ?: return null
    for (key in CONFIG.keys()) {
        if (!savedConfig.has(key)) {
            savedConfig.put(key, CONFIG.get(key))
        }
    }
    return savedConfig
}


fun saveConfig(context: Context, config: JSONObject) {
    context.openFileOutput("koola-config.json", Context.MODE_PRIVATE).use { outputStream ->
        outputStream.write(config.toString().toByteArray())
    }
}
