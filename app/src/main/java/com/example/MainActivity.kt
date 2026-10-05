package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.dialogs.AutoCaptionSheet
import com.example.ui.dialogs.CanvasSheet
import com.example.ui.dialogs.ExportDialog
import com.example.ui.dialogs.SpeedSheet
import com.example.ui.dialogs.StickerSheet
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MainEditorScreen
import com.example.ui.screens.MediaEffectsScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.QuranEditorScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TextCaptionEditorScreen
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranVideoEditorTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.EditorModal
import com.example.ui.viewmodel.EditorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: EditorViewModel = viewModel()
            val settings by viewModel.settings.collectAsStateWithLifecycle()

            QuranVideoEditorTheme(
                darkTheme = settings.isDarkTheme,
                accentColor = QuranGold
            ) {
                QuranVideoEditorApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun QuranVideoEditorApp(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val activeModal by viewModel.activeModal.collectAsStateWithLifecycle()
    val activeBottomTool by viewModel.activeBottomTool.collectAsStateWithLifecycle()

    // Handle system back navigation gracefully
    BackHandler(enabled = activeModal != EditorModal.NONE || activeBottomTool != com.example.ui.viewmodel.BottomToolType.TIMELINE || currentScreen != AppScreen.HOME) {
        if (activeModal != EditorModal.NONE) {
            viewModel.closeModal()
        } else if (activeBottomTool != com.example.ui.viewmodel.BottomToolType.TIMELINE) {
            viewModel.closeBottomTool()
        } else {
            when (currentScreen) {
                AppScreen.QURAN_EDITOR,
                AppScreen.TEXT_EDITOR,
                AppScreen.MEDIA_EFFECTS -> viewModel.navigateTo(AppScreen.MAIN_EDITOR)

                AppScreen.MAIN_EDITOR,
                AppScreen.PROJECTS,
                AppScreen.SETTINGS -> viewModel.navigateTo(AppScreen.HOME)

                AppScreen.HOME -> { /* let system exit */ }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
            when (screen) {
                AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                AppScreen.PROJECTS -> ProjectsScreen(viewModel = viewModel)
                AppScreen.MAIN_EDITOR -> MainEditorScreen(viewModel = viewModel)
                AppScreen.QURAN_EDITOR -> QuranEditorScreen(viewModel = viewModel)
                AppScreen.TEXT_EDITOR -> TextCaptionEditorScreen(viewModel = viewModel)
                AppScreen.MEDIA_EFFECTS -> MediaEffectsScreen(viewModel = viewModel)
                AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }

        // Active Modals & Dialogs
        when (activeModal) {
            EditorModal.EXPORT -> ExportDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.dismissExport() }
            )
            EditorModal.CANVAS -> CanvasSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.closeModal() }
            )
            EditorModal.SPEED -> SpeedSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.closeModal() }
            )
            EditorModal.AUTO_CAPTION -> AutoCaptionSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.closeModal() }
            )
            EditorModal.STICKER -> StickerSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.closeModal() }
            )
            EditorModal.VIDEO -> SpeedSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.closeModal() }
            )
            EditorModal.NONE -> { /* No modal */ }
        }
    }
}
