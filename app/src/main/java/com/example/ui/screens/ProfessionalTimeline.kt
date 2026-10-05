package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CropPortrait
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioClip
import com.example.data.model.EditingProject
import com.example.data.model.ImageClip
import com.example.data.model.QuranClip
import com.example.data.model.TextClip
import com.example.data.model.VideoTimelineClip
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
import com.example.ui.viewmodel.EditorViewModel
import com.example.ui.viewmodel.TrackType

val PinkButtonColor = Color(0xFFFF2D55)
val HandleWhite = Color(0xFFF0F0F5)
val HandleGray = Color(0xFF9E9EA8)

@Composable
fun ProfessionalTimelineView(
    project: EditingProject,
    currentPlayheadMs: Long,
    totalDurationMs: Long,
    selectedTrack: TrackType,
    selectedClipId: String?,
    timelineZoom: Float,
    isPlaying: Boolean,
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    var showPlusMenu by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    // Media picker for VIDEO / PHOTO
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.addMediaFromUri(uri)
        }
    }

    val scrollState = rememberScrollState()

    // Base scale: at zoom 1.0f, 1 second = 55dp (0.055 dp per ms)
    val dpPerMs = 0.055f * timelineZoom
    val timelineContentWidthDp = maxOf(400f, totalDurationMs * dpPerMs + 200f).dp

    // Auto-scroll timeline smoothly when playing so playhead stays in view
    LaunchedEffect(currentPlayheadMs, isPlaying) {
        if (isPlaying) {
            val playheadPx = with(density) { (currentPlayheadMs * dpPerMs).dp.toPx() }.toInt()
            val viewportPx = scrollState.maxValue
            if (playheadPx > scrollState.value + 600 || playheadPx < scrollState.value) {
                scrollState.scrollTo(maxOf(0, playheadPx - 300))
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // 1. LEFT SIDE: Large Rounded RED/PINK "+" Button & Track Headers
                Column(
                    modifier = Modifier
                        .width(68.dp)
                        .padding(start = 8.dp, end = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Large rounded RED/PINK "+" button
                    Box(
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .shadow(6.dp, RoundedCornerShape(14.dp))
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFFF3366), PinkButtonColor, Color(0xFFE00034))
                                    )
                                )
                                .clickable { showPlusMenu = true }
                                .testTag("timeline_add_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Media",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Sleek Popup Menu: BLANK vs VIDEO / PHOTO
                        DropdownMenu(
                            expanded = showPlusMenu,
                            onDismissRequest = { showPlusMenu = false },
                            modifier = Modifier
                                .background(DarkCardLighter)
                                .border(1.dp, QuranGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CropPortrait,
                                            contentDescription = null,
                                            tint = QuranGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("BLANK", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("Add blank clip / canvas", color = TextGray, fontSize = 10.sp)
                                        }
                                    }
                                },
                                onClick = {
                                    showPlusMenu = false
                                    viewModel.addBlankClip()
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.PhotoLibrary,
                                            contentDescription = null,
                                            tint = PinkButtonColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("VIDEO / PHOTO", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("Select from Phone Gallery", color = TextGray, fontSize = 10.sp)
                                        }
                                    }
                                },
                                onClick = {
                                    showPlusMenu = false
                                    mediaPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Track Labels Column
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TrackMiniLabel("VIDEO", Icons.Default.Movie, selectedTrack == TrackType.VIDEO) {
                            viewModel.selectTrack(TrackType.VIDEO, project.videoClips.firstOrNull()?.id)
                        }
                        TrackMiniLabel("QURAN", Icons.Default.MenuBook, selectedTrack == TrackType.QURAN) {
                            viewModel.selectTrack(TrackType.QURAN, project.quranClips.firstOrNull()?.id)
                        }
                        TrackMiniLabel("TEXT", Icons.Default.TextFields, selectedTrack == TrackType.TEXT) {
                            viewModel.selectTrack(TrackType.TEXT, project.textClips.firstOrNull()?.id)
                        }
                        TrackMiniLabel("AUDIO", Icons.Default.Audiotrack, selectedTrack == TrackType.AUDIO) {
                            viewModel.selectTrack(TrackType.AUDIO, project.audioClips.firstOrNull()?.id)
                        }
                    }
                }

                // 2. RIGHT SIDE: Horizontally Scrollable Timeline Viewport
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(scrollState)
                        .testTag("timeline_scroll_container")
                ) {
                    Column(modifier = Modifier.width(timelineContentWidthDp)) {
                        // Dynamic Time Ruler
                        TimelineTimeRuler(
                            totalDurationMs = totalDurationMs,
                            dpPerMs = dpPerMs,
                            timelineWidthDp = timelineContentWidthDp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // All Tracks Canvas with Red Playhead
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(168.dp)
                                .background(DarkBackground)
                        ) {
                            // Tracks Column
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // 1. Video Track
                                VideoTrackView(
                                    clips = project.videoClips,
                                    selectedTrack = selectedTrack,
                                    selectedClipId = selectedClipId,
                                    dpPerMs = dpPerMs,
                                    onSelect = { viewModel.selectTrack(TrackType.VIDEO, it) },
                                    onDragStart = { viewModel.onDragGestureStarted() },
                                    onDragEnd = { viewModel.onDragGestureEnded() },
                                    onTrimStart = { id, delta -> viewModel.trimClipStart(TrackType.VIDEO, id, delta) },
                                    onTrimEnd = { id, delta -> viewModel.trimClipEnd(TrackType.VIDEO, id, delta) },
                                    onMove = { id, delta -> viewModel.moveClip(TrackType.VIDEO, id, delta) }
                                )

                                // 2. Quran Track
                                QuranTrackView(
                                    clips = project.quranClips,
                                    selectedTrack = selectedTrack,
                                    selectedClipId = selectedClipId,
                                    dpPerMs = dpPerMs,
                                    onSelect = { viewModel.selectTrack(TrackType.QURAN, it) },
                                    onDragStart = { viewModel.onDragGestureStarted() },
                                    onDragEnd = { viewModel.onDragGestureEnded() },
                                    onTrimStart = { id, delta -> viewModel.trimClipStart(TrackType.QURAN, id, delta) },
                                    onTrimEnd = { id, delta -> viewModel.trimClipEnd(TrackType.QURAN, id, delta) },
                                    onMove = { id, delta -> viewModel.moveClip(TrackType.QURAN, id, delta) }
                                )

                                // 3. Text Track
                                TextTrackView(
                                    clips = project.textClips,
                                    selectedTrack = selectedTrack,
                                    selectedClipId = selectedClipId,
                                    dpPerMs = dpPerMs,
                                    onSelect = { viewModel.selectTrack(TrackType.TEXT, it) },
                                    onDragStart = { viewModel.onDragGestureStarted() },
                                    onDragEnd = { viewModel.onDragGestureEnded() },
                                    onTrimStart = { id, delta -> viewModel.trimClipStart(TrackType.TEXT, id, delta) },
                                    onTrimEnd = { id, delta -> viewModel.trimClipEnd(TrackType.TEXT, id, delta) },
                                    onMove = { id, delta -> viewModel.moveClip(TrackType.TEXT, id, delta) }
                                )

                                // 4. Audio Track
                                AudioTrackView(
                                    clips = project.audioClips,
                                    selectedTrack = selectedTrack,
                                    selectedClipId = selectedClipId,
                                    dpPerMs = dpPerMs,
                                    onSelect = { viewModel.selectTrack(TrackType.AUDIO, it) },
                                    onDragStart = { viewModel.onDragGestureStarted() },
                                    onDragEnd = { viewModel.onDragGestureEnded() },
                                    onTrimStart = { id, delta -> viewModel.trimClipStart(TrackType.AUDIO, id, delta) },
                                    onTrimEnd = { id, delta -> viewModel.trimClipEnd(TrackType.AUDIO, id, delta) },
                                    onMove = { id, delta -> viewModel.moveClip(TrackType.AUDIO, id, delta) }
                                )
                            }

                            // 3. VERTICAL RED PLAYHEAD passing through all tracks
                            val playheadOffsetDp = (currentPlayheadMs * dpPerMs).dp
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .offset(x = playheadOffsetDp - 8.dp)
                                    .width(18.dp)
                                    .pointerInput(dpPerMs) {
                                        detectDragGestures { change, dragAmount ->
                                            change.consume()
                                            val deltaMs = (with(density) { dragAmount.x.toDp() }.value / dpPerMs).toLong()
                                            viewModel.seekTo(currentPlayheadMs + deltaMs)
                                        }
                                    },
                                contentAlignment = Alignment.TopCenter
                            ) {
                                // Top Red Circular / Teardrop Badge
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .shadow(3.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(PinkButtonColor)
                                        .border(1.5.dp, Color.White, CircleShape)
                                )

                                // Vertical Red Line
                                Box(
                                    modifier = Modifier
                                        .width(2.5.dp)
                                        .fillMaxHeight()
                                        .background(PinkButtonColor)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackMiniLabel(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) QuranGold.copy(alpha = 0.2f) else Color.Transparent)
            .border(1.dp, if (isSelected) QuranGold else Color.Transparent, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) QuranGold else TextGray,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = title,
            color = if (isSelected) QuranGold else TextGray,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TimelineTimeRuler(
    totalDurationMs: Long,
    dpPerMs: Float,
    timelineWidthDp: androidx.compose.ui.unit.Dp
) {
    // Generate tick marks every 1 second or 2 seconds based on scale
    val stepSec = if (dpPerMs > 0.06f) 1 else 2
    val totalSec = (totalDurationMs / 1000).toInt() + 10

    Box(
        modifier = Modifier
            .width(timelineWidthDp)
            .height(20.dp)
            .padding(horizontal = 4.dp)
    ) {
        for (s in 0..totalSec step stepSec) {
            val offsetDp = (s * 1000 * dpPerMs).dp
            val timeText = String.format("%02d:%02d", s / 60, s % 60)

            Column(
                modifier = Modifier
                    .offset(x = offsetDp)
                    .width(40.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .width(1.5.dp)
                        .height(5.dp)
                        .background(TextGray)
                )
                Text(
                    text = timeText,
                    color = TextGray,
                    fontSize = 8.5.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

// =========================================================================
// 1. VIDEO TRACK WITH MOVABLE AND TRIMMABLE CLIPS
// =========================================================================
@Composable
fun VideoTrackView(
    clips: List<VideoTimelineClip>,
    selectedTrack: TrackType,
    selectedClipId: String?,
    dpPerMs: Float,
    onSelect: (String) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onTrimStart: (String, Long) -> Unit,
    onTrimEnd: (String, Long) -> Unit,
    onMove: (String, Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(TrackVideoBg)
            .border(1.dp, Color(0xFF2C3E50), RoundedCornerShape(6.dp))
    ) {
        clips.forEach { clip ->
            val isSelected = selectedTrack == TrackType.VIDEO && (selectedClipId == clip.id || selectedClipId == null)
            TimelineClipItem(
                id = clip.id,
                title = if (clip.isBlank) "Blank Canvas" else clip.name,
                startTimeMs = clip.startTimeMs,
                durationMs = clip.durationMs,
                isMuted = clip.isMuted,
                isSelected = isSelected,
                accentColor = Color(0xFF3498DB),
                dpPerMs = dpPerMs,
                onSelect = { onSelect(clip.id) },
                onDragStart = onDragStart,
                onDragEnd = onDragEnd,
                onTrimStart = { onTrimStart(clip.id, it) },
                onTrimEnd = { onTrimEnd(clip.id, it) },
                onMove = { onMove(clip.id, it) }
            )
        }
    }
}

// =========================================================================
// 2. QURAN TRACK WITH MOVABLE AND TRIMMABLE CLIPS
// =========================================================================
@Composable
fun QuranTrackView(
    clips: List<QuranClip>,
    selectedTrack: TrackType,
    selectedClipId: String?,
    dpPerMs: Float,
    onSelect: (String) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onTrimStart: (String, Long) -> Unit,
    onTrimEnd: (String, Long) -> Unit,
    onMove: (String, Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(TrackQuranBg)
            .border(1.dp, Color(0xFF1E462E), RoundedCornerShape(6.dp))
    ) {
        clips.forEach { clip ->
            val isSelected = selectedTrack == TrackType.QURAN && (selectedClipId == clip.id || selectedClipId == null)
            TimelineClipItem(
                id = clip.id,
                title = clip.arabicText,
                startTimeMs = clip.startTimeMs,
                durationMs = clip.durationMs,
                isMuted = false,
                isSelected = isSelected,
                accentColor = QuranGold,
                dpPerMs = dpPerMs,
                onSelect = { onSelect(clip.id) },
                onDragStart = onDragStart,
                onDragEnd = onDragEnd,
                onTrimStart = { onTrimStart(clip.id, it) },
                onTrimEnd = { onTrimEnd(clip.id, it) },
                onMove = { onMove(clip.id, it) }
            )
        }
    }
}

// =========================================================================
// 3. TEXT TRACK WITH MOVABLE AND TRIMMABLE CLIPS
// =========================================================================
@Composable
fun TextTrackView(
    clips: List<TextClip>,
    selectedTrack: TrackType,
    selectedClipId: String?,
    dpPerMs: Float,
    onSelect: (String) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onTrimStart: (String, Long) -> Unit,
    onTrimEnd: (String, Long) -> Unit,
    onMove: (String, Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(TrackTextBg)
            .border(1.dp, Color(0xFF1E3252), RoundedCornerShape(6.dp))
    ) {
        clips.forEach { clip ->
            val isSelected = selectedTrack == TrackType.TEXT && (selectedClipId == clip.id || selectedClipId == null)
            TimelineClipItem(
                id = clip.id,
                title = clip.text,
                startTimeMs = clip.startTimeMs,
                durationMs = clip.durationMs,
                isMuted = false,
                isSelected = isSelected,
                accentColor = Color(0xFF5C6BC0),
                dpPerMs = dpPerMs,
                onSelect = { onSelect(clip.id) },
                onDragStart = onDragStart,
                onDragEnd = onDragEnd,
                onTrimStart = { onTrimStart(clip.id, it) },
                onTrimEnd = { onTrimEnd(clip.id, it) },
                onMove = { onMove(clip.id, it) }
            )
        }
    }
}

// =========================================================================
// 4. AUDIO TRACK WITH MOVABLE AND TRIMMABLE CLIPS
// =========================================================================
@Composable
fun AudioTrackView(
    clips: List<AudioClip>,
    selectedTrack: TrackType,
    selectedClipId: String?,
    dpPerMs: Float,
    onSelect: (String) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onTrimStart: (String, Long) -> Unit,
    onTrimEnd: (String, Long) -> Unit,
    onMove: (String, Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(TrackAudioBg)
            .border(1.dp, Color(0xFF134850), RoundedCornerShape(6.dp))
    ) {
        clips.forEach { clip ->
            val isSelected = selectedTrack == TrackType.AUDIO && (selectedClipId == clip.id || selectedClipId == null)
            TimelineClipItem(
                id = clip.id,
                title = clip.title,
                startTimeMs = clip.startTimeMs,
                durationMs = clip.durationMs,
                isMuted = clip.volume <= 0f,
                isSelected = isSelected,
                accentColor = Color(0xFF26A69A),
                dpPerMs = dpPerMs,
                onSelect = { onSelect(clip.id) },
                onDragStart = onDragStart,
                onDragEnd = onDragEnd,
                onTrimStart = { onTrimStart(clip.id, it) },
                onTrimEnd = { onTrimEnd(clip.id, it) },
                onMove = { onMove(clip.id, it) }
            )
        }
    }
}

// =========================================================================
// UNIVERSAL REUSABLE TIMELINE CLIP COMPONENT WITH LEFT & RIGHT TRIM HANDLES
// =========================================================================
@Composable
fun TimelineClipItem(
    id: String,
    title: String,
    startTimeMs: Long,
    durationMs: Long,
    isMuted: Boolean,
    isSelected: Boolean,
    accentColor: Color,
    dpPerMs: Float,
    onSelect: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onTrimStart: (Long) -> Unit,
    onTrimEnd: (Long) -> Unit,
    onMove: (Long) -> Unit
) {
    val density = LocalDensity.current
    val clipWidthDp = maxOf(40f, durationMs * dpPerMs).dp
    val clipOffsetDp = (startTimeMs * dpPerMs).dp

    Box(
        modifier = Modifier
            .offset(x = clipOffsetDp)
            .width(clipWidthDp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(accentColor.copy(alpha = 0.28f))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) QuranGold else accentColor.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // =============================================================
            // ACTION 4: LEFT HANDLE (WHITE/GRAY ROUNDED PILL) = TRIM START
            // =============================================================
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                        .background(HandleWhite)
                        .pointerInput(id, dpPerMs) {
                            detectDragGestures(
                                onDragStart = { onDragStart() },
                                onDragEnd = { onDragEnd() },
                                onDragCancel = { onDragEnd() }
                            ) { change, dragAmount ->
                                change.consume()
                                val deltaMs = (with(density) { dragAmount.x.toDp() }.value / dpPerMs).toLong()
                                onTrimStart(deltaMs)
                            }
                        }
                        .testTag("left_handle_$id"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "◀",
                        color = TextDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // =============================================================
            // ACTION 3: CENTER CLIP BODY = MOVE THE WHOLE CLIP & TAP SELECT
            // =============================================================
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onSelect() }
                    .pointerInput(id, dpPerMs) {
                        detectDragGestures(
                            onDragStart = { onDragStart() },
                            onDragEnd = { onDragEnd() },
                            onDragCancel = { onDragEnd() }
                        ) { change, dragAmount ->
                            change.consume()
                            val deltaMs = (with(density) { dragAmount.x.toDp() }.value / dpPerMs).toLong()
                            onMove(deltaMs)
                        }
                    }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isMuted) {
                        Icon(
                            imageVector = Icons.Default.VolumeOff,
                            contentDescription = "Muted",
                            tint = Color(0xFFEF5350),
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    Text(
                        text = title,
                        color = if (isSelected) QuranGold else TextWhite,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${String.format("%.1f", durationMs / 1000f)}s",
                        color = TextGray,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // =============================================================
            // ACTION 4: RIGHT HANDLE (WHITE/GRAY ROUNDED PILL) = TRIM END
            // =============================================================
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                        .background(HandleWhite)
                        .pointerInput(id, dpPerMs) {
                            detectDragGestures(
                                onDragStart = { onDragStart() },
                                onDragEnd = { onDragEnd() },
                                onDragCancel = { onDragEnd() }
                            ) { change, dragAmount ->
                                change.consume()
                                val deltaMs = (with(density) { dragAmount.x.toDp() }.value / dpPerMs).toLong()
                                onTrimEnd(deltaMs)
                            }
                        }
                        .testTag("right_handle_$id"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "▶",
                        color = TextDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
