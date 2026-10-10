package com.example.ui.screens

import android.net.Uri
import android.widget.VideoView
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                    viewModel = viewModel,
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
                onDuplicate = { viewModel.duplicateSelectedClip() },
                onMuteClip = { viewModel.toggleSelectedClipMute() },
                onDelete = { viewModel.deleteSelectedClip() },
                onZoomIn = { viewModel.zoomInTimeline() },
                onZoomOut = { viewModel.zoomOutTimeline() }
            )

            // 2. THE EDITING TOOLBAR: Quran, Text, Audio, Speed, Canvas, Adjust, Filter, Effects, Sticker
            EditorToolbarRow(
                features = toolbarFeatures,
                activeTool = activeBottomTool,
                onToolClick = { toolId ->
                    when (toolId) {
                        "timeline" -> viewModel.closeBottomTool()
                        "quran" -> viewModel.openBottomTool(BottomToolType.QURAN)
                        "text" -> viewModel.openBottomTool(BottomToolType.TEXT)
                        "audio" -> viewModel.openBottomTool(BottomToolType.AUDIO)
                        "speed" -> viewModel.openBottomTool(BottomToolType.SPEED)
                        "canvas" -> viewModel.openBottomTool(BottomToolType.CANVAS)
                        "adjust" -> viewModel.openBottomTool(BottomToolType.ADJUST)
                        "filter" -> viewModel.openBottomTool(BottomToolType.FILTER)
                        "effects" -> viewModel.openBottomTool(BottomToolType.EFFECTS)
                        "sticker" -> viewModel.openBottomTool(BottomToolType.STICKER)
                    }
                }
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
                    BottomToolType.QURAN -> InlineQuranPanel(
                        viewModel = viewModel,
                        onClose = { viewModel.closeBottomTool() }
                    )
                    BottomToolType.TEXT -> InlineTextPanel(
                        viewModel = viewModel,
                        onClose = { viewModel.closeBottomTool() }
                    )
                    BottomToolType.AUDIO -> InlineAudioPanel(
                        viewModel = viewModel,
                        onClose = { viewModel.closeBottomTool() }
                    )
                    BottomToolType.SPEED -> InlineSpeedPanel(
                        viewModel = viewModel,
                        onClose = { viewModel.closeBottomTool() }
                    )
                    BottomToolType.CANVAS -> InlineCanvasPanel(
                        viewModel = viewModel,
                        onClose = { viewModel.closeBottomTool() }
                    )
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
                    BottomToolType.STICKER -> InlineStickerPanel(
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
    onDuplicate: () -> Unit,
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

            // Duplicate Action
            IconButton(
                onClick = onDuplicate,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Duplicate",
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
    onToolClick: (String) -> Unit
) {
    val iconForId: (String) -> ImageVector = { id ->
        when (id) {
            "quran" -> Icons.Default.MenuBook
            "text" -> Icons.Default.TextFields
            "audio" -> Icons.Default.Audiotrack
            "speed" -> Icons.Default.Speed
            "canvas" -> Icons.Default.AspectRatio
            "adjust" -> Icons.Default.Tune
            "filter" -> Icons.Default.Filter
            "effects" -> Icons.Default.AutoAwesome
            "sticker" -> Icons.Default.NightlightRound
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
                "audio" -> activeTool == BottomToolType.AUDIO
                "speed" -> activeTool == BottomToolType.SPEED
                "canvas" -> activeTool == BottomToolType.CANVAS
                "adjust" -> activeTool == BottomToolType.ADJUST
                "filter" -> activeTool == BottomToolType.FILTER
                "effects" -> activeTool == BottomToolType.EFFECTS
                "sticker" -> activeTool == BottomToolType.STICKER
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
    viewModel: EditorViewModel,
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

    val currentScale = currentVideoClip?.scale ?: cfg.scale
    val currentOffsetX = currentVideoClip?.offsetX ?: cfg.offsetX
    val currentOffsetY = currentVideoClip?.offsetY ?: cfg.offsetY

    // Canvas Aspect Ratio
    val targetAspect = when (project.canvas.aspectRatio) {
        com.example.data.model.AspectRatioOption.RATIO_9_16 -> 9f / 16f
        com.example.data.model.AspectRatioOption.RATIO_16_9 -> 16f / 9f
        com.example.data.model.AspectRatioOption.RATIO_1_1 -> 1f
        com.example.data.model.AspectRatioOption.RATIO_4_5 -> 4f / 5f
        com.example.data.model.AspectRatioOption.RATIO_ORIGINAL -> 16f / 9f
    }

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
                .background(Color(0xFF0A0A0E)),
            contentAlignment = Alignment.Center
        ) {
            // Aspect-ratio-constrained video canvas
            Box(
                modifier = Modifier
                    .aspectRatio(targetAspect)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
                    .pointerInput(currentVideoClip?.id) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val oldScale = currentVideoClip?.scale ?: cfg.scale
                            val oldX = currentVideoClip?.offsetX ?: cfg.offsetX
                            val oldY = currentVideoClip?.offsetY ?: cfg.offsetY

                            val newScale = (oldScale * zoom).coerceIn(1.0f, 5.0f)

                            val maxPanX = if (newScale > 1f) (size.width * (newScale - 1f) / 2f) else 0f
                            val maxPanY = if (newScale > 1f) (size.height * (newScale - 1f) / 2f) else 0f

                            val newX = if (maxPanX > 0f) (oldX + pan.x).coerceIn(-maxPanX, maxPanX) else 0f
                            val newY = if (maxPanY > 0f) (oldY + pan.y).coerceIn(-maxPanY, maxPanY) else 0f

                            viewModel.updateVideoTransform(newScale, newX, newY)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Video & canvas layer transformed with hardware acceleration
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = currentScale
                            scaleY = currentScale
                            translationX = currentOffsetX
                            translationY = currentOffsetY
                        }
                ) {
                    // Real Android VideoView for imported gallery video
                    if (currentVideoClip?.isBlank != true && project.videoUri != null) {
                        var videoPlayerRef by remember { mutableStateOf<android.media.MediaPlayer?>(null) }
                        val isVideoMuted = isMuted || currentVideoClip?.isMuted == true

                        LaunchedEffect(isVideoMuted, currentVideoClip?.volume) {
                            try {
                                val v = if (isVideoMuted) 0f else (currentVideoClip?.volume ?: 1f).coerceIn(0f, 1f)
                                videoPlayerRef?.setVolume(v, v)
                            } catch (_: Exception) {}
                        }

                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { ctx ->
                                VideoView(ctx).apply {
                                    try {
                                        setVideoURI(Uri.parse(project.videoUri))
                                        setOnPreparedListener { mp ->
                                            videoPlayerRef = mp
                                            mp.isLooping = false
                                            val v = if (isVideoMuted) 0f else (currentVideoClip?.volume ?: 1f).coerceIn(0f, 1f)
                                            mp.setVolume(v, v)
                                            try {
                                                val p = mp.playbackParams
                                                p.speed = project.videoConfig.speed.coerceIn(0.25f, 3.0f)
                                                mp.playbackParams = p
                                            } catch (_: Exception) {}
                                        }
                                        setOnErrorListener { _, _, _ -> true }
                                    } catch (_: Exception) {}
                                }
                            },
                            update = { vv ->
                                try {
                                    val v = if (isVideoMuted) 0f else (currentVideoClip?.volume ?: 1f).coerceIn(0f, 1f)
                                    videoPlayerRef?.setVolume(v, v)

                                    val sourcePosMs = if (currentVideoClip != null) {
                                        val offsetFromClipStart = (currentPlayheadMs - currentVideoClip.startTimeMs).coerceAtLeast(0L)
                                        val target = currentVideoClip.trimStartMs + offsetFromClipStart
                                        target.coerceIn(currentVideoClip.trimStartMs, currentVideoClip.trimStartMs + currentVideoClip.durationMs)
                                    } else {
                                        currentPlayheadMs
                                    }

                                    if (isPlaying) {
                                        if (!vv.isPlaying) {
                                            vv.seekTo(sourcePosMs.toInt())
                                            vv.start()
                                        }
                                    } else {
                                        if (vv.isPlaying) {
                                            vv.pause()
                                        }
                                        val diff = Math.abs(vv.currentPosition - sourcePosMs.toInt())
                                        if (diff > 500) {
                                            vv.seekTo(sourcePosMs.toInt())
                                        }
                                    }
                                } catch (_: Exception) {}
                            }
                        )
                    } else if (currentVideoClip?.isBlank == true) {
                        // Real Blank Canvas Clip
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF0E0E14)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Blank Canvas",
                                color = TextGray,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        // Nature placeholder when no video is selected yet
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
                            Text(
                                text = "No Video Selected\nTap '+' on Timeline to add Video",
                                color = TextGray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
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

                // Live Quran Ayah Overlay at current playhead with actual animation and touch dragging
                val activeQuran = project.quranClips.find {
                    currentPlayheadMs >= it.startTimeMs && currentPlayheadMs <= (it.startTimeMs + it.durationMs)
                }

                // Live Text Overlay at current playhead with actual animation and touch dragging
                val activeText = project.textClips.find {
                    currentPlayheadMs >= it.startTimeMs && currentPlayheadMs <= (it.startTimeMs + it.durationMs)
                }

                // Live Sticker at current playhead with touch dragging
                val activeSticker = project.stickerClips.find {
                    currentPlayheadMs >= it.startTimeMs && currentPlayheadMs <= (it.startTimeMs + it.durationMs)
                }

                // Live Image at current playhead with touch dragging
                val activeImage = project.imageClips.find {
                    currentPlayheadMs >= it.startTimeMs && currentPlayheadMs <= (it.startTimeMs + it.durationMs)
                }

                // Render Active Quran Calligraphy
                if (activeQuran != null) {
                    val qProgress = ((currentPlayheadMs - activeQuran.startTimeMs).toFloat() / activeQuran.durationMs.coerceAtLeast(1000L).toFloat()).coerceIn(0f, 1f)
                    val qAlpha = when (activeQuran.animation) {
                        com.example.data.model.AnimationType.FADE_IN -> (qProgress / 0.15f).coerceIn(0f, 1f)
                        com.example.data.model.AnimationType.FADE_OUT -> ((1f - qProgress) / 0.15f).coerceIn(0f, 1f)
                        else -> 1f
                    }
                    val qOffsetY = when (activeQuran.animation) {
                        com.example.data.model.AnimationType.SLIDE_UP -> (1f - (qProgress / 0.15f).coerceIn(0f, 1f)) * 35f
                        com.example.data.model.AnimationType.SLIDE_DOWN -> -(1f - (qProgress / 0.15f).coerceIn(0f, 1f)) * 35f
                        else -> 0f
                    }
                    val qScale = when (activeQuran.animation) {
                        com.example.data.model.AnimationType.ZOOM_IN -> 0.7f + 0.3f * (qProgress / 0.15f).coerceIn(0f, 1f)
                        com.example.data.model.AnimationType.ZOOM_OUT -> 1.3f - 0.3f * (qProgress / 0.15f).coerceIn(0f, 1f)
                        else -> 1f
                    }

                    val qPosX = activeQuran.positionX
                    val qPosY = activeQuran.positionY
                    val qUserScale = activeQuran.scale
                    val qUserRotation = activeQuran.rotation

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .graphicsLayer {
                                alpha = qAlpha
                                translationX = (qPosX - 0.5f) * 320f
                                translationY = (qPosY - 0.5f) * 320f + qOffsetY
                                scaleX = qScale * qUserScale
                                scaleY = qScale * qUserScale
                                rotationZ = qUserRotation
                            }
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xAA0A0A0E))
                            .border(1.dp, QuranGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .pointerInput(activeQuran.id) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    viewModel.moveQuranClipPosition(activeQuran.id, dragAmount.x / 400f, dragAmount.y / 400f)
                                }
                            }
                            .clickable {
                                viewModel.selectTrack(TrackType.QURAN, activeQuran.id)
                                onTapOverlay()
                            },
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
                }

                // Render Active Text
                if (activeText != null) {
                    val tProgress = ((currentPlayheadMs - activeText.startTimeMs).toFloat() / activeText.durationMs.coerceAtLeast(1000L).toFloat()).coerceIn(0f, 1f)
                    val tAlpha = when (activeText.animation) {
                        com.example.data.model.AnimationType.FADE_IN -> (tProgress / 0.15f).coerceIn(0f, 1f)
                        com.example.data.model.AnimationType.FADE_OUT -> ((1f - tProgress) / 0.15f).coerceIn(0f, 1f)
                        else -> 1f
                    }
                    val tOffsetY = when (activeText.animation) {
                        com.example.data.model.AnimationType.SLIDE_UP -> (1f - (tProgress / 0.15f).coerceIn(0f, 1f)) * 35f
                        com.example.data.model.AnimationType.SLIDE_DOWN -> -(1f - (tProgress / 0.15f).coerceIn(0f, 1f)) * 35f
                        else -> 0f
                    }

                    val tPosX = activeText.positionX
                    val tPosY = activeText.positionY
                    val tUserScale = activeText.scale
                    val tUserRotation = activeText.rotation
                    val template = activeText.templateId

                    val containerBg = when (template) {
                        "special_breaking_news" -> Color(0xFFD32F2F)
                        "lbl_msg_bubble" -> Color(0xFF0288D1)
                        "lbl_sticky_note" -> Color(0xFFFFF59D)
                        "lbl_brush_note" -> Color(0xFFC2185B)
                        "lbl_warning" -> Color(0xFFFFD600)
                        "lbl_cartoon_eyes" -> Color.White
                        "lbl_ripped_paper" -> Color(0xFFEEEEEE)
                        "lbl_paper_tape" -> Color(0xFFD7CCC8)
                        "lbl_green_brush" -> Color(0xFF76FF03)
                        "lbl_click_button" -> Color.White
                        "cap_vlog_style" -> Color(0xFF37474F)
                        "cap_classic_black" -> Color.Black
                        "cap_light_yellow" -> Color(0xFFFFEB3B)
                        "cap_purple_label" -> Color(0xFFB39DDB)
                        else -> Color(0x99000000)
                    }

                    val textColor = when (template) {
                        "special_breaking_news" -> Color.White
                        "lbl_sticky_note", "lbl_warning", "lbl_cartoon_eyes", "lbl_ripped_paper", "lbl_paper_tape", "lbl_green_brush", "lbl_click_button", "cap_light_yellow", "cap_purple_label" -> Color.Black
                        "title_peach" -> Color(0xFFFF8A65)
                        "special_golden_time" -> Color(0xFFFFD54F)
                        "cap_unique_neon", "title_red_blue" -> Color(0xFF00E5FF)
                        "special_neon_lights" -> Color(0xFF76FF03)
                        "special_magnifier", "title_rainbow" -> Color(0xFFFF4081)
                        "special_cartoon" -> Color(0xFFFF7043)
                        "title_milksweet" -> Color(0xFF80DEEA)
                        "cap_sunny_pop" -> Color(0xFFFFB300)
                        "cap_gradient" -> Color(0xFFFF9800)
                        "cap_neon_script" -> Color(0xFFEA80FC)
                        "cap_pixel_style" -> Color(0xFF18FFFF)
                        else -> Color(activeText.color)
                    }

                    val textFont = when (template) {
                        "cap_typewriter", "cap_pixel_style", "lbl_pixel_bubble" -> FontFamily.Monospace
                        "special_golden_time" -> FontFamily.Serif
                        else -> FontFamily.Default
                    }

                    val borderColor = when (template) {
                        "title_peach" -> Color(0xFFFFCCBC)
                        "special_neon_lights", "lbl_green_brush" -> Color(0xFF76FF03)
                        "cap_unique_neon" -> Color(0xFF00E5FF)
                        "special_golden_time" -> Color(0xFFFFD54F)
                        else -> Color.White.copy(alpha = 0.6f)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .graphicsLayer {
                                alpha = tAlpha
                                translationX = (tPosX - 0.5f) * 320f
                                translationY = (tPosY - 0.5f) * 320f + tOffsetY
                                scaleX = tUserScale
                                scaleY = tUserScale
                                rotationZ = tUserRotation
                            }
                            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                            .background(containerBg, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                            .pointerInput(activeText.id) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    viewModel.moveTextClipPosition(activeText.id, dragAmount.x / 400f, dragAmount.y / 400f)
                                }
                            }
                            .clickable {
                                viewModel.selectTrack(TrackType.TEXT, activeText.id)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (template == "special_breaking_news") {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color.White)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("BREAKING NEWS", color = Color(0xFFD32F2F), fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(
                                text = when (template) {
                                    "lbl_warning" -> "⚠️ ${activeText.text}"
                                    "lbl_cartoon_eyes" -> "👀 ${activeText.text}"
                                    "lbl_green_leaf" -> "🍃 ${activeText.text}"
                                    "lbl_click_button" -> "👆 ${activeText.text}"
                                    else -> activeText.text
                                },
                                color = textColor,
                                fontSize = activeText.size.sp,
                                fontWeight = if (activeText.isBold || template != null) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = textFont,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Render Active Sticker
                if (activeSticker != null) {
                    val sPosX = activeSticker.positionX
                    val sPosY = activeSticker.positionY
                    val sUserScale = activeSticker.scale

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .graphicsLayer {
                                translationX = (sPosX - 0.5f) * 320f
                                translationY = (sPosY - 0.5f) * 320f
                                scaleX = sUserScale
                                scaleY = sUserScale
                            }
                            .pointerInput(activeSticker.id) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    viewModel.moveStickerPosition(activeSticker.id, dragAmount.x / 400f, dragAmount.y / 400f)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = activeSticker.emojiOrIcon,
                            fontSize = 34.sp
                        )
                    }
                }

                // Render Active Image
                if (activeImage != null) {
                    val iPosX = activeImage.positionX
                    val iPosY = activeImage.positionY
                    val iUserScale = activeImage.scale

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .graphicsLayer {
                                translationX = (iPosX - 0.5f) * 320f
                                translationY = (iPosY - 0.5f) * 320f
                                scaleX = iUserScale
                                scaleY = iUserScale
                            }
                            .pointerInput(activeImage.id) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    viewModel.moveImagePosition(activeImage.id, dragAmount.x / 400f, dragAmount.y / 400f)
                                }
                            }
                            .size(75.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = activeImage.uri,
                            contentDescription = "Image Overlay",
                            modifier = Modifier.fillMaxSize()
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
            }

            // Floating Zoom / Scale HUD Bar
            if (currentScale > 1.01f || currentOffsetX != 0f || currentOffsetY != 0f) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xCC111116))
                        .border(1.dp, QuranGold.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Zoom: ${String.format("%.1fx", currentScale)}",
                            color = QuranGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(QuranGold.copy(alpha = 0.25f))
                                .clickable { viewModel.resetVideoZoom() }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                .testTag("reset_zoom_button")
                        ) {
                            Text("Reset Zoom", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .clickable { viewModel.fitVideoToCanvas() }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                .testTag("fit_to_canvas_button")
                        ) {
                            Text("Fit to Canvas", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Bottom Overlays (Timecode on Left, Reset/Fit, Mute + Fullscreen on Right)
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

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { viewModel.resetVideoZoom() },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset Zoom",
                            tint = if (currentScale > 1.01f || currentOffsetX != 0f || currentOffsetY != 0f) QuranGold else TextWhite,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.fitVideoToCanvas() },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitScreen,
                            contentDescription = "Fit to Canvas",
                            tint = TextWhite,
                            modifier = Modifier.size(17.dp)
                        )
                    }

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
