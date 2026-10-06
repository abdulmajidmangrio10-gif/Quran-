package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val selectedAyahNumbers by viewModel.selectedAyahNumbers.collectAsStateWithLifecycle()
    val quranFont by viewModel.quranFont.collectAsStateWithLifecycle()
    val quranFontSize by viewModel.quranFontSize.collectAsStateWithLifecycle()
    val quranArabicColor by viewModel.quranArabicColor.collectAsStateWithLifecycle()
    val quranTranslationColor by viewModel.quranTranslationColor.collectAsStateWithLifecycle()
    val hasOutline by viewModel.quranHasOutline.collectAsStateWithLifecycle()
    val hasShadow by viewModel.quranHasShadow.collectAsStateWithLifecycle()

    var activeViewSurah by remember { mutableStateOf<SurahMeta?>(null) }
    var showFontMenu by remember { mutableStateOf(false) }

    val filteredSurahs = remember(searchQuery) {
        QuranData.searchSurahs(searchQuery)
    }

    val availableAyahs = remember(activeViewSurah) {
        activeViewSurah?.let { QuranData.getAyahsForSurah(it.number) } ?: emptyList()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        bottomBar = {
            if (activeViewSurah != null) {
                // Bottom bar when viewing Ayahs of a selected Surah
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${selectedAyahNumbers.size} Ayah(s) Selected",
                                    color = QuranGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "ٹک مارک والی آیات ویڈیو میں شامل کریں",
                                    color = TextGray,
                                    fontSize = 10.sp
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.addSelectedAyahsToVideo()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = QuranGold,
                                    contentColor = TextDark
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .height(42.dp)
                                    .testTag("add_selected_ayahs_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "+ Add to Video",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (activeViewSurah != null) {
                    // Back to Surahs list button
                    TextButton(
                        onClick = { activeViewSurah = null },
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Surahs",
                            tint = QuranGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تمام سورتیں (All Surahs)",
                            color = QuranGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.MAIN_EDITOR) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }

                    Text(
                        text = "قرآن کریم (Holy Quran)",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                // Quick Bismillah insertion
                Text(
                    text = "﷽",
                    color = QuranGold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            viewModel.addBasmalaToVideo()
                            viewModel.navigateTo(AppScreen.MAIN_EDITOR)
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            if (activeViewSurah == null) {
                // =========================================================================
                // SCREEN 1: ALL 114 SURAHS LIST WITH SEARCH
                // =========================================================================
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setQuranSearch(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    placeholder = { Text("Search Surah (1 to 114) / تلاش سورت", color = TextGray, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = QuranGold, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setQuranSearch("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextGray, modifier = Modifier.size(18.dp))
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

                Text(
                    text = "Click any Surah to select its Ayahs with checkmark (ٹک مارک):",
                    color = TextGray,
                    fontSize = 11.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredSurahs) { surah ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.selectSurah(surah)
                                    activeViewSurah = surah
                                }
                                .testTag("surah_item_${surah.number}"),
                            colors = CardDefaults.cardColors(containerColor = DarkCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Surah Number Badge
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(DarkCardLighter)
                                            .border(1.dp, QuranGold.copy(alpha = 0.5f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${surah.number}",
                                            color = QuranGold,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = surah.nameEnglish,
                                            color = TextWhite,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${surah.meaning} • ${surah.totalAyahs} Ayahs • ${surah.revelation}",
                                            color = TextGray,
                                            fontSize = 10.5.sp
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = surah.nameArabic,
                                        color = QuranGold,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = null,
                                        tint = TextGray,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // =========================================================================
                // SCREEN 2: ALL AYAHS OF THE SELECTED SURAH WITH TICK MARKS (ٹک مارک)
                // =========================================================================
                val surah = activeViewSurah!!

                // Surah Banner Info
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardLighter),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "سُورَةُ ${surah.nameArabic} - ${surah.nameEnglish}",
                            color = QuranGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Surah ${surah.number} • ${surah.totalAyahs} آیات • ${surah.revelation}",
                            color = TextGray,
                            fontSize = 11.sp
                        )
                    }
                }

                // Styling row (Font, Size, Color, Outline, Shadow)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Font Menu
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkCard)
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

                    // Size Controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.setQuranFontSize(quranFontSize - 2f) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = null, tint = TextWhite, modifier = Modifier.size(14.dp))
                        }
                        Text("${quranFontSize.toInt()}sp", color = QuranGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { viewModel.setQuranFontSize(quranFontSize + 2f) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = TextWhite, modifier = Modifier.size(14.dp))
                        }
                    }

                    // Color & Outline & Shadow toggles
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                .size(20.dp)
                                .clickable { viewModel.toggleQuranOutline() }
                        )

                        Icon(
                            imageVector = Icons.Default.FormatPaint,
                            contentDescription = "Shadow",
                            tint = if (hasShadow) QuranGold else TextGray,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { viewModel.toggleQuranShadow() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // ALL AYAHS LIST WITH PROMINENT CHECKMARK (ٹک مارک)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(availableAyahs) { ayah ->
                        val isChecked = selectedAyahNumbers.contains(ayah.ayahNumber)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isChecked) 1.5.dp else 1.dp,
                                    color = if (isChecked) QuranGold else DarkBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    viewModel.toggleAyahSelection(ayah)
                                }
                                .testTag("ayah_card_${ayah.ayahNumber}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isChecked) DarkCardLighter else DarkCard
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                // Top Row: Ayah Number Badge + TICK MARK (ٹک مارک)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isChecked) QuranGold else DarkCardLighter)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "آیت نمبر ${ayah.ayahNumber}",
                                                color = if (isChecked) TextDark else QuranGold,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = "Ayah ${ayah.ayahNumber}",
                                            color = TextGray,
                                            fontSize = 11.sp
                                        )
                                    }

                                    // PROMINENT CHECKMARK (ٹک مارک) ICON
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            viewModel.toggleAyahSelection(ayah)
                                        }
                                    ) {
                                        Text(
                                            text = if (isChecked) "Selected (منتخب)" else "Select",
                                            color = if (isChecked) QuranGold else TextGray,
                                            fontSize = 11.sp,
                                            fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(end = 6.dp)
                                        )

                                        Icon(
                                            imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = "Tick Mark",
                                            tint = if (isChecked) QuranGold else TextGray,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Arabic Quran Scripture
                                Text(
                                    text = ayah.arabicText,
                                    color = Color(quranArabicColor),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Right,
                                    lineHeight = 28.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Urdu Translation
                                Text(
                                    text = ayah.urduTranslation,
                                    color = Color(quranTranslationColor),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Right,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Quick Single Add Button
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.addAyahToProject(ayah)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isChecked) QuranGold else DarkCardLighter,
                                            contentColor = if (isChecked) TextDark else QuranGold
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.height(30.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "+ Add This Ayah",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
