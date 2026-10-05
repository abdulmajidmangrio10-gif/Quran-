package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EditingProject
import com.example.data.model.EffectType
import com.example.data.model.FilterType
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardLighter
import com.example.ui.theme.PlayheadRed
import com.example.ui.theme.QuranGold
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.theme.TrackAudioBg
import com.example.ui.theme.TrackAudioClip
import com.example.ui.theme.TrackQuranBg
import com.example.ui.theme.TrackQuranClip
import com.example.ui.theme.TrackTextBg
import com.example.ui.theme.TrackTextClip
import com.example.ui.theme.TrackVideoBg
import com.example.ui.theme.TrackVideoClip
import com.example.ui.theme.TrackWaveform
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BottomToolType
import com.example.ui.viewmodel.EditorModal
import com.example.ui.viewmodel.EditorViewModel
import com.example.ui.viewmodel.ToolbarFeatureItem
import com.example.ui.viewmodel.TrackType

@Composable
fun MainEditorScreen(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.currentProject.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentPlayheadMs by viewModel.currentPlayheadMs.collectAsStateWithLifecycle()
    val isMuted by viewModel.isMuted.collectAsStateWithLifecycle()
    val selectedTrack by viewModel.selectedTrack.collectAsStateWithLifecycle()
    val selectedClipId by viewModel.selectedClipId.collectAsStateWithLifecycle()
    val timelineZoom by viewModel.timelineZoom.collectAsStateWithLifecycle()
    val activeBottomTool by viewModel.activeBottomTool.collectAsStateWithLifecycle()
    val toolbarFeatures by viewModel.toolbarFeatures.collectAsStateWithLifecycle()

    val totalDurationMs = project.durationMs.coerceAtLeast(1000L)
    val formatTime = { ms: Long ->
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        String.format("%02d:%02d", min, sec)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        bottomBar = {
            EditorCustomizableBottomToolbar(
                features = toolbarFeatures,
                activeTool = activeBottomTool,
                onToolClick = { toolId ->
                    when (toolId) {
                        "timeline" -> viewModel.closeBottomTool()
                        "quran" -> viewModel.openBottomTool(BottomToolType.QURAN)
                        "text" -> viewModel.openBottomTool(BottomToolType.TEXT)
                        "adjust" -> viewModel.openBottomTool(BottomToolType.ADJUST)
                        "filter" -> viewModel.openBottomTool(BottomToolType.FILTER)
                        "effects" -> viewModel.openBottomTool(BottomToolType.EFFECTS)
                        "canvas" -> viewModel.openBottomTool(BottomToolType.CANVAS)
                        "audio" -> viewModel.openBottomTool(BottomToolType.AUDIO)
                        "speed" -> viewModel.openBottomTool(BottomToolType.SPEED)
                        "sticker" -> viewModel.openBottomTool(BottomToolType.STICKER)
                        "autocaption" -> viewModel.openBottomTool(BottomToolType.AUTO_CAPTION)
                    }
                },
                onCustomizeClick = {
                    viewModel.openBottomTool(BottomToolType.CUSTOMIZE_TOOLBAR)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            // Top Action Toolbar
            EditorTopBar(
                onBack = { viewModel.navigateTo(AppScreen.HOME) },
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() },
                onCustomizeTools = { viewModel.openBottomTool(BottomToolType.CUSTOMIZE_TOOLBAR) },
                onExport = { viewModel.openModal(EditorModal.EXPORT) }
            )

            // Video Preview Canvas Area (ALWAYS VISIBLE AT TOP, NEVER BLOCKED)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                VideoPreviewCard(
                    project = project,
                    currentPlayheadMs = currentPlayheadMs,
                    isPlaying = isPlaying,
                    isMuted = isMuted,
                    timeFormatted = "${formatTime(currentPlayheadMs)} / ${formatTime(totalDurationMs)}",
                    onTogglePlay = { viewModel.togglePlayPause() },
                    onToggleMute = { viewModel.toggleMute() },
                    onToggleFullscreen = { viewModel.toggleFullscreen() },
                    onTapOverlay = { viewModel.openBottomTool(BottomToolType.QURAN) }
                )
            }

            // Inline Editing Panel Area (Below the Video, Above the Toolbar)
            // When user opens any tool, it stays BELOW the video so they can watch changes live!
            Crossfade(
                targetState = activeBottomTool,
                label = "BottomToolPanelTransition"
            ) { tool ->
                when (tool) {
                    BottomToolType.TIMELINE -> {
                        Column {
                            // Transport Control Strip
                            TimelineControlsStrip(
                                isPlaying = isPlaying,
                                timeFormatted = formatTime(currentPlayheadMs),
                                durationFormatted = formatTime(totalDurationMs),
                                timelineZoom = timelineZoom,
                                onPlayPause = { viewModel.togglePlayPause() },
                                onStepBack = { viewModel.stepBackward() },
                                onStepForward = { viewModel.stepForward() },
                                onZoomIn = { viewModel.zoomInTimeline() },
                                onZoomOut = { viewModel.zoomOutTimeline() }
                            )

                            // Selected Clip Timing Adjustment Panel (Lengthen, Shorten, Start Timing, Duplicate, Delete)
                            SelectedClipControlBar(
                                project = project,
                                selectedTrack = selectedTrack,
                                selectedClipId = selectedClipId,
                                onLengthen = { viewModel.adjustSelectedClipDuration(1000L) },
                                onShorten = { viewModel.adjustSelectedClipDuration(-1000L) },
                                onShiftStartBack = { viewModel.adjustSelectedClipStart(-1000L) },
                                onShiftStartForward = { viewModel.adjustSelectedClipStart(1000L) },
                                onDuplicate = { viewModel.duplicateSelectedClip() },
                                onDelete = { viewModel.deleteSelectedClip() }
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Multi-track Editing Timeline
                            MultiTrackTimeline(
                                project = project,
                                currentPlayheadMs = currentPlayheadMs,
                                totalDurationMs = totalDurationMs,
                                selectedTrack = selectedTrack,
                                selectedClipId = selectedClipId,
                                timelineZoom = timelineZoom,
                                onSeek = { viewModel.seekTo(it) },
                                onSelectTrack = { track, clipId -> viewModel.selectTrack(track, clipId) },
                                onTrimClip = { track, clipId, newStart, newDuration ->
                                    viewModel.updateClipTiming(track, clipId, newStart, newDuration)
                                }
                            )
                        }
                    }
                    BottomToolType.ADJUST -> InlineAdjustPanel(
                        viewModel = viewModel,
                        onClose = { viewModel.closeBottomTool() }
                    )
                    BottomToolType.FILTER -> InlineFilterPanel(
                        viewModel = viewModel,
                        onClose = { viewModel.closeBottomTool() }
                    )
                    BottomToolType.EFFECTS -> InlineEffectsPanel(
                        viewModel = viewModel,
                        onClose = { viewModel.closeBottomTool() }
                    )
                    BottomToolType.QURAN -> InlineQuranPanel(
                        viewModel = viewModel,
                        onClose = { viewModel.closeBottomTool() }
                    )
                    BottomToolType.TEXT -> InlineTextPanel(
                        viewModel = viewModel,
                        onClose = { viewModel.closeBottomTool() }
                    )
                    BottomToolType.CANVAS -> InlineCanvasPanel(
                        viewModel = viewModel,
                        onClose = { viewModel.closeBottomTool() }
                    )
                    BottomToolType.SPEED -> InlineSpeedPanel(
                        viewModel = viewModel,
                        onClose = { viewModel.closeBottomTool() }
                    )
                    BottomToolType.STICKER -> InlineStickerPanel(
                        viewModel = viewModel,
                        onClose = { viewModel.closeBottomTool() }
                    )
                    BottomToolType.CUSTOMIZE_TOOLBAR -> InlineCustomizeToolbarPanel(
                        viewModel = viewModel,
                        onClose = { viewModel.closeBottomTool() }
                    )
                    else -> {
                        // Fallback to timeline
                        Column {
                            TimelineControlsStrip(
                                isPlaying = isPlaying,
                                timeFormatted = formatTime(currentPlayheadMs),
                                durationFormatted = formatTime(totalDurationMs),
                                timelineZoom = timelineZoom,
                                onPlayPause = { viewModel.togglePlayPause() },
                                onStepBack = { viewModel.stepBackward() },
                                onStepForward = { viewModel.stepForward() },
                                onZoomIn = { viewModel.zoomInTimeline() },
                                onZoomOut = { viewModel.zoomOutTimeline() }
                            )
                            SelectedClipControlBar(
                                project = project,
                                selectedTrack = selectedTrack,
                                selectedClipId = selectedClipId,
                                onLengthen = { viewModel.adjustSelectedClipDuration(1000L) },
                                onShorten = { viewModel.adjustSelectedClipDuration(-1000L) },
                                onShiftStartBack = { viewModel.adjustSelectedClipStart(-1000L) },
                                onShiftStartForward = { viewModel.adjustSelectedClipStart(1000L) },
                                onDuplicate = { viewModel.duplicateSelectedClip() },
                                onDelete = { viewModel.deleteSelectedClip() }
                            )
                            MultiTrackTimeline(
                                project = project,
                                currentPlayheadMs = currentPlayheadMs,
                                totalDurationMs = totalDurationMs,
                                selectedTrack = selectedTrack,
                                selectedClipId = selectedClipId,
                                timelineZoom = timelineZoom,
                                onSeek = { viewModel.seekTo(it) },
                                onSelectTrack = { track, clipId -> viewModel.selectTrack(track, clipId) },
                                onTrimClip = { track, clipId, newStart, newDuration ->
                                    viewModel.updateClipTiming(track, clipId, newStart, newDuration)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun EditorTopBar(
    onBack: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onCustomizeTools: () -> Unit,
    onExport: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("editor_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextWhite
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopActionItem(
                icon = Icons.AutoMirrored.Filled.Undo,
                label = "Undo",
                onClick = onUndo
            )

            TopActionItem(
                icon = Icons.AutoMirrored.Filled.Redo,
                label = "Redo",
                onClick = onRedo
            )

            TopActionItem(
                icon = Icons.Default.Tune,
                label = "ترتیب فیچرز",
                onClick = onCustomizeTools
            )
        }

        Button(
            onClick = onExport,
            colors = ButtonDefaults.buttonColors(
                containerColor = QuranGold,
                contentColor = TextDark
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .height(34.dp)
                .testTag("export_button")
        ) {
            Text(
                text = "Export",
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp
            )
        }
    }
}

@Composable
fun TopActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = TextWhite,
            modifier = Modifier.size(17.dp)
        )
        Text(
            text = label,
            color = TextWhite,
            fontSize = 9.sp
        )
    }
}

@Composable
fun VideoPreviewCard(
    project: EditingProject,
    currentPlayheadMs: Long,
    isPlaying: Boolean,
    isMuted: Boolean,
    timeFormatted: String,
    onTogglePlay: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onTapOverlay: () -> Unit
) {
    val cfg = project.videoConfig

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2C3E50),
                            Color(0xFF1F4037),
                            Color(0xFF0F2027)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Nature Video Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0x55348AC7),
                                Color(0x442E7D32),
                                Color(0x88000000)
                            )
                        )
                    )
            )

            // LIVE FILTER LAYER
            when (cfg.activeFilter) {
                FilterType.WARM -> Box(modifier = Modifier.fillMaxSize().background(Color(0x35E67E22)))
                FilterType.VINTAGE -> Box(modifier = Modifier.fillMaxSize().background(Color(0x45795548)))
                FilterType.YELLOW -> Box(modifier = Modifier.fillMaxSize().background(Color(0x35F1C40F)))
                FilterType.CINEMATIC -> Box(modifier = Modifier.fillMaxSize().background(Color(0x3500838F)))
                FilterType.BLACK_AND_WHITE -> Box(modifier = Modifier.fillMaxSize().background(Color(0x55000000)))
                else -> {}
            }

            // LIVE BRIGHTNESS / CONTRAST ADJUSTMENT LAYER
            if (cfg.brightness > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = (cfg.brightness / 120f).coerceIn(0f, 0.45f)))
                )
            } else if (cfg.brightness < 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = (-cfg.brightness / 100f).coerceIn(0f, 0.65f)))
                )
            }

            // LIVE EFFECTS LAYER
            when (cfg.activeEffect) {
                EffectType.VIGNETTE -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color.Transparent, Color(0xB5000000))
                                )
                            )
                    )
                }
                EffectType.LIGHT -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0x44FFF9C4), Color.Transparent)
                                )
                            )
                    )
                }
                EffectType.GLITCH -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x22FF0000),
                                        Color(0x1100FFFF),
                                        Color(0x2200FF00)
                                    )
                                )
                            )
                    )
                }
                else -> {}
            }

            // Live Quran Ayah Overlay at current playhead
            val activeQuran = project.quranClips.find {
                currentPlayheadMs >= it.startTimeMs && currentPlayheadMs <= (it.startTimeMs + it.durationMs)
            }

            // Live Text Overlay at current playhead
            val activeText = project.textClips.find {
                currentPlayheadMs >= it.startTimeMs && currentPlayheadMs <= (it.startTimeMs + it.durationMs)
            }

            if (activeQuran != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xAA0A0A0E))
                        .border(1.dp, QuranGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .clickable(onClick = onTapOverlay),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = activeQuran.arabicText,
                            color = Color(activeQuran.arabicColor),
                            fontSize = activeQuran.fontSize.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = (activeQuran.fontSize * 1.5f).sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = activeQuran.urduTranslation,
                            color = Color(activeQuran.translationColor),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (activeText != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                        .background(Color(0x88000000))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = activeText.text,
                        color = Color(activeText.color),
                        fontSize = activeText.size.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Play / Pause Overlay Button in Center
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xAA000000))
                    .clickable(onClick = onTogglePlay)
                    .testTag("preview_play_pause_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = TextWhite,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Bottom Overlays (Timecode on Left, Mute + Fullscreen on Right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x88000000))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = timeFormatted,
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Mute",
                            tint = TextWhite,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleFullscreen,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = TextWhite,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineControlsStrip(
    isPlaying: Boolean,
    timeFormatted: String,
    durationFormatted: String,
    timelineZoom: Float,
    onPlayPause: () -> Unit,
    onStepBack: () -> Unit,
    onStepForward: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onStepBack,
                modifier = Modifier.size(26.dp)
            ) {
                Icon(Icons.Default.FastRewind, contentDescription = "Rewind 2s", tint = TextWhite, modifier = Modifier.size(16.dp))
            }

            IconButton(
                onClick = onPlayPause,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = QuranGold,
                    modifier = Modifier.size(22.dp)
                )
            }

            IconButton(
                onClick = onStepForward,
                modifier = Modifier.size(26.dp)
            ) {
                Icon(Icons.Default.FastForward, contentDescription = "Forward 2s", tint = TextWhite, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = timeFormatted,
                color = QuranGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "/",
                color = TextGray,
                fontSize = 10.sp
            )
            Text(
                text = durationFormatted,
                color = TextGray,
                fontSize = 11.sp
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onZoomOut,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out Timeline", tint = TextGray, modifier = Modifier.size(15.dp))
            }
            Text(
                text = "${(timelineZoom * 100).toInt()}%",
                color = TextGray,
                fontSize = 9.sp
            )
            IconButton(
                onClick = onZoomIn,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In Timeline", tint = TextGray, modifier = Modifier.size(15.dp))
            }
        }
    }
}

@Composable
fun SelectedClipControlBar(
    project: EditingProject,
    selectedTrack: TrackType,
    selectedClipId: String?,
    onLengthen: () -> Unit,
    onShorten: () -> Unit,
    onShiftStartBack: () -> Unit,
    onShiftStartForward: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    val clipInfo = when (selectedTrack) {
        TrackType.QURAN -> {
            val c = project.quranClips.find { it.id == selectedClipId } ?: project.quranClips.firstOrNull()
            if (c != null) "Quran: ${c.arabicText.take(16)}..." to (c.startTimeMs to c.durationMs) else null
        }
        TrackType.TEXT -> {
            val c = project.textClips.find { it.id == selectedClipId } ?: project.textClips.firstOrNull()
            if (c != null) "Text: ${c.text.take(16)}..." to (c.startTimeMs to c.durationMs) else null
        }
        TrackType.AUDIO -> {
            val c = project.audioClips.find { it.id == selectedClipId } ?: project.audioClips.firstOrNull()
            if (c != null) "Audio: ${c.title.take(16)}..." to (c.startTimeMs to c.durationMs) else null
        }
        TrackType.VIDEO -> {
            "Video: Full Clip" to (0L to project.durationMs)
        }
        else -> null
    }

    if (clipInfo != null) {
        val (title, timings) = clipInfo
        val (startMs, durMs) = timings
        val startSec = startMs / 1000f
        val durSec = durMs / 1000f

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 2.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, QuranGold.copy(alpha = 0.45f), RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkCardLighter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.width(100.dp)) {
                    Text(
                        text = title,
                        color = QuranGold,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Start: ${String.format("%.1f", startSec)}s",
                        color = TextGray,
                        fontSize = 8.5.sp
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(DarkCard)
                            .border(1.dp, DarkBorder, RoundedCornerShape(5.dp))
                            .clickable(onClick = onShorten)
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("- 1s", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(QuranGold.copy(alpha = 0.15f))
                            .border(1.dp, QuranGold, RoundedCornerShape(5.dp))
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${String.format("%.1f", durSec)}s",
                            color = QuranGold,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(QuranGold)
                            .clickable(onClick = onLengthen)
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+ 1s", color = TextDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    IconButton(
                        onClick = onDuplicate,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = TextWhite, modifier = Modifier.size(14.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF5350), modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MultiTrackTimeline(
    project: EditingProject,
    currentPlayheadMs: Long,
    totalDurationMs: Long,
    selectedTrack: TrackType,
    selectedClipId: String?,
    timelineZoom: Float,
    onSeek: (Long) -> Unit,
    onSelectTrack: (TrackType, String?) -> Unit,
    onTrimClip: (TrackType, String, Long, Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .width(92.dp)
                        .padding(top = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    TrackHeaderItem(
                        label = "VIDEO",
                        icon = Icons.Default.PlayArrow,
                        isSelected = selectedTrack == TrackType.VIDEO,
                        onClick = { onSelectTrack(TrackType.VIDEO, null) }
                    )

                    TrackHeaderItem(
                        label = "QURAN",
                        icon = Icons.Default.MenuBook,
                        isSelected = selectedTrack == TrackType.QURAN,
                        onClick = { onSelectTrack(TrackType.QURAN, project.quranClips.firstOrNull()?.id) }
                    )

                    TrackHeaderItem(
                        label = "TEXT",
                        icon = Icons.Default.TextFields,
                        isSelected = selectedTrack == TrackType.TEXT,
                        onClick = { onSelectTrack(TrackType.TEXT, project.textClips.firstOrNull()?.id) }
                    )

                    TrackHeaderItem(
                        label = "AUDIO",
                        icon = Icons.Default.Audiotrack,
                        isSelected = selectedTrack == TrackType.AUDIO,
                        onClick = { onSelectTrack(TrackType.AUDIO, project.audioClips.firstOrNull()?.id) }
                    )
                }

                val timelineWidthDp = (240 * timelineZoom).coerceAtLeast(200f).dp
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState())
                ) {
                    Column(modifier = Modifier.width(timelineWidthDp)) {
                        DynamicTimelineRuler(totalDurationMs = totalDurationMs)

                        Spacer(modifier = Modifier.height(3.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(145.dp)
                                .background(DarkBackground)
                                .pointerInput(totalDurationMs) {
                                    detectDragGestures { change, _ ->
                                        val progress = (change.position.x / size.width).coerceIn(0f, 1f)
                                        onSeek((progress * totalDurationMs).toLong())
                                    }
                                }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                VideoTrackInteractive(
                                    project = project,
                                    isSelected = selectedTrack == TrackType.VIDEO,
                                    onClick = { onSelectTrack(TrackType.VIDEO, null) }
                                )

                                QuranTrackInteractive(
                                    project = project,
                                    totalDurationMs = totalDurationMs,
                                    selectedTrack = selectedTrack,
                                    selectedClipId = selectedClipId,
                                    onSelect = { onSelectTrack(TrackType.QURAN, it) },
                                    onTrim = { id, start, dur -> onTrimClip(TrackType.QURAN, id, start, dur) }
                                )

                                TextTrackInteractive(
                                    project = project,
                                    totalDurationMs = totalDurationMs,
                                    selectedTrack = selectedTrack,
                                    selectedClipId = selectedClipId,
                                    onSelect = { onSelectTrack(TrackType.TEXT, it) },
                                    onTrim = { id, start, dur -> onTrimClip(TrackType.TEXT, id, start, dur) }
                                )

                                AudioTrackInteractive(
                                    project = project,
                                    totalDurationMs = totalDurationMs,
                                    selectedTrack = selectedTrack,
                                    selectedClipId = selectedClipId,
                                    onSelect = { onSelectTrack(TrackType.AUDIO, it) },
                                    onTrim = { id, start, dur -> onTrimClip(TrackType.AUDIO, id, start, dur) }
                                )
                            }

                            val playheadFrac = (currentPlayheadMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(2.5.dp)
                                    .align(Alignment.CenterStart)
                                    .offset(x = timelineWidthDp * playheadFrac)
                                    .background(PlayheadRed)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DynamicTimelineRuler(totalDurationMs: Long) {
    val sec = totalDurationMs / 1000
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("00:00", color = TextGray, fontSize = 8.5.sp)
        Text(String.format("%02d:%02d", (sec / 3) / 60, (sec / 3) % 60), color = TextGray, fontSize = 8.5.sp)
        Text(String.format("%02d:%02d", (2 * sec / 3) / 60, (2 * sec / 3) % 60), color = TextGray, fontSize = 8.5.sp)
        Text(String.format("%02d:%02d", sec / 60, sec % 60), color = QuranGold, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun VideoTrackInteractive(
    project: EditingProject,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(31.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(TrackVideoBg)
            .border(if (isSelected) 1.5.dp else 1.dp, if (isSelected) QuranGold else Color(0xFF3B4D61), RoundedCornerShape(5.dp))
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(8) { idx ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (idx % 2 == 0) TrackVideoClip else Color(0xFF1E2B38))
                    .border(0.5.dp, Color(0x22FFFFFF))
            )
        }
    }
}

@Composable
fun QuranTrackInteractive(
    project: EditingProject,
    totalDurationMs: Long,
    selectedTrack: TrackType,
    selectedClipId: String?,
    onSelect: (String) -> Unit,
    onTrim: (String, Long, Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(31.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(TrackQuranBg)
            .border(1.dp, Color(0xFF1E462E), RoundedCornerShape(5.dp))
    ) {
        project.quranClips.forEach { clip ->
            val startFrac = (clip.startTimeMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 0.95f)
            val durFrac = (clip.durationMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0.06f, 1f - startFrac)
            val isClipSelected = selectedTrack == TrackType.QURAN && (selectedClipId == clip.id || selectedClipId == null)

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(durFrac)
                    .offset(x = 240.dp * startFrac)
                    .clip(RoundedCornerShape(4.dp))
                    .background(TrackQuranClip)
                    .border(
                        width = if (isClipSelected) 2.dp else 1.dp,
                        color = if (isClipSelected) QuranGold else TrackQuranClip,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable { onSelect(clip.id) }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (isClipSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .width(3.5.dp)
                            .height(16.dp)
                            .background(QuranGold, RoundedCornerShape(2.dp))
                    )
                }

                Text(
                    text = clip.arabicText,
                    color = QuranGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )

                if (isClipSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(3.5.dp)
                            .height(16.dp)
                            .background(QuranGold, RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun TextTrackInteractive(
    project: EditingProject,
    totalDurationMs: Long,
    selectedTrack: TrackType,
    selectedClipId: String?,
    onSelect: (String) -> Unit,
    onTrim: (String, Long, Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(31.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(TrackTextBg)
            .border(1.dp, Color(0xFF1E3252), RoundedCornerShape(5.dp))
    ) {
        project.textClips.forEach { clip ->
            val startFrac = (clip.startTimeMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 0.95f)
            val durFrac = (clip.durationMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0.06f, 1f - startFrac)
            val isClipSelected = selectedTrack == TrackType.TEXT && (selectedClipId == clip.id || selectedClipId == null)

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(durFrac)
                    .offset(x = 240.dp * startFrac)
                    .clip(RoundedCornerShape(4.dp))
                    .background(TrackTextClip)
                    .border(
                        width = if (isClipSelected) 2.dp else 1.dp,
                        color = if (isClipSelected) QuranGold else Color(0xFF284B82),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable { onSelect(clip.id) }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (isClipSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .width(3.5.dp)
                            .height(16.dp)
                            .background(QuranGold, RoundedCornerShape(2.dp))
                    )
                }

                Text(
                    text = clip.text.take(15),
                    color = TextWhite,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )

                if (isClipSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(3.5.dp)
                            .height(16.dp)
                            .background(QuranGold, RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun AudioTrackInteractive(
    project: EditingProject,
    totalDurationMs: Long,
    selectedTrack: TrackType,
    selectedClipId: String?,
    onSelect: (String) -> Unit,
    onTrim: (String, Long, Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(31.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(TrackAudioBg)
            .border(1.dp, Color(0xFF134850), RoundedCornerShape(5.dp))
    ) {
        val audioClip = project.audioClips.firstOrNull()
        if (audioClip != null) {
            val isClipSelected = selectedTrack == TrackType.AUDIO
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = if (isClipSelected) 1.5.dp else 0.dp,
                        color = if (isClipSelected) QuranGold else Color.Transparent,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clickable { onSelect(audioClip.id) }
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val heights = listOf(10, 18, 8, 22, 14, 10, 24, 16, 12, 20, 6, 18, 14, 22, 10, 16, 24, 10, 14, 20, 12, 18)
                heights.forEach { h ->
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(h.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(TrackWaveform)
                    )
                }
            }
        }
    }
}

@Composable
fun TrackHeaderItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(31.dp)
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(if (isSelected) DarkCardLighter else Color.Transparent)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) QuranGold else TextGray,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            color = if (isSelected) TextWhite else TextGray,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun EditorCustomizableBottomToolbar(
    features: List<ToolbarFeatureItem>,
    activeTool: BottomToolType,
    onToolClick: (String) -> Unit,
    onCustomizeClick: () -> Unit
) {
    val iconForId: (String) -> ImageVector = { id ->
        when (id) {
            "timeline" -> Icons.Default.Movie
            "quran" -> Icons.Default.MenuBook
            "text" -> Icons.Default.TextFields
            "adjust" -> Icons.Default.Tune
            "filter" -> Icons.Default.Filter
            "effects" -> Icons.Default.AutoAwesome
            "canvas" -> Icons.Default.AspectRatio
            "audio" -> Icons.Default.Audiotrack
            "speed" -> Icons.Default.Speed
            "sticker" -> Icons.Default.NightlightRound
            "autocaption" -> Icons.Default.ClosedCaption
            else -> Icons.Default.Tune
        }
    }

    val visibleFeatures = features.filter { it.isVisible }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkCard)
            .navigationBarsPadding()
            .height(68.dp)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        visibleFeatures.forEach { item ->
            val isCurrentActive = when (item.id) {
                "timeline" -> activeTool == BottomToolType.TIMELINE
                "quran" -> activeTool == BottomToolType.QURAN
                "text" -> activeTool == BottomToolType.TEXT
                "adjust" -> activeTool == BottomToolType.ADJUST
                "filter" -> activeTool == BottomToolType.FILTER
                "effects" -> activeTool == BottomToolType.EFFECTS
                "canvas" -> activeTool == BottomToolType.CANVAS
                "audio" -> activeTool == BottomToolType.AUDIO
                "speed" -> activeTool == BottomToolType.SPEED
                "sticker" -> activeTool == BottomToolType.STICKER
                "autocaption" -> activeTool == BottomToolType.AUTO_CAPTION
                else -> false
            }

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToolClick(item.id) }
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = iconForId(item.id),
                    contentDescription = item.name,
                    tint = if (isCurrentActive) QuranGold else TextWhite,
                    modifier = Modifier.size(21.dp)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.name,
                    color = if (isCurrentActive) QuranGold else TextWhite,
                    fontSize = 9.5.sp,
                    fontWeight = if (isCurrentActive) FontWeight.Bold else FontWeight.Medium
                )
            }
        }

        // Dedicated "Customize Toolbar" Button at end
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onCustomizeClick)
                .padding(horizontal = 6.dp, vertical = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Customize Toolbar",
                tint = QuranGold,
                modifier = Modifier.size(21.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "ترتیب فیچرز",
                color = QuranGold,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
