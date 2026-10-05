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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.ui.theme.TrackWaveform
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.EditorViewModel

@Composable
fun MediaEffectsScreen(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val mediaTab by viewModel.mediaEffectsTab.collectAsStateWithLifecycle()
    val project by viewModel.currentProject.collectAsStateWithLifecycle()
    val videoConfig = project.videoConfig

    val tabs = listOf("Audio", "Image", "Sticker", "Effects", "Filter", "Adjust")

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
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.MAIN_EDITOR) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }

                Text(
                    text = "Media & Effects",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            // Top Tabs Row: Audio, Image, Sticker, Effects, Filter, Adjust
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                tabs.forEachIndexed { idx, tabName ->
                    val isSelected = mediaTab == idx
                    Column(
                        modifier = Modifier
                            .clickable { viewModel.setMediaEffectsTab(idx) }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tabName,
                            color = if (isSelected) QuranGold else TextGray,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(2.dp)
                                    .background(QuranGold)
                            )
                        }
                    }
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Audio Section
                Text(
                    text = "Audio",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // 3 Audio Action Buttons: Add Music, Add Audio, Record Voice
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AudioActionCard(
                        title = "Add Music",
                        icon = Icons.Default.MusicNote,
                        onClick = { viewModel.addAudio("Holy Quran Recitation - Surah Rahman") },
                        modifier = Modifier.weight(1f)
                    )

                    AudioActionCard(
                        title = "Add Audio",
                        icon = Icons.Default.VolumeUp,
                        onClick = { viewModel.addAudio("Reverberant Nasheed Background") },
                        modifier = Modifier.weight(1f)
                    )

                    AudioActionCard(
                        title = "Record Voice",
                        icon = Icons.Default.Mic,
                        onClick = { viewModel.addAudio("Voice Recitation Recording") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Waveform Display Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, DarkBorder, RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val heights = listOf(16, 24, 8, 30, 20, 14, 34, 22, 18, 28, 10, 26, 20, 32, 16, 24, 36, 14, 20, 30, 18, 24, 28, 12, 22, 32, 16, 20, 26, 14, 24, 30)
                        heights.forEach { h ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(h.dp)
                                    .clip(RoundedCornerShape(1.5.dp))
                                    .background(TrackWaveform)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Effects Section
                Text(
                    text = "Effects",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Effects Horizontal Row: Blur, Glitch, Cinematic, Light, Shake, Zoom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(
                        EffectType.BLUR,
                        EffectType.GLITCH,
                        EffectType.CINEMATIC,
                        EffectType.LIGHT,
                        EffectType.SHAKE,
                        EffectType.ZOOM,
                        EffectType.VIGNETTE
                    ).forEach { effect ->
                        EffectThumbnailCard(
                            effect = effect,
                            isSelected = videoConfig.activeEffect == effect,
                            onClick = { viewModel.applyVideoEffect(effect) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Filter Section
                Text(
                    text = "Filter",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Filter Row: Natural, Warm, Vintage, Yellow, Cinematic, Black & White
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(
                        FilterType.NATURAL,
                        FilterType.WARM,
                        FilterType.VINTAGE,
                        FilterType.YELLOW,
                        FilterType.CINEMATIC,
                        FilterType.BLACK_AND_WHITE
                    ).forEach { filter ->
                        FilterThumbnailCard(
                            filter = filter,
                            isSelected = videoConfig.activeFilter == filter,
                            onClick = { viewModel.applyVideoFilter(filter) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Adjust Section with Sliders (Matching Reference: Brightness +15, Contrast 0, Saturation 0)
                Text(
                    text = "Adjust",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCard)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdjustSliderRow(
                        label = "Brightness",
                        value = videoConfig.brightness,
                        valueRange = -50f..50f,
                        valueDisplay = if (videoConfig.brightness >= 0) "+${videoConfig.brightness.toInt()}" else "${videoConfig.brightness.toInt()}",
                        onValueChange = { viewModel.updateVideoAdjustment(brightness = it) }
                    )

                    AdjustSliderRow(
                        label = "Contrast",
                        value = videoConfig.contrast,
                        valueRange = -50f..50f,
                        valueDisplay = if (videoConfig.contrast >= 0) "+${videoConfig.contrast.toInt()}" else "${videoConfig.contrast.toInt()}",
                        onValueChange = { viewModel.updateVideoAdjustment(contrast = it) }
                    )

                    AdjustSliderRow(
                        label = "Saturation",
                        value = videoConfig.saturation,
                        valueRange = -50f..50f,
                        valueDisplay = if (videoConfig.saturation >= 0) "+${videoConfig.saturation.toInt()}" else "${videoConfig.saturation.toInt()}",
                        onValueChange = { viewModel.updateVideoAdjustment(saturation = it) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun AudioActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(84.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = QuranGold,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            color = TextWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EffectThumbnailCard(
    effect: EffectType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF3B4D61),
                            Color(0xFF1B2838)
                        )
                    )
                )
                .border(
                    1.5.dp,
                    if (isSelected) QuranGold else DarkBorder,
                    RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = effect.displayName,
                tint = TextWhite.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = effect.displayName,
            color = if (isSelected) QuranGold else TextWhite,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun FilterThumbnailCard(
    filter: FilterType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    when (filter) {
                        FilterType.WARM -> Brush.linearGradient(listOf(Color(0xFFE67E22), Color(0xFFD35400)))
                        FilterType.VINTAGE -> Brush.linearGradient(listOf(Color(0xFF795548), Color(0xFF4E342E)))
                        FilterType.YELLOW -> Brush.linearGradient(listOf(Color(0xFFF1C40F), Color(0xFFD4AC0D)))
                        FilterType.BLACK_AND_WHITE -> Brush.linearGradient(listOf(Color(0xFF757575), Color(0xFF212121)))
                        else -> Brush.linearGradient(listOf(Color(0xFF27AE60), Color(0xFF1E8449)))
                    }
                )
                .border(
                    1.5.dp,
                    if (isSelected) QuranGold else DarkBorder,
                    RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = filter.displayName,
                tint = TextWhite.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = filter.displayName,
            color = if (isSelected) QuranGold else TextWhite,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun AdjustSliderRow(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    valueDisplay: String,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextWhite,
            fontSize = 12.sp,
            modifier = Modifier.width(85.dp)
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = QuranGold,
                activeTrackColor = QuranGold,
                inactiveTrackColor = DarkCardLighter
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = valueDisplay,
            color = QuranGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.End
        )
    }
}
