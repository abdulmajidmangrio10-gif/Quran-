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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardLighter
import com.example.ui.theme.QuranGold
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.EditorViewModel

enum class TemplateCategory {
    SPECIAL, CAPTIONS, LABEL, TITLE
}

data class TemplateItem(
    val id: String,
    val title: String,
    val category: TemplateCategory,
    val isPro: Boolean = false
)

object TemplateData {
    val SPECIAL_TEMPLATES = listOf(
        TemplateItem("special_none", "None", TemplateCategory.SPECIAL),
        TemplateItem("special_blur", "BLUR", TemplateCategory.SPECIAL),
        TemplateItem("special_golden_time", "GOLDEN TIME", TemplateCategory.SPECIAL),
        TemplateItem("special_cartoon", "CARTOON", TemplateCategory.SPECIAL),
        TemplateItem("special_ribbed_glass", "RIBBED GLASS", TemplateCategory.SPECIAL, isPro = true),
        TemplateItem("special_rainbow", "RAINBOW", TemplateCategory.SPECIAL),
        TemplateItem("special_the_end", "The End", TemplateCategory.SPECIAL, isPro = true),
        TemplateItem("special_reflection", "REFLECTION", TemplateCategory.SPECIAL),
        TemplateItem("special_ghosted", "GHOSTED", TemplateCategory.SPECIAL),
        TemplateItem("special_neon_lights", "NEON LIGHTS", TemplateCategory.SPECIAL, isPro = true),
        TemplateItem("special_printing", "Printing", TemplateCategory.SPECIAL),
        TemplateItem("special_magnifier", "MAGNIFIER", TemplateCategory.SPECIAL),
        TemplateItem("special_colorful", "Colorful", TemplateCategory.SPECIAL, isPro = true),
        TemplateItem("special_no_signal", "NO SIGNAL", TemplateCategory.SPECIAL, isPro = true),
        TemplateItem("special_breaking_news", "BREAKING NEWS", TemplateCategory.SPECIAL)
    )

    val CAPTIONS_TEMPLATES = listOf(
        TemplateItem("cap_classic", "Classic", TemplateCategory.CAPTIONS),
        TemplateItem("cap_shadow", "Shadow", TemplateCategory.CAPTIONS),
        TemplateItem("cap_vlog_style", "Vlog style", TemplateCategory.CAPTIONS),
        TemplateItem("cap_unique_neon", "Unique neon", TemplateCategory.CAPTIONS),
        TemplateItem("cap_classic_black", "Classic black", TemplateCategory.CAPTIONS),
        TemplateItem("cap_sunny_pop", "Sunny pop", TemplateCategory.CAPTIONS, isPro = true),
        TemplateItem("cap_typewriter", "Typewriter", TemplateCategory.CAPTIONS),
        TemplateItem("cap_light_yellow", "Light yellow", TemplateCategory.CAPTIONS),
        TemplateItem("cap_white_outline", "White outline", TemplateCategory.CAPTIONS),
        TemplateItem("cap_green_fever", "Green fever", TemplateCategory.CAPTIONS, isPro = true),
        TemplateItem("cap_tint_edge", "TINT EDGE", TemplateCategory.CAPTIONS),
        TemplateItem("cap_gradient", "Gradient", TemplateCategory.CAPTIONS),
        TemplateItem("cap_neon_script", "Neon script", TemplateCategory.CAPTIONS),
        TemplateItem("cap_pixel_style", "Pixel style", TemplateCategory.CAPTIONS),
        TemplateItem("cap_purple_label", "Purple label", TemplateCategory.CAPTIONS)
    )

    val LABEL_TEMPLATES = listOf(
        TemplateItem("lbl_msg_bubble", "Message bubble", TemplateCategory.LABEL),
        TemplateItem("lbl_sticky_note", "Sticky Note", TemplateCategory.LABEL),
        TemplateItem("lbl_brush_note", "Brush note", TemplateCategory.LABEL),
        TemplateItem("lbl_warning", "Warning sign", TemplateCategory.LABEL),
        TemplateItem("lbl_cartoon_eyes", "Cartoon eyes", TemplateCategory.LABEL, isPro = true),
        TemplateItem("lbl_ripped_paper", "Ripped paper", TemplateCategory.LABEL),
        TemplateItem("lbl_no_signal_bar", "No signal", TemplateCategory.LABEL),
        TemplateItem("lbl_paper_tape", "PAPER TAPE", TemplateCategory.LABEL),
        TemplateItem("lbl_phone_card", "Phone Card", TemplateCategory.LABEL),
        TemplateItem("lbl_cat_note", "Cat Note", TemplateCategory.LABEL, isPro = true),
        TemplateItem("lbl_green_brush", "Green brush", TemplateCategory.LABEL),
        TemplateItem("lbl_pixel_bubble", "PIXEL BUBBLE", TemplateCategory.LABEL),
        TemplateItem("lbl_green_leaf", "Green leaf", TemplateCategory.LABEL),
        TemplateItem("lbl_click_button", "CLICK BUTTON", TemplateCategory.LABEL),
        TemplateItem("lbl_burger_badge", "Burger Badge", TemplateCategory.LABEL, isPro = true)
    )

    val TITLE_TEMPLATES = listOf(
        TemplateItem("title_peach", "PEACH", TemplateCategory.TITLE),
        TemplateItem("title_blur", "BLUR", TemplateCategory.TITLE),
        TemplateItem("title_milksweet", "Milksweet", TemplateCategory.TITLE),
        TemplateItem("title_laser", "Laser", TemplateCategory.TITLE),
        TemplateItem("title_rainbow", "RAINBOW", TemplateCategory.TITLE, isPro = true),
        TemplateItem("title_soft_script", "Soft script", TemplateCategory.TITLE),
        TemplateItem("title_3d_stack", "3D STACK", TemplateCategory.TITLE, isPro = true),
        TemplateItem("title_handwriting", "Handwriting", TemplateCategory.TITLE),
        TemplateItem("title_gradient_color", "Gradient color", TemplateCategory.TITLE),
        TemplateItem("title_brownie", "Brownie", TemplateCategory.TITLE),
        TemplateItem("title_pink_cap", "PINK CAP", TemplateCategory.TITLE, isPro = true),
        TemplateItem("title_red_blue", "RED BLUE", TemplateCategory.TITLE),
        TemplateItem("title_signature", "Signature.", TemplateCategory.TITLE),
        TemplateItem("title_fun_dot", "FUN DOT", TemplateCategory.TITLE, isPro = true),
        TemplateItem("title_doodle", "Doodle", TemplateCategory.TITLE, isPro = true)
    )
}

@Composable
fun TextTemplatesPanel(
    viewModel: EditorViewModel,
    onClose: () -> Unit
) {
    val project by viewModel.currentProject.collectAsStateWithLifecycle()
    val activeText = project.textClips.firstOrNull()
    val currentTemplateId = activeText?.templateId

    var selectedCategory by remember { mutableStateOf(TemplateCategory.TITLE) }

    val currentItems = when (selectedCategory) {
        TemplateCategory.SPECIAL -> TemplateData.SPECIAL_TEMPLATES
        TemplateCategory.CAPTIONS -> TemplateData.CAPTIONS_TEMPLATES
        TemplateCategory.LABEL -> TemplateData.LABEL_TEMPLATES
        TemplateCategory.TITLE -> TemplateData.TITLE_TEMPLATES
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp)
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, QuranGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14141A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 10.dp)
        ) {
            // Header: [X]  Text Templates  [✓]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextWhite, modifier = Modifier.size(20.dp))
                }

                Text(
                    text = "Text Templates",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Check, contentDescription = "Done", tint = QuranGold, modifier = Modifier.size(22.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Category Tabs: A+ icon, SPECIAL, CAPTIONS, LABEL, TITLE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FormatSize,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(18.dp)
                )

                listOf(
                    TemplateCategory.SPECIAL to "SPECIAL",
                    TemplateCategory.CAPTIONS to "CAPTIONS",
                    TemplateCategory.LABEL to "LABEL",
                    TemplateCategory.TITLE to "TITLE"
                ).forEach { (cat, label) ->
                    val isSel = selectedCategory == cat
                    Column(
                        modifier = Modifier
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 4.dp, horizontal = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = label,
                            color = if (isSel) TextWhite else TextGray,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .width(if (isSel) 28.dp else 0.dp)
                                .background(if (isSel) QuranGold else Color.Transparent)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 3-Column Grid of Text Template cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(currentItems) { item ->
                    val isCurrent = currentTemplateId == item.id
                    TemplateCardItem(
                        item = item,
                        isSelected = isCurrent,
                        onClick = { viewModel.applyTextTemplate(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TemplateCardItem(
    item: TemplateItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(58.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF22222A))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) QuranGold else Color(0xFF33333F),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        // Pro badge (small crown on top-end)
        if (item.isPro) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(3.dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(0x99000000)),
                contentAlignment = Alignment.Center
            ) {
                Text("👑", fontSize = 7.sp)
            }
        }

        // Render card content with style matching the screenshots
        when (item.id) {
            // SPECIAL TAB STYLES
            "special_none" -> Text("None", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            "special_blur" -> Text("BLUR", color = Color(0xFFAAAAAA), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            "special_golden_time" -> Text("GOLDEN TIME", color = Color(0xFFFFD54F), fontSize = 11.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
            "special_cartoon" -> Text("CARTOON", color = Color(0xFFFF7043), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            "special_ribbed_glass" -> Text("RIBBED GLASS", color = Color(0xFFECEFF1), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            "special_rainbow" -> Text("RAINBOW", color = Color(0xFF29B6F6), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            "special_the_end" -> Text("The End", color = Color(0xFFFFE082), fontSize = 14.sp, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold)
            "special_reflection" -> Text("REFLECTION", color = Color(0xFF64B5F6), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            "special_ghosted" -> Text("GHOSTED", color = Color(0xFFCCFF90), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            "special_neon_lights" -> Text("NEON LIGHTS", color = Color(0xFF76FF03), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            "special_printing" -> Text("Printing", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            "special_magnifier" -> Text("MAGNIFIER", color = Color(0xFFFF4081), fontSize = 12.sp, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Black)
            "special_colorful" -> Text("Colorful", color = Color(0xFFBA68C8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            "special_no_signal" -> Text("NO SIGNAL", color = TextWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            "special_breaking_news" -> {
                Box(modifier = Modifier.clip(RoundedCornerShape(3.dp)).background(Color(0xFFD32F2F)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text("BREAKING NEWS", color = Color.White, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                }
            }

            // CAPTIONS TAB STYLES
            "cap_classic" -> Text("Classic", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            "cap_shadow" -> Text("Shadow", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            "cap_vlog_style" -> {
                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFF37474F)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                    Text("Vlog style", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            "cap_unique_neon" -> Text("Unique neon", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            "cap_classic_black" -> {
                Box(modifier = Modifier.clip(RoundedCornerShape(2.dp)).background(Color.Black).padding(horizontal = 5.dp, vertical = 2.dp)) {
                    Text("Classic black", color = Color.White, fontSize = 9.5.sp)
                }
            }
            "cap_sunny_pop" -> Text("SUNNY POP", color = Color(0xFFFFB300), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            "cap_typewriter" -> Text("Typewriter", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            "cap_light_yellow" -> {
                Box(modifier = Modifier.clip(RoundedCornerShape(2.dp)).background(Color(0xFFFFEB3B)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text("Light yellow", color = Color.Black, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                }
            }
            "cap_white_outline" -> Text("White outline", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            "cap_green_fever" -> Text("Green fever", color = Color(0xFF69F0AE), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            "cap_tint_edge" -> Text("TINT EDGE", color = Color(0xFFFF80AB), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            "cap_gradient" -> Text("Gradient", color = Color(0xFFFF9800), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            "cap_neon_script" -> Text("Neon script", color = Color(0xFFEA80FC), fontSize = 12.sp, fontStyle = FontStyle.Italic)
            "cap_pixel_style" -> Text("Pixel style", color = Color(0xFF18FFFF), fontSize = 10.5.sp, fontFamily = FontFamily.Monospace)
            "cap_purple_label" -> {
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFB39DDB)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text("Purple label", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Medium)
                }
            }

            // LABEL TAB STYLES
            "lbl_msg_bubble" -> {
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF0288D1)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                    Text("Message bubble", color = Color.White, fontSize = 9.sp)
                }
            }
            "lbl_sticky_note" -> {
                Box(modifier = Modifier.size(38.dp, 32.dp).background(Color(0xFFFFF59D)).padding(2.dp), contentAlignment = Alignment.Center) {
                    Text("Note", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            "lbl_brush_note" -> {
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFC2185B)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text("Brush note", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            "lbl_warning" -> {
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFFFD600)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                    Text("⚠️ Warning", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            "lbl_cartoon_eyes" -> {
                Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White).padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text("👀 Cartoon", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            "lbl_ripped_paper" -> {
                Box(modifier = Modifier.clip(RoundedCornerShape(2.dp)).background(Color(0xFFEEEEEE)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text("Ripped paper", color = Color.Black, fontSize = 8.5.sp)
                }
            }
            "lbl_no_signal_bar" -> {
                Box(modifier = Modifier.border(1.dp, Color(0xFFE91E63), RoundedCornerShape(4.dp)).background(Color.Black).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text("No signal", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            "lbl_paper_tape" -> {
                Box(modifier = Modifier.clip(RoundedCornerShape(2.dp)).background(Color(0xFFD7CCC8)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text("PAPER TAPE", color = Color(0xFF3E2723), fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                }
            }
            "lbl_phone_card" -> Text("📱 Phone card", color = Color(0xFFFF80AB), fontSize = 10.sp)
            "lbl_cat_note" -> Text("🐱 Cat note", color = Color(0xFFFFB74D), fontSize = 10.sp)
            "lbl_green_brush" -> {
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFF76FF03)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text("Green brush", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            "lbl_pixel_bubble" -> {
                Box(modifier = Modifier.border(1.dp, Color.White, RoundedCornerShape(2.dp)).background(Color.Black).padding(horizontal = 5.dp, vertical = 2.dp)) {
                    Text("PIXEL BUBBLE", color = Color.White, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                }
            }
            "lbl_green_leaf" -> Text("🍃 Green leaf", color = Color(0xFF81C784), fontSize = 10.sp)
            "lbl_click_button" -> {
                Box(modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(Color.White).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text("CLICK BUTTON", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
            "lbl_burger_badge" -> Text("🍔 Burger", color = Color(0xFFFFB300), fontSize = 10.sp)

            // TITLE TAB STYLES
            "title_peach" -> Text("PEACH", color = Color(0xFFFF8A65), fontSize = 14.sp, fontWeight = FontWeight.Black)
            "title_blur" -> Text("BLUR", color = Color(0xFFCFD8DC), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            "title_milksweet" -> Text("Milksweet", color = Color(0xFF80DEEA), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            "title_laser" -> Text("Laser", color = Color(0xFFE0E0E0), fontSize = 14.sp, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Black)
            "title_rainbow" -> Text("RAINBOW", color = Color(0xFFFF4081), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            "title_soft_script" -> Text("Soft script", color = Color.White, fontSize = 13.sp, fontStyle = FontStyle.Italic)
            "title_3d_stack" -> Text("3D STACK", color = Color(0xFFFF1744), fontSize = 13.sp, fontWeight = FontWeight.Black)
            "title_handwriting" -> Text("Handwriting", color = Color(0xFFB0BEC5), fontSize = 12.sp, fontStyle = FontStyle.Italic)
            "title_gradient_color" -> Text("Gradient color", color = Color(0xFF00E676), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            "title_brownie" -> Text("Brownie", color = Color(0xFFFFCC80), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            "title_pink_cap" -> Text("PINK CAP", color = Color(0xFFF48FB1), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            "title_red_blue" -> Text("RED BLUE", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Black)
            "title_signature" -> Text("Signature.", color = Color(0xFFFFAB40), fontSize = 13.sp, fontStyle = FontStyle.Italic)
            "title_fun_dot" -> Text("FUN DOT", color = Color(0xFFFF5252), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            "title_doodle" -> Text("Doodle", color = Color(0xFFFF6D00), fontSize = 13.sp)

            else -> Text(item.title, color = TextWhite, fontSize = 12.sp)
        }
    }
}
