package com.example.ui.dialogs

import android.content.Intent
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AspectRatioOption
import com.example.data.model.CanvasBgType
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardLighter
import com.example.ui.theme.QuranGold
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.EditorViewModel

@Composable
fun ExportDialog(
    viewModel: EditorViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isExporting by viewModel.isExporting.collectAsStateWithLifecycle()
    val exportProgress by viewModel.exportProgress.collectAsStateWithLifecycle()
    val exportStage by viewModel.exportStage.collectAsStateWithLifecycle()
    val isComplete by viewModel.exportCompleted.collectAsStateWithLifecycle()
    val project by viewModel.currentProject.collectAsStateWithLifecycle()

    var selectedResolution by remember { mutableStateOf("1080p (FHD)") }
    var selectedFps by remember { mutableStateOf(30) }
    var selectedFormat by remember { mutableStateOf("MP4") }

    val durSec = (project.durationMs / 1000).coerceAtLeast(1)
    val estimatedMb = when (selectedResolution) {
        "480p" -> String.format(java.util.Locale.US, "%.1f MB", durSec * 0.4f)
        "720p" -> String.format(java.util.Locale.US, "%.1f MB", durSec * 0.8f)
        "4K (UHD)" -> String.format(java.util.Locale.US, "%.1f MB", durSec * 3.8f)
        else -> String.format(java.util.Locale.US, "%.1f MB", durSec * 1.5f)
    }

    Dialog(
        onDismissRequest = { if (!isExporting) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, com.example.ui.theme.DarkBorder, RoundedCornerShape(24.dp)),
            color = com.example.ui.theme.DarkCard
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isComplete) "Video Saved!" else if (isExporting) "Export Progress" else "Export Settings",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (!isExporting) {
                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isComplete) {
                    // =========================================================
                    // SCREEN 21 & 22: VIDEO SAVED & SHARE SCREEN
                    // =========================================================
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(com.example.ui.theme.NeonGreen.copy(alpha = 0.15f))
                            .border(2.dp, com.example.ui.theme.NeonGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = com.example.ui.theme.NeonGreen,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Video Saved to Gallery!",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${project.title} • $selectedResolution • $selectedFps FPS",
                        color = TextGray,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Play Video Button
                    Button(
                        onClick = {
                            viewModel.togglePlayPause()
                            Toast.makeText(context, "Playing project preview", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.NeonPurple)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Play Video", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Social Share Row (WhatsApp, YouTube, Instagram, Facebook)
                    Text("Share to Social Media", color = TextGray, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val shareApps = listOf(
                            Pair("WhatsApp", Color(0xFF25D366)),
                            Pair("YouTube", Color(0xFFFF0000)),
                            Pair("Instagram", Color(0xFFE1306C)),
                            Pair("Facebook", Color(0xFF1877F2))
                        )

                        shareApps.forEach { (appName, appColor) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, project.title)
                                        putExtra(Intent.EXTRA_TEXT, "Watch my video: ${project.title} created with Video Editor.")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share via $appName"))
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(appColor.copy(alpha = 0.2f))
                                        .border(1.dp, appColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = appName, tint = appColor, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(appName, color = TextWhite, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGray)
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }

                } else if (isExporting) {
                    // =========================================================
                    // SCREEN 20: EXPORT PROGRESS (Circular percentage ring)
                    // =========================================================
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { exportProgress / 100f },
                            modifier = Modifier.fillMaxSize(),
                            color = com.example.ui.theme.NeonPurple,
                            trackColor = com.example.ui.theme.DarkCardLighter,
                            strokeWidth = 8.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${exportProgress.toInt()}%",
                                color = TextWhite,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = exportStage,
                        color = com.example.ui.theme.NeonCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Rendering video frames, Quran calligraphy & audio waveforms...",
                        color = TextGray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedButton(
                        onClick = { viewModel.dismissExport() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }

                } else {
                    // =========================================================
                    // SCREEN 18: EXPORT SETTINGS FORM
                    // =========================================================
                    // 1. Resolution
                    Text(
                        text = "Resolution",
                        color = TextWhite,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("480p", "720p", "1080p (FHD)", "4K (UHD)").forEach { res ->
                            val isSel = selectedResolution == res
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) com.example.ui.theme.NeonPurple else com.example.ui.theme.DarkCardLighter)
                                    .border(1.dp, if (isSel) com.example.ui.theme.NeonCyan else com.example.ui.theme.DarkBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedResolution = res }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = res,
                                    color = TextWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Frame Rate
                    Text(
                        text = "Frame Rate",
                        color = TextWhite,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(24, 30, 60).forEach { fps ->
                            val isSel = selectedFps == fps
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) com.example.ui.theme.NeonPurple else com.example.ui.theme.DarkCardLighter)
                                    .border(1.dp, if (isSel) com.example.ui.theme.NeonCyan else com.example.ui.theme.DarkBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedFps = fps }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$fps fps",
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Format & Size Info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(com.example.ui.theme.DarkCardLighter)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Format: $selectedFormat", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Estimated Size: $estimatedMb", color = com.example.ui.theme.NeonCyan, fontSize = 11.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(com.example.ui.theme.NeonPurple.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("High Quality", color = com.example.ui.theme.NeonPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Export Button with Purple Gradient
                    Button(
                        onClick = {
                            viewModel.startExport(selectedResolution, selectedFps, "High")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("start_export_button"),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.NeonPurple)
                    ) {
                        Text("Export Video", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextWhite)
                    }
                }
            }
        }
    }
}

@Composable
fun CanvasSheet(
    viewModel: EditorViewModel,
    onDismiss: () -> Unit
) {
    val project by viewModel.currentProject.collectAsStateWithLifecycle()
    val canvas = project.canvas

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(20.dp)),
            color = DarkCard
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Canvas Settings", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Aspect Ratio", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AspectRatioOption.values().forEach { opt ->
                        val isSel = canvas.aspectRatio == opt
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) QuranGold else DarkCardLighter)
                                .clickable { viewModel.setCanvasRatio(opt) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt.label,
                                color = if (isSel) TextDark else TextWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Background Style", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (canvas.bgType == CanvasBgType.BLUR) QuranGold else DarkCardLighter)
                            .clickable { viewModel.setCanvasBackground(CanvasBgType.BLUR) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Blur Nature", color = if (canvas.bgType == CanvasBgType.BLUR) TextDark else TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (canvas.bgType == CanvasBgType.COLOR) QuranGold else DarkCardLighter)
                            .clickable { viewModel.setCanvasBackground(CanvasBgType.COLOR) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Charcoal Black", color = if (canvas.bgType == CanvasBgType.COLOR) TextDark else TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QuranGold, contentColor = TextDark)
                ) {
                    Text("Apply Canvas", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SpeedSheet(
    viewModel: EditorViewModel,
    onDismiss: () -> Unit
) {
    val project by viewModel.currentProject.collectAsStateWithLifecycle()
    val currentSpeed = project.videoConfig.speed

    val speeds = listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 3.0f)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(20.dp)),
            color = DarkCard
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Video Playback Speed", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(speeds) { spd ->
                        val isSel = currentSpeed == spd
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) QuranGold else DarkCardLighter)
                                .clickable { viewModel.setVideoSpeed(spd) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${spd}x",
                                color = if (isSel) TextDark else TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QuranGold, contentColor = TextDark)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StickerSheet(
    viewModel: EditorViewModel,
    onDismiss: () -> Unit
) {
    val stickers = listOf(
        "﷽" to "Bismillah",
        "۞" to "Hizb Quarter",
        "۝" to "Ayah Ornament",
        "ﷲ" to "Allah",
        "ﷺ" to "Prophet Muhammad",
        "🌙" to "Crescent Moon",
        "⭐" to "Star",
        "🕌" to "Mosque",
        "📖" to "Holy Quran",
        "✨" to "Sparkles",
        "🤲" to "Dua Prayer",
        "🕊️" to "Peace"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(20.dp)),
            color = DarkCard
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Islamic Stickers & Motifs", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(stickers) { (symbol, name) ->
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkCardLighter)
                                .clickable {
                                    viewModel.addSticker(symbol)
                                    onDismiss()
                                }
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = symbol, fontSize = 26.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = name, color = TextGray, fontSize = 9.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
