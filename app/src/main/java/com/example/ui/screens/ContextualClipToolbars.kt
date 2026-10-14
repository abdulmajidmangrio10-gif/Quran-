package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnimationType
import com.example.data.model.AspectRatioOption
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardLighter
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldGreenLight
import com.example.ui.theme.IslamicCharcoal
import com.example.ui.theme.IslamicEmeraldGradient
import com.example.ui.theme.IslamicGoldGradient
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.PremiumGoldLight
import com.example.ui.theme.QuranGold
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.BottomToolType
import com.example.ui.viewmodel.EditorViewModel
import com.example.ui.viewmodel.TrackType

/**
 * Contextual Editing Toolbar that displays the exact options matching user specifications
 * for Video, Image, Text/Quran, and Audio.
 */
@Composable
fun ContextualClipToolbar(
    selectedTrack: TrackType,
    selectedClipId: String?,
    viewModel: EditorViewModel,
    onCloseSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditTextDialog by remember { mutableStateOf(false) }
    var textInputState by remember { mutableStateOf("") }
    var showVolumeDialog by remember { mutableStateOf(false) }
    var volumeSliderValue by remember { mutableFloatStateOf(1.0f) }

    // Dialog for Editing Text Content
    if (showEditTextDialog && selectedClipId != null) {
        AlertDialog(
            onDismissRequest = { showEditTextDialog = false },
            containerColor = DarkCard,
            title = {
                Text(
                    text = "Edit Text",
                    color = PremiumGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = textInputState,
                    onValueChange = { textInputState = it },
                    singleLine = false,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PremiumGold,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateTextClipContent(selectedClipId, textInputState)
                        showEditTextDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenLight)
                ) {
                    Text("Save", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditTextDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    // Dialog for Volume Adjustment
    if (showVolumeDialog) {
        AlertDialog(
            onDismissRequest = { showVolumeDialog = false },
            containerColor = DarkCard,
            title = {
                Text(
                    text = "Clip Volume (${(volumeSliderValue * 100).toInt()}%)",
                    color = PremiumGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Slider(
                        value = volumeSliderValue,
                        onValueChange = { volumeSliderValue = it },
                        valueRange = 0f..1.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = PremiumGold,
                            activeTrackColor = PremiumGold,
                            inactiveTrackColor = DarkBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedClipId != null) {
                            if (selectedTrack == TrackType.VIDEO) {
                                viewModel.setVideoVolume(volumeSliderValue.coerceIn(0f, 1f))
                            } else if (selectedTrack == TrackType.AUDIO) {
                                viewModel.setAudioClipVolume(selectedClipId, volumeSliderValue)
                            }
                        }
                        showVolumeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenLight)
                ) {
                    Text("Apply", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showVolumeDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(IslamicCharcoal)
            .border(1.dp, PremiumGold.copy(alpha = 0.35f))
    ) {
        // Top header strip: Clip type title + Deselect button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            when (selectedTrack) {
                                TrackType.VIDEO -> NeonBlue
                                TrackType.IMAGE -> NeonPink
                                TrackType.QURAN -> PremiumGold
                                TrackType.TEXT -> NeonPurple
                                TrackType.AUDIO -> EmeraldGreenLight
                                else -> TextWhite
                            }
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when (selectedTrack) {
                        TrackType.VIDEO -> "VIDEO CLIP CONTROLS"
                        TrackType.IMAGE -> "IMAGE CLIP CONTROLS"
                        TrackType.QURAN -> "QURAN AYAH CONTROLS"
                        TrackType.TEXT -> "TEXT CLIP CONTROLS"
                        TrackType.AUDIO -> "AUDIO CONTROLS"
                        else -> "CLIP CONTROLS"
                    },
                    color = PremiumGold,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Done / Deselect Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldGreenLight.copy(alpha = 0.25f))
                        .clickable(onClick = onCloseSelection)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Done ✓",
                        color = EmeraldGreenLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Horizontal Scrolling Toolbar with exact user requested options
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (selectedTrack) {
                TrackType.VIDEO -> {
                    // 1. Trim
                    ToolbarItem(icon = Icons.Default.ContentCut, label = "Trim") {
                        // Activates trimming by zooming into clip or focusing timeline
                        viewModel.openElementEditing(TrackType.VIDEO, selectedClipId)
                    }
                    // 2. Split
                    ToolbarItem(icon = Icons.Default.ContentCut, label = "Split", accent = PremiumGold) {
                        viewModel.splitSelectedClip()
                    }
                    // 3. Cut
                    ToolbarItem(icon = Icons.Default.ContentCut, label = "Cut") {
                        viewModel.cutSelectedClip()
                    }
                    // 4. Crop
                    ToolbarItem(icon = Icons.Default.Crop, label = "Crop") {
                        viewModel.openBottomTool(BottomToolType.CANVAS)
                    }
                    // 5. Resize / Fit
                    ToolbarItem(icon = Icons.Default.FitScreen, label = "Resize") {
                        viewModel.fitVideoToCanvas()
                    }
                    // 6. Zoom In
                    ToolbarItem(icon = Icons.Default.ZoomIn, label = "Zoom +") {
                        viewModel.zoomInVideo()
                    }
                    // 7. Zoom Out
                    ToolbarItem(icon = Icons.Default.ZoomOut, label = "Zoom −") {
                        viewModel.zoomOutVideo()
                    }
                    // 8. Move
                    ToolbarItem(icon = Icons.Default.OpenWith, label = "Move") {
                        selectedClipId?.let { viewModel.shiftVideoClipTiming(it, 1000L) }
                    }
                    // 9. Duplicate
                    ToolbarItem(icon = Icons.Default.ContentCopy, label = "Duplicate") {
                        viewModel.duplicateSelectedClip()
                    }
                    // 10. Delete
                    ToolbarItem(icon = Icons.Default.Delete, label = "Delete", accent = Color(0xFFFF453A)) {
                        viewModel.deleteSelectedClip()
                    }
                    // 11. Undo
                    ToolbarItem(icon = Icons.AutoMirrored.Filled.Undo, label = "Undo") {
                        viewModel.undo()
                    }
                    // 12. Speed
                    ToolbarItem(icon = Icons.Default.Speed, label = "Speed") {
                        viewModel.openBottomTool(BottomToolType.SPEED)
                    }
                    // 13. Volume
                    ToolbarItem(icon = Icons.Default.VolumeUp, label = "Volume") {
                        showVolumeDialog = true
                    }
                    // 14. Mute
                    ToolbarItem(icon = Icons.Default.VolumeMute, label = "Mute") {
                        viewModel.toggleMute()
                    }
                }

                TrackType.IMAGE -> {
                    // 1. Crop
                    ToolbarItem(icon = Icons.Default.Crop, label = "Crop") {
                        viewModel.openBottomTool(BottomToolType.CANVAS)
                    }
                    // 2. Resize / Zoom In
                    ToolbarItem(icon = Icons.Default.ZoomIn, label = "Zoom +") {
                        selectedClipId?.let { viewModel.zoomInImage(it) }
                    }
                    // 3. Zoom Out
                    ToolbarItem(icon = Icons.Default.ZoomOut, label = "Zoom −") {
                        selectedClipId?.let { viewModel.zoomOutImage(it) }
                    }
                    // 4. Move
                    ToolbarItem(icon = Icons.Default.OpenWith, label = "Move") {
                        selectedClipId?.let { viewModel.moveImagePosition(it, 0.05f, 0.05f) }
                    }
                    // 5. Rotate
                    ToolbarItem(icon = Icons.Default.RotateRight, label = "Rotate", accent = PremiumGold) {
                        selectedClipId?.let { viewModel.rotateImageClip(it, 90f) }
                    }
                    // 6. Duration +
                    ToolbarItem(icon = Icons.Default.Timer, label = "Dur +1s") {
                        selectedClipId?.let { viewModel.shiftImageClipTiming(it, 0L, 1000L) }
                    }
                    // 7. Duration −
                    ToolbarItem(icon = Icons.Default.Remove, label = "Dur −1s") {
                        selectedClipId?.let { viewModel.shiftImageClipTiming(it, 0L, -1000L) }
                    }
                    // 8. Start Time
                    ToolbarItem(icon = Icons.Default.FastForward, label = "Start +1s") {
                        selectedClipId?.let { viewModel.shiftImageClipTiming(it, 1000L, 0L) }
                    }
                    // 9. Duplicate
                    ToolbarItem(icon = Icons.Default.ContentCopy, label = "Duplicate") {
                        viewModel.duplicateSelectedClip()
                    }
                    // 10. Delete
                    ToolbarItem(icon = Icons.Default.Delete, label = "Delete", accent = Color(0xFFFF453A)) {
                        viewModel.deleteSelectedClip()
                    }
                }

                TrackType.TEXT -> {
                    // 1. Edit Text
                    ToolbarItem(icon = Icons.Default.Edit, label = "Edit Text", accent = PremiumGold) {
                        val textClip = viewModel.currentProject.value.textClips.find { it.id == selectedClipId }
                        textInputState = textClip?.text ?: ""
                        showEditTextDialog = true
                    }
                    // 2. Font
                    ToolbarItem(icon = Icons.Default.TextFields, label = "Font") {
                        viewModel.openElementEditing(TrackType.TEXT, selectedClipId)
                    }
                    // 3. Size +
                    ToolbarItem(icon = Icons.Default.FormatSize, label = "Size +") {
                        selectedClipId?.let { id ->
                            val cur = viewModel.currentProject.value.textClips.find { it.id == id }?.size ?: 22f
                            viewModel.setTextClipFontSize(id, cur + 2f)
                        }
                    }
                    // 4. Size −
                    ToolbarItem(icon = Icons.Default.Remove, label = "Size −") {
                        selectedClipId?.let { id ->
                            val cur = viewModel.currentProject.value.textClips.find { it.id == id }?.size ?: 22f
                            viewModel.setTextClipFontSize(id, cur - 2f)
                        }
                    }
                    // 5. Color (Gold)
                    ToolbarItem(icon = Icons.Default.Palette, label = "Gold Color", accent = PremiumGold) {
                        selectedClipId?.let { viewModel.setTextClipColor(it, 0xFFFFD700) }
                    }
                    // 6. Color (White)
                    ToolbarItem(icon = Icons.Default.FormatColorText, label = "White Color") {
                        selectedClipId?.let { viewModel.setTextClipColor(it, 0xFFFFFFFF) }
                    }
                    // 7. Position (Center)
                    ToolbarItem(icon = Icons.Default.OpenWith, label = "Center") {
                        selectedClipId?.let { viewModel.moveTextClipPosition(it, 0f, 0f) }
                    }
                    // 8. Background
                    ToolbarItem(icon = Icons.Default.Crop, label = "Background") {
                        selectedClipId?.let { id ->
                            val cur = viewModel.currentProject.value.textClips.find { it.id == id }?.backgroundColor
                            viewModel.setTextClipBgColor(id, if (cur != null) null else 0xCC0D0B14)
                        }
                    }
                    // 9. Alignment
                    ToolbarItem(icon = Icons.Default.FormatAlignLeft, label = "Align") {
                        selectedClipId?.let { viewModel.setTextClipAlignment(it, "Center") }
                    }
                    // 10. Rotation
                    ToolbarItem(icon = Icons.Default.RotateRight, label = "Rotate") {
                        selectedClipId?.let { viewModel.rotateTextClip(it, 15f) }
                    }
                    // 11. Animation
                    ToolbarItem(icon = Icons.Default.Animation, label = "Animation") {
                        selectedClipId?.let { viewModel.setTextClipAnimation(it, AnimationType.FADE_IN) }
                    }
                    // 12. Duration
                    ToolbarItem(icon = Icons.Default.Timer, label = "Dur +1s") {
                        selectedClipId?.let { viewModel.shiftTextClipTiming(it, 0L, 1000L) }
                    }
                    // 13. Start Time
                    ToolbarItem(icon = Icons.Default.FastForward, label = "Start +1s") {
                        selectedClipId?.let { viewModel.shiftTextClipTiming(it, 1000L, 0L) }
                    }
                    // 14. Duplicate
                    ToolbarItem(icon = Icons.Default.ContentCopy, label = "Duplicate") {
                        viewModel.duplicateSelectedClip()
                    }
                    // 15. Delete
                    ToolbarItem(icon = Icons.Default.Delete, label = "Delete", accent = Color(0xFFFF453A)) {
                        viewModel.deleteSelectedClip()
                    }
                }

                TrackType.QURAN -> {
                    // 1. Edit Ayah / Surah
                    ToolbarItem(icon = Icons.Default.Edit, label = "Change Ayah", accent = PremiumGold) {
                        viewModel.openElementEditing(TrackType.QURAN, selectedClipId)
                    }
                    // 2. Gold Text
                    ToolbarItem(icon = Icons.Default.Palette, label = "Gold", accent = PremiumGold) {
                        selectedClipId?.let { viewModel.setQuranClipArabicColor(it, 0xFFFFD700) }
                    }
                    // 3. White Text
                    ToolbarItem(icon = Icons.Default.FormatColorText, label = "White") {
                        selectedClipId?.let { viewModel.setQuranClipArabicColor(it, 0xFFFFFFFF) }
                    }
                    // 4. Size +
                    ToolbarItem(icon = Icons.Default.FormatSize, label = "Size +") {
                        selectedClipId?.let { id ->
                            val cur = viewModel.currentProject.value.quranClips.find { it.id == id }?.fontSize ?: 26f
                            viewModel.setQuranClipFontSize(id, cur + 2f)
                        }
                    }
                    // 5. Size −
                    ToolbarItem(icon = Icons.Default.Remove, label = "Size −") {
                        selectedClipId?.let { id ->
                            val cur = viewModel.currentProject.value.quranClips.find { it.id == id }?.fontSize ?: 26f
                            viewModel.setQuranClipFontSize(id, cur - 2f)
                        }
                    }
                    // 6. Translation Color
                    ToolbarItem(icon = Icons.Default.FormatColorText, label = "Urdu Color", accent = EmeraldGreenLight) {
                        selectedClipId?.let { viewModel.setQuranClipTranslationColor(it, 0xFFE6B74A) }
                    }
                    // 7. Rotate
                    ToolbarItem(icon = Icons.Default.RotateRight, label = "Rotate") {
                        selectedClipId?.let { viewModel.rotateQuranClip(it, 15f) }
                    }
                    // 8. Duration +
                    ToolbarItem(icon = Icons.Default.Timer, label = "Dur +1s") {
                        selectedClipId?.let { viewModel.shiftQuranClipTiming(it, 0L, 1000L) }
                    }
                    // 9. Start Time
                    ToolbarItem(icon = Icons.Default.FastForward, label = "Start +1s") {
                        selectedClipId?.let { viewModel.shiftQuranClipTiming(it, 1000L, 0L) }
                    }
                    // 10. Duplicate
                    ToolbarItem(icon = Icons.Default.ContentCopy, label = "Duplicate") {
                        viewModel.duplicateSelectedClip()
                    }
                    // 11. Delete
                    ToolbarItem(icon = Icons.Default.Delete, label = "Delete", accent = Color(0xFFFF453A)) {
                        viewModel.deleteSelectedClip()
                    }
                }

                TrackType.AUDIO -> {
                    // 1. Trim
                    ToolbarItem(icon = Icons.Default.ContentCut, label = "Trim") {
                        viewModel.openElementEditing(TrackType.AUDIO, selectedClipId)
                    }
                    // 2. Split
                    ToolbarItem(icon = Icons.Default.ContentCut, label = "Split", accent = PremiumGold) {
                        viewModel.splitSelectedClip()
                    }
                    // 3. Move
                    ToolbarItem(icon = Icons.Default.OpenWith, label = "Move +1s") {
                        selectedClipId?.let { viewModel.shiftAudioClipTiming(it, 1000L) }
                    }
                    // 4. Volume
                    ToolbarItem(icon = Icons.Default.VolumeUp, label = "Volume") {
                        showVolumeDialog = true
                    }
                    // 5. Mute
                    ToolbarItem(icon = Icons.Default.VolumeMute, label = "Mute") {
                        selectedClipId?.let { viewModel.toggleAudioClipMute(it) }
                    }
                    // 6. Start Time
                    ToolbarItem(icon = Icons.Default.FastForward, label = "Start −1s") {
                        selectedClipId?.let { viewModel.shiftAudioClipTiming(it, -1000L) }
                    }
                    // 7. Delete
                    ToolbarItem(icon = Icons.Default.Delete, label = "Delete", accent = Color(0xFFFF453A)) {
                        viewModel.deleteSelectedClip()
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun ToolbarItem(
    icon: ImageVector,
    label: String,
    accent: Color = TextWhite,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = accent,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (accent != TextWhite) accent else TextGray,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Interactive Preview Overlay Container that wraps text, images, or Quran calligraphy.
 * Renders distinct selection border with 4 corner resize/scale handles, touch drag,
 * pinch-to-zoom, and rotation.
 */
@Composable
fun InteractivePreviewOverlayBox(
    isSelected: Boolean,
    positionX: Float,
    positionY: Float,
    scale: Float,
    rotation: Float,
    onTap: () -> Unit,
    onMove: (dx: Float, dy: Float) -> Unit,
    onScaleChange: (newScale: Float) -> Unit,
    onRotateChange: (newRotation: Float) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var accumulatedScale by remember(scale) { mutableFloatStateOf(scale) }
    var accumulatedRotation by remember(rotation) { mutableFloatStateOf(rotation) }

    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = (positionX - 0.5f) * 320f
                translationY = (positionY - 0.5f) * 320f
                scaleX = accumulatedScale
                scaleY = accumulatedScale
                rotationZ = accumulatedRotation
            }
            .then(
                if (isSelected) {
                    Modifier
                        .border(1.5.dp, PremiumGold, RoundedCornerShape(8.dp))
                        .shadow(4.dp, RoundedCornerShape(8.dp), spotColor = PremiumGold)
                } else {
                    Modifier.border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                }
            )
            .pointerInput(isSelected) {
                if (isSelected) {
                    // Multi-touch gestures: Pinch to Zoom & Rotate
                    detectTransformGestures { _, pan, zoom, rot ->
                        accumulatedScale = (accumulatedScale * zoom).coerceIn(0.2f, 4.0f)
                        accumulatedRotation = (accumulatedRotation + rot) % 360f
                        onScaleChange(accumulatedScale)
                        onRotateChange(accumulatedRotation)
                        onMove(pan.x / 400f, pan.y / 400f)
                    }
                }
            }
            .pointerInput(Unit) {
                // Drag gesture to move
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onMove(dragAmount.x / 400f, dragAmount.y / 400f)
                }
            }
            .clickable(onClick = onTap)
            .padding(if (isSelected) 8.dp else 2.dp),
        contentAlignment = Alignment.Center
    ) {
        content()

        // 4 Corner Handles when selected
        if (isSelected) {
            // Top-Left Handle
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-6).dp, y = (-6).dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(PremiumGold)
                    .border(1.dp, Color.White, CircleShape)
            )

            // Top-Right Handle (Rotate / Quick Action)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-6).dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(EmeraldGreenLight)
                    .border(1.dp, Color.White, CircleShape)
            )

            // Bottom-Left Handle
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-6).dp, y = 6.dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, PremiumGold, CircleShape)
            )

            // Bottom-Right Handle (Resize / Scale Handle with drag)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 6.dp, y = 6.dp)
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(PremiumGold)
                    .border(1.5.dp, Color.White, CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val deltaScale = (dragAmount.x + dragAmount.y) / 200f
                            accumulatedScale = (accumulatedScale + deltaScale).coerceIn(0.2f, 4.0f)
                            onScaleChange(accumulatedScale)
                        }
                    }
            )
        }
    }
}
