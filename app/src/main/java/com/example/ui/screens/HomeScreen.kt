package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.AspectRatioOption
import com.example.data.model.EditingProject
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardLighter
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.ProCardGradient
import com.example.ui.theme.ProNeonGradient
import com.example.ui.theme.ProPurpleGradient
import com.example.ui.theme.QuranGold
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.EditorViewModel

@Composable
fun HomeScreen(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var showProjectSetupDialog by remember { mutableStateOf(false) }
    var projectToRename by remember { mutableStateOf<EditingProject?>(null) }
    var renameTitleInput by remember { mutableStateOf("") }

    // Media pickers
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onMediaSelected(uri)
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onMediaSelected(uri, "Photo Project")
        }
    }

    // Filtered projects
    val filteredProjects = remember(projects, searchQuery) {
        if (searchQuery.isBlank()) projects
        else projects.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    // Project Setup Dialog (Matching Screen 06)
    if (showProjectSetupDialog) {
        ProjectSetupDialog(
            onDismiss = { showProjectSetupDialog = false },
            onLaunchBlank = { ratio, name ->
                showProjectSetupDialog = false
                viewModel.setupNewProject(name, ratio, null)
            },
            onPickVideo = { ratio, name ->
                showProjectSetupDialog = false
                videoPickerLauncher.launch("video/*")
            },
            onPickPhoto = { ratio, name ->
                showProjectSetupDialog = false
                photoPickerLauncher.launch("image/*")
            }
        )
    }

    // Rename Dialog
    if (projectToRename != null) {
        AlertDialog(
            onDismissRequest = { projectToRename = null },
            containerColor = DarkCard,
            title = {
                Text("Rename Project", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                OutlinedTextField(
                    value = renameTitleInput,
                    onValueChange = { renameTitleInput = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple,
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
                        projectToRename?.let { viewModel.renameProject(it.id, renameTitleInput) }
                        projectToRename = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Text("Save", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToRename = null }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        bottomBar = {
            HomeBottomNav(
                currentScreen = currentScreen,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            // TOP HEADER: Neon Logo, App Title, Settings/Templates Shortcut
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Glowing Neon Logo Badge
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ProNeonGradient)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Logo",
                                tint = NeonCyan,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Quran & Video Pro",
                                color = TextWhite,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.3.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ProNeonGradient)
                                    .padding(horizontal = 5.dp, vertical = 1.5.dp)
                            ) {
                                Text("PRO", color = TextDark, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Text(
                            text = "Hafiz Abdul Majid Mangrew • Premium Editor",
                            color = TextGray,
                            fontSize = 11.5.sp
                        )
                    }
                }

                // Templates / Quran shortcut
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.QURAN_EDITOR) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DarkCard)
                        .border(1.dp, DarkBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Quran Recitations",
                        tint = QuranGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // SEARCH BAR (Interactive Project & Template Search)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("home_search_bar"),
                placeholder = { Text("Search projects & drafts...", color = TextGray, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextGray, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard,
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // =================================================================
            // QUICK ACTION GRID (MATCHING SCREEN 04 IN REFERENCE IMAGE)
            // 4 Core Action Cards: New Project (+), Video, Photo, Collage
            // =================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. NEW PROJECT (Hero Gradient Card)
                Card(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(125.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { showProjectSetupDialog = true }
                        .testTag("action_new_project"),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ProPurpleGradient)
                            .padding(14.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = TextWhite, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("New Project", color = TextWhite, fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                            Text("Ratio & Canvas", color = TextWhite.copy(alpha = 0.8f), fontSize = 10.5.sp)
                        }
                    }
                }

                // 2. VIDEO (Quick Gallery Video Import)
                QuickActionItem(
                    title = "Video",
                    subtitle = "Gallery MP4",
                    icon = Icons.Default.Movie,
                    accentColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                ) {
                    videoPickerLauncher.launch("video/*")
                }

                // 3. PHOTO (Photo Editor / Overlay)
                QuickActionItem(
                    title = "Photo",
                    subtitle = "Single Edit",
                    icon = Icons.Default.PhotoLibrary,
                    accentColor = NeonPurple,
                    modifier = Modifier.weight(1f)
                ) {
                    photoPickerLauncher.launch("image/*")
                }

                // 4. COLLAGE (Photo Canvas Grid)
                QuickActionItem(
                    title = "Collage",
                    subtitle = "Grid Maker",
                    icon = Icons.Default.GridView,
                    accentColor = NeonPink,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.setupNewProject("Photo Collage", AspectRatioOption.RATIO_1_1, null)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // =================================================================
            // RECENT PROJECTS SECTION (MATCHING SCREEN 04 & 23)
            // =================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Recent Projects",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkCardLighter)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("${filteredProjects.size}", color = NeonPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "View All",
                    color = NeonCyan,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.PROJECTS) }
                )
            }

            if (filteredProjects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkCard)
                        .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Movie, contentDescription = null, tint = TextGray, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No matching projects found" else "No projects yet",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap 'New Project' or 'Video' above to start editing!",
                            color = TextGray,
                            fontSize = 11.5.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(end = 8.dp)
                ) {
                    items(filteredProjects) { project ->
                        ProProjectCard(
                            project = project,
                            onClick = { viewModel.openProject(project) },
                            onDuplicate = { viewModel.duplicateProject(project) },
                            onRename = {
                                projectToRename = project
                                renameTitleInput = project.title
                            },
                            onDelete = { viewModel.deleteProject(project.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Quran Ayah Templates Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { viewModel.navigateTo(AppScreen.QURAN_EDITOR) },
                colors = CardDefaults.cardColors(containerColor = DarkCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(QuranGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = QuranGold, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("114 Quran Surahs & Ayahs", color = TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                            Text("Arabic Calligraphy + Urdu Translations", color = QuranGold, fontSize = 11.sp)
                        }
                    }
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.QURAN_EDITOR) },
                        colors = ButtonDefaults.buttonColors(containerColor = QuranGold, contentColor = TextDark),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Explore", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// =============================================================================
// QUICK ACTION ITEM
// =============================================================================
@Composable
fun QuickActionItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(125.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            }

            Column {
                Text(title, color = TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = TextGray, fontSize = 10.sp)
            }
        }
    }
}

// =============================================================================
// PROJECT SETUP DIALOG (SCREEN 06 IN REFERENCE)
// =============================================================================
@Composable
fun ProjectSetupDialog(
    onDismiss: () -> Unit,
    onLaunchBlank: (AspectRatioOption, String) -> Unit,
    onPickVideo: (AspectRatioOption, String) -> Unit,
    onPickPhoto: (AspectRatioOption, String) -> Unit
) {
    var selectedRatio by remember { mutableStateOf(AspectRatioOption.RATIO_9_16) }
    var projectName by remember { mutableStateOf("My Awesome Video") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Project Setup", color = TextWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray, modifier = Modifier.size(18.dp))
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // 1. Ratio Selection Row (9:16, 16:9, 1:1, 4:5)
                Text("Select Aspect Ratio", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val ratios = listOf(
                        Triple(AspectRatioOption.RATIO_9_16, "9:16", "TikTok/Reels"),
                        Triple(AspectRatioOption.RATIO_16_9, "16:9", "YouTube"),
                        Triple(AspectRatioOption.RATIO_1_1, "1:1", "Instagram"),
                        Triple(AspectRatioOption.RATIO_4_5, "4:5", "Portrait")
                    )

                    ratios.forEach { (ratio, label, platform) ->
                        val isSelected = selectedRatio == ratio
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) NeonPurple.copy(alpha = 0.2f) else DarkCardLighter)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) NeonPurple else DarkBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedRatio = ratio }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    label,
                                    color = if (isSelected) NeonCyan else TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    platform,
                                    color = TextGray,
                                    fontSize = 8.5.sp
                                )
                            }
                        }
                    }
                }

                // 2. Project Name
                Text("Project Name", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkCardLighter,
                        unfocusedContainerColor = DarkCardLighter,
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                // 3. Media Sources
                Text("Choose Media Source", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onPickVideo(selectedRatio, projectName) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Video", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onPickPhoto(selectedRatio, projectName) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Photo", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = { onLaunchBlank(selectedRatio, projectName) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = QuranGold)
                ) {
                    Text("Start Blank Canvas (Quran/Text)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {}
    )
}

// =============================================================================
// PRO RECENT PROJECT CARD
// =============================================================================
@Composable
fun ProProjectCard(
    project: EditingProject,
    onClick: () -> Unit,
    onDuplicate: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val durSec = (project.durationMs / 1000).coerceAtLeast(0)
    val durFormatted = String.format(java.util.Locale.US, "%02d:%02d", durSec / 60, durSec % 60)

    Column(
        modifier = modifier
            .width(160.dp)
            .clickable(onClick = onClick)
            .testTag("project_card_${project.id}")
    ) {
        // Thumbnail Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ProCardGradient)
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (!project.thumbnailUri.isNullOrEmpty()) {
                AsyncImage(
                    model = project.thumbnailUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF2E1065), Color(0xFF1E1B4B), Color(0xFF0F172A))
                            )
                        )
                )
            }

            // Translucent play circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x99000000)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = TextWhite,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Duration badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xCC000000))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = durFormatted,
                    color = TextWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Title and 3-dot dropdown menu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    color = TextWhite,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = project.createdAt,
                    color = TextGray,
                    fontSize = 10.sp
                )
            }

            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = TextGray, modifier = Modifier.size(16.dp))
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(DarkCardLighter)
                ) {
                    DropdownMenuItem(
                        text = { Text("Reopen to Edit", color = TextWhite, fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.PlayArrow, null, tint = NeonPurple, modifier = Modifier.size(16.dp)) },
                        onClick = {
                            showMenu = false
                            onClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Duplicate", color = TextWhite, fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.ContentCopy, null, tint = NeonCyan, modifier = Modifier.size(16.dp)) },
                        onClick = {
                            showMenu = false
                            onDuplicate()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Rename", color = TextWhite, fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Edit, null, tint = QuranGold, modifier = Modifier.size(16.dp)) },
                        onClick = {
                            showMenu = false
                            onRename()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = Color(0xFFEF4444), fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp)) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

// =============================================================================
// PRO BOTTOM NAVIGATION
// =============================================================================
@Composable
fun HomeBottomNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = DarkCard,
        contentColor = TextWhite,
        modifier = Modifier
            .navigationBarsPadding()
            .height(64.dp)
    ) {
        NavigationBarItem(
            selected = currentScreen == AppScreen.HOME,
            onClick = { onNavigate(AppScreen.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home", fontSize = 11.5.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonPurple,
                selectedTextColor = NeonPurple,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = NeonPurple.copy(alpha = 0.2f)
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.QURAN_EDITOR,
            onClick = { onNavigate(AppScreen.QURAN_EDITOR) },
            icon = {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "Templates"
                )
            },
            label = { Text("Templates", fontSize = 11.5.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = QuranGold,
                selectedTextColor = QuranGold,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = QuranGold.copy(alpha = 0.2f)
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.PROJECTS,
            onClick = { onNavigate(AppScreen.PROJECTS) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.PROJECTS) Icons.Filled.Folder else Icons.Outlined.Folder,
                    contentDescription = "Projects"
                )
            },
            label = { Text("Drafts", fontSize = 11.5.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonCyan,
                selectedTextColor = NeonCyan,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = NeonCyan.copy(alpha = 0.2f)
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.SETTINGS,
            onClick = { onNavigate(AppScreen.SETTINGS) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = "Settings"
                )
            },
            label = { Text("Settings", fontSize = 11.5.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonPurple,
                selectedTextColor = NeonPurple,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = NeonPurple.copy(alpha = 0.2f)
            )
        )
    }
}
