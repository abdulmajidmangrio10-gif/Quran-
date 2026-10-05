package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AnimationType
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
fun TextCaptionEditorScreen(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val textInput by viewModel.textInput.collectAsStateWithLifecycle()
    val textSubTab by viewModel.textSubTab.collectAsStateWithLifecycle()
    val textAnimation by viewModel.textAnimation.collectAsStateWithLifecycle()

    var isBold by remember { mutableStateOf(true) }
    var hasOutline by remember { mutableStateOf(true) }
    var hasShadow by remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        bottomBar = {
            // Apply Changes Button
            Button(
                onClick = { viewModel.applyEditedText() },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .height(48.dp)
                    .testTag("apply_text_button"),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = QuranGold,
                    contentColor = TextDark
                )
            ) {
                Text("Apply to Video", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
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
                    text = "Text & Caption",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            // Preview with Bounding Box and Resize Handles (matching reference image #4!)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
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
                                    Color(0xFF1B4D3E),
                                    Color(0xFF0F1E19)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Selected text with white dashed bounding box & corner grab handles
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.88f)
                            .border(1.5.dp, Color.White, RoundedCornerShape(4.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Four Corner Handles
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .size(8.dp)
                                .background(Color.White)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(8.dp)
                                .background(Color.White)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .size(8.dp)
                                .background(Color.White)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(8.dp)
                                .background(Color.White)
                        )

                        // Text content
                        Text(
                            text = textInput,
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )
                    }

                    // Bottom info bar (Timecode 00:18 / 01:24, Play, Fullscreen)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "00:18 / 01:24",
                            color = TextWhite,
                            fontSize = 11.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = TextWhite,
                                modifier = Modifier.size(18.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Fullscreen",
                                tint = TextWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-tabs: Edit Text | Style | Trim | Shadow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkCard)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("Edit Text", "Style", "Trim", "Shadow").forEachIndexed { idx, tabTitle ->
                    val isSelected = textSubTab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) DarkCardLighter else Color.Transparent)
                            .clickable { viewModel.setTextSubTab(idx) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tabTitle,
                            color = if (isSelected) QuranGold else TextWhite,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Text Input Field
            OutlinedTextField(
                value = textInput,
                onValueChange = { viewModel.setTextInput(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                placeholder = { Text("Enter text or Arabic/Urdu caption...", color = TextGray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard,
                    focusedBorderColor = QuranGold,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Formatting Icon Buttons Row (Font Aa, Style A, Color, Size 1, Bold B, Outline, Shadow)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkCard)
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextFormatButton("Font", "Aa") { }
                TextFormatButton("Style", "A") { }
                TextFormatColorButton("Color", Color.White) { }
                TextFormatButton("Size", "1") { }
                TextFormatButton("Bold", "B", isActive = isBold) { isBold = !isBold }
                TextFormatOutlineButton("Outline", isActive = hasOutline) { hasOutline = !hasOutline }
                TextFormatShadowButton("Shadow", isActive = hasShadow) { hasShadow = !hasShadow }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Animation Section
            Text(
                text = "Animation",
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // Animation cards row: [Fade In], [Slide Up] (active in reference), [Zoom In], [Fade Out]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnimationCardItem(
                    label = "Fade In",
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    isSelected = textAnimation == AnimationType.FADE_IN,
                    onClick = { viewModel.setTextAnimation(AnimationType.FADE_IN) },
                    modifier = Modifier.weight(1f)
                )

                AnimationCardItem(
                    label = "Slide Up",
                    icon = Icons.Default.ArrowUpward,
                    isSelected = textAnimation == AnimationType.SLIDE_UP,
                    onClick = { viewModel.setTextAnimation(AnimationType.SLIDE_UP) },
                    modifier = Modifier.weight(1f)
                )

                AnimationCardItem(
                    label = "Zoom In",
                    icon = Icons.Default.ZoomIn,
                    isSelected = textAnimation == AnimationType.ZOOM_IN,
                    onClick = { viewModel.setTextAnimation(AnimationType.ZOOM_IN) },
                    modifier = Modifier.weight(1f)
                )

                AnimationCardItem(
                    label = "Fade Out",
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    isSelected = textAnimation == AnimationType.FADE_OUT,
                    onClick = { viewModel.setTextAnimation(AnimationType.FADE_OUT) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TextFormatButton(
    label: String,
    symbol: String,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Text(
            text = symbol,
            color = if (isActive) QuranGold else TextWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isActive) QuranGold else TextGray,
            fontSize = 9.sp
        )
    }
}

@Composable
fun TextFormatColorButton(
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = TextGray,
            fontSize = 9.sp
        )
    }
}

@Composable
fun TextFormatOutlineButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .border(1.5.dp, if (isActive) QuranGold else TextWhite, RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isActive) QuranGold else TextGray,
            fontSize = 9.sp
        )
    }
}

@Composable
fun TextFormatShadowButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(if (isActive) QuranGold else Color(0xFF666677), RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isActive) QuranGold else TextGray,
            fontSize = 9.sp
        )
    }
}

@Composable
fun AnimationCardItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkCard)
            .border(
                1.5.dp,
                if (isSelected) QuranGold else DarkBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) QuranGold else TextWhite,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = if (isSelected) QuranGold else TextWhite,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}
