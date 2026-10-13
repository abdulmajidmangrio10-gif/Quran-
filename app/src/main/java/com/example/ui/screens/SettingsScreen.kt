package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardLighter
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.QuranGold
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.EditorViewModel

@Composable
fun SettingsScreen(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    // Language Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = DarkCard,
            title = {
                Text("Select Language / زبان منتخب کریں", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("English", "اردو (Urdu)", "العربية (Arabic)").forEach { lang ->
                        val isSelected = settings.language.startsWith(lang.take(4))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) NeonPurple.copy(alpha = 0.2f) else DarkCardLighter)
                                .border(1.dp, if (isSelected) NeonPurple else DarkBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    val code = when {
                                        lang.startsWith("English") -> "English"
                                        lang.startsWith("اردو") -> "Urdu"
                                        else -> "Arabic"
                                    }
                                    viewModel.updateSettings(language = code)
                                    showLanguageDialog = false
                                }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(lang, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            if (isSelected) {
                                Text("Active", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close", color = TextGray)
                }
            }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = DarkCard,
            title = {
                Text("Privacy & Data Policy", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("1. 100% Local Processing", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("All your video edits, audio clips, and Quran recitations remain strictly on your device. No video is uploaded to external cloud servers.", color = TextGray, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("2. Media Permissions", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Photo and video access is used exclusively when you choose to import files for your timeline projects.", color = TextGray, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("3. Holy Quran Integrity", color = QuranGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("All 114 Surahs and Arabic Ayahs are bundled directly in the application database for authentic, offline recitation styling.", color = TextGray, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Text("Understood", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Help Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            containerColor = DarkCard,
            title = {
                Text("Help & Feedback", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Need help editing videos or styling Quran Ayahs?", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("• Tap + New Project to pick video aspect ratio (9:16 for Reels/Shorts, 16:9 for YouTube).", color = TextGray, fontSize = 12.sp)
                    Text("• Use 'Quran' tool to insert authentic Ayahs with Urdu translations and calligraphy.", color = TextGray, fontSize = 12.sp)
                    Text("• Tap 'Export' at any time to render in Full HD (1080p, 60fps).", color = TextGray, fontSize = 12.sp)
                    Text("Developed by Hafiz Abdul Majid Mangrew", color = QuranGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showHelpDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Text("Done", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        bottomBar = {
            HomeBottomNav(
                currentScreen = currentScreen,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Settings",
                    color = TextWhite,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // =================================================================
            // SCREEN 24: SETTINGS ROWS
            // =================================================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCard)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Preferences", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    // 1. Appearance (Dark Theme / Light Mode toggle)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(NeonPurple.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DarkMode, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Appearance", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(if (settings.isDarkTheme) "Dark Mode (Enabled)" else "Light Mode", color = TextGray, fontSize = 11.sp)
                            }
                        }

                        Switch(
                            checked = settings.isDarkTheme,
                            onCheckedChange = { viewModel.updateSettings(isDarkTheme = it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TextWhite,
                                checkedTrackColor = NeonPurple,
                                uncheckedThumbColor = TextGray,
                                uncheckedTrackColor = DarkCardLighter
                            )
                        )
                    }

                    // 2. Language
                    SettingsInteractiveRow(
                        icon = Icons.Default.Language,
                        iconTint = NeonCyan,
                        title = "Language",
                        subtitle = settings.language,
                        onClick = { showLanguageDialog = true }
                    )

                    // 3. Storage & Cache
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Storage, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Storage & Cache", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text("Temporary clip buffers", color = TextGray, fontSize = 11.sp)
                            }
                        }

                        Button(
                            onClick = { viewModel.clearAppCache() },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCardLighter, contentColor = NeonCyan),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // 4. Notifications
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF59E0B).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Notifications", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text("Export completion alerts", color = TextGray, fontSize = 11.sp)
                            }
                        }

                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TextWhite,
                                checkedTrackColor = NeonPurple,
                                uncheckedThumbColor = TextGray,
                                uncheckedTrackColor = DarkCardLighter
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Support & About Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCard)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Support & Legal", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    // Privacy Policy
                    SettingsInteractiveRow(
                        icon = Icons.Default.PrivacyTip,
                        iconTint = NeonPurple,
                        title = "Privacy Policy",
                        subtitle = "Offline-first data security",
                        onClick = { showPrivacyDialog = true }
                    )

                    // Help & Feedback
                    SettingsInteractiveRow(
                        icon = Icons.Default.HelpOutline,
                        iconTint = NeonCyan,
                        title = "Help & Feedback",
                        subtitle = "Tips and developer contact",
                        onClick = { showHelpDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Creator & App Branding Card (Matching Screen 25)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, QuranGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCard)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(QuranGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = QuranGold, modifier = Modifier.size(28.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Quran & Video Pro Editor", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Version 2.0 • Pro Suite", color = NeonCyan, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Designed & Built by Hafiz Abdul Majid Mangrew\nComplete Video Studio with 114 Quran Surahs, timeline synchronization, Urdu translation overlays, audio tools, and studio filters.",
                        color = TextGray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingsInteractiveRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = TextGray, fontSize = 11.sp)
            }
        }

        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
    }
}
