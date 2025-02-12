package cn.peyriat.koola.ui.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState

object SnackbarManager {
    val snackbarHostState = SnackbarHostState()

    suspend fun showSnackbar(message: String, actionLabel: String? = null) {
        snackbarHostState.currentSnackbarData?.dismiss()
        snackbarHostState.showSnackbar(message, actionLabel, duration = SnackbarDuration.Short)
    }
}
