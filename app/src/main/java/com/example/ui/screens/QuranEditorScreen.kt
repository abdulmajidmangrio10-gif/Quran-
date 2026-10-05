package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AnimationType
import com.example.data.quran.AyahItem
import com.example.data.quran.QuranData
import com.example.data.quran.SurahMeta
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardLighter
import com.example.ui.theme.QuranGold
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.EditorViewModel

@Composable
fun QuranEditorScreen(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.quranSearchQuery.collectAsStateWithLifecycle()
    val selectedSurah by viewModel.selectedSurah.collectAsStateWithLifecycle()
    val selectedAyah by viewModel.selectedAyah.collectAsStateWithLifecycle()
    val quranFont by viewModel.quranFont.collectAsStateWithLifecycle()
    val quranFontSize by viewModel.quranFontSize.collectAsStateWithLifecycle()
    val quranArabicColor by viewModel.quranArabicColor.collectAsStateWithLifecycle()
    val quranTranslationColor by viewModel.quranTranslationColor.collectAsStateWithLifecycle()
    val hasOutline by viewModel.quranHasOutline.collectAsStateWithLifecycle()
    val hasShadow by viewModel.quranHasShadow.collectAsStateWithLifecycle()

    var showFontMenu by remember { mutableStateOf(false) }
    val filteredSurahs = remember(searchQuery) {
        QuranData.searchSurahs(searchQuery)
    }

    val availableAyahs = remember(selectedSurah) {
        QuranData.getAyahsForSurah(selectedSurah.number)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        bottomBar = {
            // Cancel and Add to Video Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCard)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.MAIN_EDITOR) },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)
                    )
                ) {
                    Text("Cancel", color = TextWhite, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { viewModel.addQuranToVideo() },
                    modifier = Modifier
                        .weight(1.4f)
                        .height(46.dp)
                        .testTag("add_to_video_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = QuranGold,
                        contentColor = TextDark
                    )
                ) {
                    Text("Add to Video", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header: Back Arrow, Title "Quran"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
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
                    text = "Quran",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                // Basmala quick insert
                Text(
                    text = "﷽",
                    color = QuranGold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { viewModel.addBasmalaToVideo() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Search Bar: "Search Surah or Ayah"
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setQuranSearch(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Search Surah or Ayah", color = TextGray, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = TextGray)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setQuranSearch("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextGray)
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard,
                    focusedBorderColor = QuranGold,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                singleLine = true
            )

            // Middle section: Left Column (Surahs List) & Right Area (Selected Ayah Calligraphy Card)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Surahs List (All 114 Surahs available)
                LazyColumn(
                    modifier = Modifier
                        .width(135.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCard)
                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                        .padding(vertical = 6.dp)
                ) {
                    items(filteredSurahs) { surah ->
                        val isSelected = surah.number == selectedSurah.number
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectSurah(surah) }
                                .background(if (isSelected) DarkCardLighter else Color.Transparent)
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${surah.number}. ${surah.nameEnglish}",
                                color = if (isSelected) QuranGold else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Right Area: Calligraphy Preview Card & Ayah Picker
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Selected Ayah Calligraphy Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, QuranGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Arabic Quran Scripture
                            Text(
                                text = selectedAyah.arabicText,
                                color = Color(quranArabicColor),
                                fontSize = quranFontSize.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = (quranFontSize * 1.6f).sp,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Urdu Translation
                            Text(
                                text = selectedAyah.urduTranslation,
                                color = Color(quranTranslationColor),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Ayah selector strip
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCard)
                            .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    ) {
                        items(availableAyahs) { ayah ->
                            val isSelected = ayah.ayahNumber == selectedAyah.ayahNumber
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectAyah(ayah) }
                                    .background(if (isSelected) QuranGold.copy(alpha = 0.15f) else Color.Transparent)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Ayah ${ayah.ayahNumber}",
                                    color = if (isSelected) QuranGold else TextWhite,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = ayah.arabicText.take(15) + "...",
                                    color = TextGray,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // Controls Below: Font dropdown & Size Slider with minus/plus
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Font Dropdown
                Box(modifier = Modifier.width(130.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCard)
                            .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                            .clickable { showFontMenu = true }
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = quranFont,
                            color = TextWhite,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text("▼", color = TextGray, fontSize = 9.sp)
                    }

                    DropdownMenu(
                        expanded = showFontMenu,
                        onDismissRequest = { showFontMenu = false },
                        modifier = Modifier.background(DarkCard)
                    ) {
                        listOf("Al-Fatihah", "Naskh", "Amiri", "Modern", "Kufic").forEach { fontName ->
                            DropdownMenuItem(
                                text = { Text(fontName, color = TextWhite) },
                                onClick = {
                                    viewModel.setQuranFont(fontName)
                                    showFontMenu = false
                                }
                            )
                        }
                    }
                }

                // Size Slider with - and +
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.setQuranFontSize(quranFontSize - 2f) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease Size", tint = TextWhite, modifier = Modifier.size(16.dp))
                    }

                    Slider(
                        value = quranFontSize,
                        onValueChange = { viewModel.setQuranFontSize(it) },
                        valueRange = 16f..44f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = QuranGold,
                            activeTrackColor = QuranGold,
                            inactiveTrackColor = DarkCardLighter
                        )
                    )

                    IconButton(
                        onClick = { viewModel.setQuranFontSize(quranFontSize + 2f) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase Size", tint = TextWhite, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Styling Icons Row matching reference: Font, Size, Color, Outline, Shadow, Background, Position, Animation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuranControlIconItem(
                    label = "Font",
                    icon = Icons.Default.TextFields,
                    onClick = { showFontMenu = true }
                )

                QuranControlIconItem(
                    label = "Size",
                    icon = Icons.Default.FormatSize,
                    onClick = { viewModel.setQuranFontSize(if (quranFontSize > 30f) 22f else 32f) }
                )

                QuranControlIconItem(
                    label = "Color",
                    icon = Icons.Default.ColorLens,
                    isColorIndicator = true,
                    indicatorColor = Color(quranArabicColor),
                    onClick = {
                        val next = if (quranArabicColor == 0xFFFFFFFF) 0xFFE6B74A else 0xFFFFFFFF
                        viewModel.setQuranArabicColor(next)
                    }
                )

                QuranControlIconItem(
                    label = "Outline",
                    icon = Icons.Default.CropFree,
                    isActive = hasOutline,
                    onClick = { viewModel.toggleQuranOutline() }
                )

                QuranControlIconItem(
                    label = "Shadow",
                    icon = Icons.Default.FormatPaint,
                    isActive = hasShadow,
                    onClick = { viewModel.toggleQuranShadow() }
                )

                QuranControlIconItem(
                    label = "Background",
                    icon = Icons.Default.FormatColorFill,
                    onClick = { /* cycles background styles */ }
                )

                QuranControlIconItem(
                    label = "Position",
                    icon = Icons.Default.OpenWith,
                    onClick = { /* center repositioning */ }
                )

                QuranControlIconItem(
                    label = "Animation",
                    icon = Icons.Default.Animation,
                    onClick = { viewModel.setQuranAnimation(AnimationType.FADE_IN) }
                )
            }
        }
    }
}

@Composable
fun QuranControlIconItem(
    label: String,
    icon: ImageVector,
    isActive: Boolean = false,
    isColorIndicator: Boolean = false,
    indicatorColor: Color = Color.White,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        if (isColorIndicator) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(indicatorColor)
                    .border(1.5.dp, DarkBorder, CircleShape)
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) QuranGold else TextWhite,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = if (isActive) QuranGold else TextGray,
            fontSize = 9.sp
        )
    }
}
