package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AnimationType
import com.example.data.model.AspectRatioOption
import com.example.data.model.CanvasBgType
import com.example.data.model.EffectType
import com.example.data.model.FilterType
import com.example.data.quran.QuranData
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardLighter
import com.example.ui.theme.QuranGold
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.theme.TrackWaveform
import com.example.ui.viewmodel.EditorViewModel

@Composable
fun InlinePanelHeader(
    title: String,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = QuranGold,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = onClose,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = TextWhite,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// 1. INLINE ADJUST PANEL (Live Brightness, Contrast, Saturation)
@Composable
fun InlineAdjustPanel(
    viewModel: EditorViewModel,
    onClose: () -> Unit
) {
    val project by viewModel.currentProject.collectAsStateWithLifecycle()
    val cfg = project.videoConfig

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, QuranGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp)
        ) {
            InlinePanelHeader(title = "Adjust (Live Video Preview)", onClose = onClose)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AdjustSliderRow(
                    label = "Brightness",
                    value = cfg.brightness,
                    valueRange = -50f..50f,
                    valueDisplay = if (cfg.brightness >= 0) "+${cfg.brightness.toInt()}" else "${cfg.brightness.toInt()}",
                    onValueChange = { viewModel.updateVideoAdjustment(brightness = it) }
                )

                AdjustSliderRow(
                    label = "Contrast",
                    value = cfg.contrast,
                    valueRange = -50f..50f,
                    valueDisplay = if (cfg.contrast >= 0) "+${cfg.contrast.toInt()}" else "${cfg.contrast.toInt()}",
                    onValueChange = { viewModel.updateVideoAdjustment(contrast = it) }
                )

                AdjustSliderRow(
                    label = "Saturation",
                    value = cfg.saturation,
                    valueRange = -50f..50f,
                    valueDisplay = if (cfg.saturation >= 0) "+${cfg.saturation.toInt()}" else "${cfg.saturation.toInt()}",
                    onValueChange = { viewModel.updateVideoAdjustment(saturation = it) }
                )

                AdjustSliderRow(
                    label = "Temperature",
                    value = cfg.temperature,
                    valueRange = -50f..50f,
                    valueDisplay = if (cfg.temperature >= 0) "+${cfg.temperature.toInt()}" else "${cfg.temperature.toInt()}",
                    onValueChange = { viewModel.updateVideoAdjustment(temperature = it) }
                )
            }
        }
    }
}

// 2. INLINE FILTER PANEL
@Composable
fun InlineFilterPanel(
    viewModel: EditorViewModel,
    onClose: () -> Unit
) {
    val project by viewModel.currentProject.collectAsStateWithLifecycle()
    val activeFilter = project.videoConfig.activeFilter

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, QuranGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            InlinePanelHeader(title = "Video Filters (Live Video Preview)", onClose = onClose)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    FilterType.NONE,
                    FilterType.NATURAL,
                    FilterType.WARM,
                    FilterType.VINTAGE,
                    FilterType.YELLOW,
                    FilterType.CINEMATIC,
                    FilterType.BLACK_AND_WHITE
                ).forEach { filter ->
                    FilterThumbnailCard(
                        filter = filter,
                        isSelected = activeFilter == filter,
                        onClick = { viewModel.applyVideoFilter(filter) }
                    )
                }
            }
        }
    }
}

// 3. INLINE EFFECTS PANEL
@Composable
fun InlineEffectsPanel(
    viewModel: EditorViewModel,
    onClose: () -> Unit
) {
    val project by viewModel.currentProject.collectAsStateWithLifecycle()
    val activeEffect = project.videoConfig.activeEffect

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, QuranGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            InlinePanelHeader(title = "Video Effects (Live Video Preview)", onClose = onClose)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    EffectType.NONE,
                    EffectType.VIGNETTE,
                    EffectType.BLUR,
                    EffectType.GLITCH,
                    EffectType.CINEMATIC,
                    EffectType.LIGHT,
                    EffectType.SHAKE,
                    EffectType.ZOOM
                ).forEach { effect ->
                    EffectThumbnailCard(
                        effect = effect,
                        isSelected = activeEffect == effect,
                        onClick = { viewModel.applyVideoEffect(effect) }
                    )
                }
            }
        }
    }
}

// 4. INLINE QURAN EDITOR PANEL
@Composable
fun InlineQuranPanel(
    viewModel: EditorViewModel,
    onClose: () -> Unit
) {
    val searchQuery by viewModel.quranSearchQuery.collectAsStateWithLifecycle()
    val selectedSurah by viewModel.selectedSurah.collectAsStateWithLifecycle()
    val selectedAyah by viewModel.selectedAyah.collectAsStateWithLifecycle()
    val quranFont by viewModel.quranFont.collectAsStateWithLifecycle()
    val quranFontSize by viewModel.quranFontSize.collectAsStateWithLifecycle()
    val quranArabicColor by viewModel.quranArabicColor.collectAsStateWithLifecycle()
    val hasOutline by viewModel.quranHasOutline.collectAsStateWithLifecycle()
    val hasShadow by viewModel.quranHasShadow.collectAsStateWithLifecycle()

    var showFontMenu by remember { mutableStateOf(false) }
    val filteredSurahs = remember(searchQuery) {
        QuranData.searchSurahs(searchQuery)
    }
    val ayahs = remember(selectedSurah) {
        QuranData.getAyahsForSurah(selectedSurah.number)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(245.dp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, QuranGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Quran Ayah Settings", color = QuranGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { viewModel.addQuranToVideo() },
                        colors = ButtonDefaults.buttonColors(containerColor = QuranGold, contentColor = TextDark),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("+ Add to Video", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextWhite, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Surah & Ayah Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Surah Quick Selector
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setQuranSearch(it) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    placeholder = { Text("Search Surah", color = TextGray, fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkCardLighter,
                        unfocusedContainerColor = DarkCardLighter,
                        focusedBorderColor = QuranGold,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                // Current Ayah info badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkCardLighter)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${selectedSurah.nameEnglish} : ${selectedAyah.ayahNumber}",
                        color = QuranGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Ayahs Horizontal Picker
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ayahs.take(20).forEach { a ->
                    val isSel = a.ayahNumber == selectedAyah.ayahNumber
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) QuranGold else DarkCardLighter)
                            .clickable { viewModel.selectAyah(a) }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Ayah ${a.ayahNumber}",
                            color = if (isSel) TextDark else TextWhite,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Font & Size Controls Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Font Dropdown
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkCardLighter)
                            .clickable { showFontMenu = true }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(quranFont, color = TextWhite, fontSize = 11.sp)
                        Text(" ▼", color = TextGray, fontSize = 8.sp)
                    }

                    DropdownMenu(expanded = showFontMenu, onDismissRequest = { showFontMenu = false }) {
                        listOf("Al-Fatihah", "Naskh", "Amiri", "Modern", "Kufic").forEach { f ->
                            DropdownMenuItem(
                                text = { Text(f) },
                                onClick = {
                                    viewModel.setQuranFont(f)
                                    showFontMenu = false
                                }
                            )
                        }
                    }
                }

                // Size Slider
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.setQuranFontSize(quranFontSize - 2f) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = TextWhite, modifier = Modifier.size(14.dp))
                    }
                    Text("${quranFontSize.toInt()}sp", color = QuranGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { viewModel.setQuranFontSize(quranFontSize + 2f) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = TextWhite, modifier = Modifier.size(14.dp))
                    }
                }

                // Color & Style Toggles
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(quranArabicColor))
                            .border(1.dp, TextGray, CircleShape)
                            .clickable {
                                viewModel.setQuranArabicColor(
                                    if (quranArabicColor == 0xFFFFFFFF) 0xFFE6B74A else 0xFFFFFFFF
                                )
                            }
                    )

                    Icon(
                        imageVector = Icons.Default.CropFree,
                        contentDescription = "Outline",
                        tint = if (hasOutline) QuranGold else TextGray,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { viewModel.toggleQuranOutline() }
                    )

                    Icon(
                        imageVector = Icons.Default.FormatPaint,
                        contentDescription = "Shadow",
                        tint = if (hasShadow) QuranGold else TextGray,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { viewModel.toggleQuranShadow() }
                    )
                }
            }
        }
    }
}

// 5. INLINE TEXT PANEL
@Composable
fun InlineTextPanel(
    viewModel: EditorViewModel,
    onClose: () -> Unit
) {
    val textInput by viewModel.textInput.collectAsStateWithLifecycle()
    val textAnimation by viewModel.textAnimation.collectAsStateWithLifecycle()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, QuranGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Text Overlay", color = QuranGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { viewModel.applyEditedText() },
                        colors = ButtonDefaults.buttonColors(containerColor = QuranGold, contentColor = TextDark),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextWhite, modifier = Modifier.size(18.dp))
                    }
                }
            }

            OutlinedTextField(
                value = textInput,
                onValueChange = { viewModel.setTextInput(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                placeholder = { Text("Write Arabic or Urdu text...", color = TextGray, fontSize = 12.sp) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkCardLighter,
                    unfocusedContainerColor = DarkCardLighter,
                    focusedBorderColor = QuranGold,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Animation Row
            Text("Animation", color = TextGray, fontSize = 10.sp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    AnimationType.NONE,
                    AnimationType.FADE_IN,
                    AnimationType.SLIDE_UP,
                    AnimationType.ZOOM_IN,
                    AnimationType.FADE_OUT
                ).forEach { anim ->
                    val isSel = textAnimation == anim
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) QuranGold else DarkCardLighter)
                            .clickable { viewModel.setTextAnimation(anim) }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = anim.displayName,
                            color = if (isSel) TextDark else TextWhite,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

// 6. INLINE CANVAS PANEL
@Composable
fun InlineCanvasPanel(
    viewModel: EditorViewModel,
    onClose: () -> Unit
) {
    val project by viewModel.currentProject.collectAsStateWithLifecycle()
    val canvas = project.canvas

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, QuranGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            InlinePanelHeader(title = "Canvas & Aspect Ratio", onClose = onClose)

            Spacer(modifier = Modifier.height(4.dp))

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
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = opt.label,
                            color = if (isSel) TextDark else TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (canvas.bgType == CanvasBgType.BLUR) QuranGold else DarkCardLighter)
                        .clickable { viewModel.setCanvasBackground(CanvasBgType.BLUR) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Blur Background", color = if (canvas.bgType == CanvasBgType.BLUR) TextDark else TextWhite, fontSize = 11.sp)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (canvas.bgType == CanvasBgType.COLOR) QuranGold else DarkCardLighter)
                        .clickable { viewModel.setCanvasBackground(CanvasBgType.COLOR) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Black Charcoal", color = if (canvas.bgType == CanvasBgType.COLOR) TextDark else TextWhite, fontSize = 11.sp)
                }
            }
        }
    }
}

// 7. INLINE SPEED PANEL
@Composable
fun InlineSpeedPanel(
    viewModel: EditorViewModel,
    onClose: () -> Unit
) {
    val project by viewModel.currentProject.collectAsStateWithLifecycle()
    val currSpeed = project.videoConfig.speed
    val speeds = listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 3.0f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, QuranGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            InlinePanelHeader(title = "Video Playback Speed", onClose = onClose)

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(speeds) { spd ->
                    val isSel = currSpeed == spd
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) QuranGold else DarkCardLighter)
                            .clickable { viewModel.setVideoSpeed(spd) }
                            .padding(vertical = 10.dp),
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
        }
    }
}

// 8. INLINE STICKER PANEL
@Composable
fun InlineStickerPanel(
    viewModel: EditorViewModel,
    onClose: () -> Unit
) {
    val stickers = listOf(
        "﷽" to "Bismillah",
        "۞" to "Hizb",
        "۝" to "Ayah",
        "ﷲ" to "Allah",
        "ﷺ" to "Prophet",
        "🌙" to "Crescent",
        "⭐" to "Star",
        "🕌" to "Mosque",
        "📖" to "Quran",
        "✨" to "Glow",
        "🤲" to "Dua",
        "🕊️" to "Peace"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, QuranGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            InlinePanelHeader(title = "Islamic Stickers & Calligraphies", onClose = onClose)

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(stickers) { (sym, name) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCardLighter)
                            .clickable { viewModel.addSticker(sym) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(sym, fontSize = 20.sp)
                            Text(name, color = TextGray, fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }
}

// 9. INLINE CUSTOMIZE TOOLBAR PANEL ("فیچرز ایڈٹ اور ترتیب دینے کا پینل")
@Composable
fun InlineCustomizeToolbarPanel(
    viewModel: EditorViewModel,
    onClose: () -> Unit
) {
    val features by viewModel.toolbarFeatures.collectAsStateWithLifecycle()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(245.dp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, QuranGold, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("فیچرز کی ترتیب تبدیل کریں", color = QuranGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Customize Toolbar (Reorder & Visibility)", color = TextGray, fontSize = 10.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { viewModel.resetToolbarFeatures() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = "Reset", tint = TextGray, modifier = Modifier.size(18.dp))
                    }

                    IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Done", tint = QuranGold, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(features.size) { idx ->
                    val item = features[idx]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCardLighter)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${idx + 1}.", color = QuranGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(item.name, color = if (item.isVisible) TextWhite else TextGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Move Up / Left
                            IconButton(
                                onClick = { viewModel.moveToolbarFeature(idx, -1) },
                                enabled = idx > 0,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", tint = if (idx > 0) QuranGold else DarkBorder, modifier = Modifier.size(16.dp))
                            }

                            // Move Down / Right
                            IconButton(
                                onClick = { viewModel.moveToolbarFeature(idx, 1) },
                                enabled = idx < features.lastIndex,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", tint = if (idx < features.lastIndex) QuranGold else DarkBorder, modifier = Modifier.size(16.dp))
                            }

                            // Toggle Visibility (Show/Hide)
                            IconButton(
                                onClick = { viewModel.toggleToolbarFeatureVisibility(item.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (item.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Visibility",
                                    tint = if (item.isVisible) QuranGold else TextGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 10. INLINE AUDIO & RECITATION PANEL
@Composable
fun InlineAudioPanel(
    viewModel: EditorViewModel,
    onClose: () -> Unit
) {
    val project by viewModel.currentProject.collectAsStateWithLifecycle()
    val selectedClipId by viewModel.selectedClipId.collectAsStateWithLifecycle()
    val audioClips = project.audioClips
    val activeClip = audioClips.find { it.id == selectedClipId } ?: audioClips.firstOrNull()

    val audioPicker = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.addAudioFromUri(uri, "Custom Audio")
        }
    }

    val libraryAudios = listOf(
        "Mishary Rashid - Surah Al-Inshirah" to 42000L,
        "Abdul Basit - Surah Ar-Rahman" to 58000L,
        "Saad Al-Ghamdi - Surah Al-Mulk" to 49000L,
        "Peaceful Quran Ambience (Dua)" to 35000L
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(245.dp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, QuranGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Audio & Recitation Tracks", color = QuranGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (activeClip != null) "Selected: ${activeClip.title}" else "Add voiceover or background recitation",
                        color = TextGray,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }

                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextWhite, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons: Import File & Record
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { audioPicker.launch("audio/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCardLighter, contentColor = QuranGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Device Audio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        viewModel.addAudio("Voiceover Record")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2185B), contentColor = TextWhite),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Record Voice", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Active Clip Volume and Controls
            if (activeClip != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkCardLighter)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = if (activeClip.volume <= 0f) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Volume",
                            tint = if (activeClip.volume <= 0f) Color(0xFFEF5350) else QuranGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Volume: ${(activeClip.volume * 100).toInt()}%",
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Slider(
                            value = activeClip.volume,
                            onValueChange = { viewModel.setAudioClipVolume(activeClip.id, it) },
                            valueRange = 0f..1f,
                            modifier = Modifier.weight(1f).padding(horizontal = 6.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = QuranGold,
                                activeTrackColor = QuranGold,
                                inactiveTrackColor = DarkBorder
                            )
                        )
                    }

                    IconButton(
                        onClick = { viewModel.deleteSelectedClip() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Audio", tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Preset Islamic Recitations
            Text("Quran Recitations & Ambience", color = TextGray, fontSize = 10.sp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                libraryAudios.forEach { (name, _) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkCardLighter)
                            .clickable { viewModel.addAudio(name) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Audiotrack, contentDescription = null, tint = QuranGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(name, color = TextWhite, fontSize = 10.5.sp)
                        }
                    }
                }
            }
        }
    }
}

// 11. INLINE AUTO CAPTION PANEL
@Composable
fun InlineAutoCaptionPanel(
    viewModel: EditorViewModel,
    onClose: () -> Unit
) {
    val isAnalyzing by viewModel.isAnalyzingCaption.collectAsStateWithLifecycle()
    val status by viewModel.autoCaptionStatus.collectAsStateWithLifecycle()
    var selectedSurahNum by remember { androidx.compose.runtime.mutableIntStateOf(94) }
    var showSurahDropdown by remember { androidx.compose.runtime.mutableStateOf(false) }

    val surahs: List<com.example.data.quran.SurahMeta> = com.example.data.quran.QuranData.ALL_SURAHS

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(245.dp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, QuranGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ClosedCaption, contentDescription = null, tint = QuranGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Auto Caption (قرآنی کیپشن سنکرونائزیشن)", color = QuranGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextWhite, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Select Surah recitation to align and generate synchronized Ayah timeline clips:",
                color = TextGray,
                fontSize = 10.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Surah Selector Dropdown
            Box {
                val currentSurah = surahs.find { it.number == selectedSurahNum } ?: surahs.first()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkCardLighter)
                        .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                        .clickable { showSurahDropdown = true }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${currentSurah.number}. ${currentSurah.nameEnglish} (${currentSurah.nameArabic})",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${currentSurah.totalAyahs} Ayahs • ${currentSurah.revelation}",
                            color = QuranGold,
                            fontSize = 10.sp
                        )
                    }
                    Text("▼", color = TextGray, fontSize = 10.sp)
                }

                DropdownMenu(
                    expanded = showSurahDropdown,
                    onDismissRequest = { showSurahDropdown = false }
                ) {
                    surahs.forEach { s ->
                        DropdownMenuItem(
                            text = { Text("${s.number}. ${s.nameEnglish} - ${s.nameArabic}") },
                            onClick = {
                                selectedSurahNum = s.number
                                showSurahDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1B241F))
                    .border(1.dp, Color(0xFF2E7D32).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = status,
                    color = Color(0xFFA5D6A7),
                    fontSize = 10.5.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Button
            Button(
                onClick = { viewModel.generateAutoCaptions(selectedSurahNum) },
                enabled = !isAnalyzing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = QuranGold,
                    contentColor = TextDark,
                    disabledContainerColor = DarkCardLighter
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(color = TextDark, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aligning Recitation...", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generate & Synchronize Captions", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
