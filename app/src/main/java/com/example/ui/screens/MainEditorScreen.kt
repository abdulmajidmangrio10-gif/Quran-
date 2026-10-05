package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Fullscreen
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.ui.theme.QuranGold
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
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
        val totalSec = ms / 1000f
        val min = (totalSec / 60).toInt()
        val sec = totalSec % 60
        String.format("%02d:%04.1f", min, sec)
    }

    // Determine if selected clip is muted
    val isSelectedClipMuted = when (selectedTrack) {
        TrackType.VIDEO -> project.videoClips.find { it.id == selectedClipId }?.isMuted ?: false
        TrackType.AUDIO -> project.audioClips.find { it.id == selectedClipId }?.volume?.let { it <= 0f } ?: false
        else -> false
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Bar
            EditorTopBar(
                onBack = { viewModel.navigateTo(AppScreen.HOME) },
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() },
                onCustomizeTools = { viewModel.openBottomTool(BottomToolType.CUSTOMIZE_TOOLBAR) },
                onExport = { viewModel.openModal(EditorModal.EXPORT) }
            )

            // VIDEO PREVIEW CANVAS AREA (ALWAYS VISIBLE AT TOP, NEVER COVERED)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 2.dp),
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

            // =========================================================================
            // LOWER VIDEO EDITOR AREA (MATCHING REFERENCE TIMELINE SPECIFICATION)
            // =========================================================================

            // 1. AT THE BOTTOM OF THE VIDEO PREVIEW: UNDO, REDO, PLAY, SPLIT, MUTE, DELETE, ZOOM
            EditorTransportControlStrip(
                isPlaying = isPlaying,
                timeFormatted = formatTime(currentPlayheadMs),
                durationFormatted = formatTime(totalDurationMs),
                isSelectedClipMuted = isSelectedClipMuted,
                hasSelectedClip = selectedClipId != null,
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() },
                onPlayPause = { viewModel.togglePlayPause() },
                onStepBack = { viewModel.stepBackward() },
                onStepForward = { viewModel.stepForward() },
                onSplit = { viewModel.splitSelectedClip() },
                onMuteClip = { viewModel.toggleSelectedClipMute() },
                onDelete = { viewModel.deleteSelectedClip() },
                onZoomIn = { viewModel.zoomInTimeline() },
                onZoomOut = { viewModel.zoomOutTimeline() }
            )

            // 2. THE EDITING TOOLBAR: Canvas, Audio, Sticker, Text, Effect, Filter, Quran, Adjust, Speed
            EditorToolbarRow(
                features = toolbarFeatures,
                activeTool = activeBottomTool,
                onToolClick = { toolId ->
                    when (toolId) {
                        "timeline" -> viewModel.closeBottomTool()
                        "canvas" -> viewModel.openBottomTool(BottomToolType.CANVAS)
                        "audio" -> viewModel.openBottomTool(BottomToolType.AUDIO)
                        "sticker" -> viewModel.openBottomTool(BottomToolType.STICKER)
                        "text" -> viewModel.openBottomTool(BottomToolType.TEXT)
                        "effects" -> viewModel.openBottomTool(BottomToolType.EFFECTS)
                        "filter" -> viewModel.openBottomTool(BottomToolType.FILTER)
                        "quran" -> viewModel.openBottomTool(BottomToolType.QURAN)
                        "adjust" -> viewModel.openBottomTool(BottomToolType.ADJUST)
                        "speed" -> viewModel.openBottomTool(BottomToolType.SPEED)
                        "autocaption" -> viewModel.openBottomTool(BottomToolType.AUTO_CAPTION)
                    }
                },
                onCustomizeClick = { viewModel.openBottomTool(BottomToolType.CUSTOMIZE_TOOLBAR) }
            )

            // 3. BELOW THIS TOOLBAR IS THE ACTUAL TIMELINE (or active inline panel)
            Crossfade(
                targetState = activeBottomTool,
                label = "TimelinePanelTransition",
                modifier = Modifier.padding(bottom = 4.dp)
            ) { tool ->
                when (tool) {
                    BottomToolType.TIMELINE -> {
                        ProfessionalTimelineView(
                            project = project,
                            currentPlayheadMs = currentPlayheadMs,
                            totalDurationMs = totalDurationMs,
                            selectedTrack = selectedTrack,
                            selectedClipId = selectedClipId,
                            timelineZoom = timelineZoom,
                            isPlaying = isPlaying,
                            viewModel = viewModel
                        )
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
                        ProfessionalTimelineView(
                            project = project,
                            currentPlayheadMs = currentPlayheadMs,
                            totalDurationMs = totalDurationMs,
                            selectedTrack = selectedTrack,
                            selectedClipId = selectedClipId,
                            timelineZoom = timelineZoom,
                            isPlaying = isPlaying,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// TOP BAR
// =========================================================================
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
            .height(48.dp)
            .padding(horizontal = 12.dp),
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
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onUndo, modifier = Modifier.size(32.dp)) {
                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = TextWhite, modifier = Modifier.size(20.dp))
            }

            IconButton(onClick = onRedo, modifier = Modifier.size(32.dp)) {
                Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo", tint = TextWhite, modifier = Modifier.size(20.dp))
            }
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

// =========================================================================
// TRANSPORT CONTROL STRIP: UNDO, REDO, PLAY, SPLIT, MUTE, DELETE, ZOOM
// =========================================================================
@Composable
fun EditorTransportControlStrip(
    isPlaying: Boolean,
    timeFormatted: String,
    durationFormatted: String,
    isSelectedClipMuted: Boolean,
    hasSelectedClip: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onPlayPause: () -> Unit,
    onStepBack: () -> Unit,
    onStepForward: () -> Unit,
    onSplit: () -> Unit,
    onMuteClip: () -> Unit,
    onDelete: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: UNDO, REDO, Timecode
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onUndo, modifier = Modifier.size(30.dp)) {
                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = TextWhite, modifier = Modifier.size(18.dp))
            }

            IconButton(onClick = onRedo, modifier = Modifier.size(30.dp)) {
                Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo", tint = TextWhite, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Timecode display
            Text(
                text = timeFormatted,
                color = QuranGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        // Center: Large prominent PLAY / PAUSE button
        Box(
            modifier = Modifier
                .size(38.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(QuranGold, Color(0xFFFFD54F))
                    )
                )
                .clickable(onClick = onPlayPause)
                .testTag("timeline_play_pause_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = TextDark,
                modifier = Modifier.size(24.dp)
            )
        }

        // Right side: SPLIT, MUTE CLIP, DELETE, ZOOM
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Split Action
            IconButton(
                onClick = onSplit,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCut,
                    contentDescription = "Split",
                    tint = QuranGold,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Mute Clip Action
            IconButton(
                onClick = onMuteClip,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = if (isSelectedClipMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = "Mute Clip",
                    tint = if (isSelectedClipMuted) Color(0xFFEF5350) else TextWhite,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Delete Action
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFEF5350),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Zoom In / Out
            IconButton(onClick = onZoomOut, modifier = Modifier.size(26.dp)) {
                Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = TextGray, modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = onZoomIn, modifier = Modifier.size(26.dp)) {
                Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = TextGray, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// =========================================================================
// EDITING TOOLBAR: Canvas, Audio, Sticker, Text, Effect, Filter, Quran...
// =========================================================================
@Composable
fun EditorToolbarRow(
    features: List<ToolbarFeatureItem>,
    activeTool: BottomToolType,
    onToolClick: (String) -> Unit,
    onCustomizeClick: () -> Unit
) {
    val iconForId: (String) -> ImageVector = { id ->
        when (id) {
            "canvas" -> Icons.Default.AspectRatio
            "audio" -> Icons.Default.Audiotrack
            "sticker" -> Icons.Default.NightlightRound
            "text" -> Icons.Default.TextFields
            "effects" -> Icons.Default.AutoAwesome
            "filter" -> Icons.Default.Filter
            "quran" -> Icons.Default.MenuBook
            "adjust" -> Icons.Default.Tune
            "speed" -> Icons.Default.Speed
            "autocaption" -> Icons.Default.ClosedCaption
            else -> Icons.Default.Movie
        }
    }

    val visibleFeatures = features.filter { it.isVisible }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(DarkCard)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Timeline Tab Item (to return back to timeline anytime)
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onToolClick("timeline") }
                .padding(horizontal = 6.dp, vertical = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Movie,
                contentDescription = "Timeline",
                tint = if (activeTool == BottomToolType.TIMELINE) QuranGold else TextWhite,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Timeline",
                color = if (activeTool == BottomToolType.TIMELINE) QuranGold else TextWhite,
                fontSize = 9.sp,
                fontWeight = if (activeTool == BottomToolType.TIMELINE) FontWeight.Bold else FontWeight.Medium
            )
        }

        visibleFeatures.forEach { item ->
            val isCurrentActive = when (item.id) {
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
                    .padding(horizontal = 6.dp, vertical = 3.dp)
                    .testTag("toolbar_${item.id}"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = iconForId(item.id),
                    contentDescription = item.name,
                    tint = if (isCurrentActive) QuranGold else TextWhite,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.name,
                    color = if (isCurrentActive) QuranGold else TextWhite,
                    fontSize = 9.sp,
                    fontWeight = if (isCurrentActive) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

// =========================================================================
// VIDEO PREVIEW CARD WITH LIVE FILTER, ADJUSTMENT, AND OVERLAYS
// =========================================================================
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

    // Check if the currently playing video clip is blank or muted
    val currentVideoClip = project.videoClips.find {
        currentPlayheadMs >= it.startTimeMs && currentPlayheadMs <= (it.startTimeMs + it.durationMs)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(16.dp)
    ) {
        val backgroundBrush = if (currentVideoClip?.isBlank == true) {
            Brush.verticalGradient(listOf(Color(0xFF0E0E12), Color(0xFF0E0E12)))
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF2C3E50),
                    Color(0xFF1F4037),
                    Color(0xFF0F2027)
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush),
            contentAlignment = Alignment.Center
        ) {
            // Nature Video Background (when not blank)
            if (currentVideoClip?.isBlank != true) {
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
            }

            // LIVE FILTER LAYER
            when (cfg.activeFilter) {
                FilterType.WARM -> Box(modifier = Modifier.fillMaxSize().background(Color(0x35E67E22)))
                FilterType.VINTAGE -> Box(modifier = Modifier.fillMaxSize().background(Color(0x45795548)))
                FilterType.YELLOW -> Box(modifier = Modifier.fillMaxSize().background(Color(0x35F1C40F)))
                FilterType.CINEMATIC -> Box(modifier = Modifier.fillMaxSize().background(Color(0x3500838F)))
                FilterType.BLACK_AND_WHITE -> Box(modifier = Modifier.fillMaxSize().background(Color(0x55000000)))
                else -> {}
            }

            // LIVE BRIGHTNESS / CONTRAST LAYER
            if (cfg.brightness > 0) {
                Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = (cfg.brightness / 120f).coerceIn(0f, 0.45f))))
            } else if (cfg.brightness < 0) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = (-cfg.brightness / 100f).coerceIn(0f, 0.65f))))
            }

            // LIVE EFFECTS LAYER
            when (cfg.activeEffect) {
                EffectType.VIGNETTE -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(colors = listOf(Color.Transparent, Color(0xB5000000)))
                            )
                    )
                }
                EffectType.LIGHT -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(colors = listOf(Color(0x44FFF9C4), Color.Transparent))
                            )
                    )
                }
                EffectType.GLITCH -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0x22FF0000), Color(0x1100FFFF), Color(0x2200FF00))
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0x99000000))
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
                            imageVector = if (isMuted || currentVideoClip?.isMuted == true) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Mute",
                            tint = if (isMuted || currentVideoClip?.isMuted == true) Color(0xFFEF5350) else TextWhite,
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
