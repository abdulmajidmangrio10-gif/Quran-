package com.example.ui.viewmodel

import android.app.Application
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ProjectRepository
import com.example.data.model.AnimationType
import com.example.data.model.AppSettings
import com.example.data.model.AspectRatioOption
import com.example.data.model.AudioClip
import com.example.data.model.CanvasBgType
import com.example.data.model.CanvasConfig
import com.example.data.model.EditingProject
import com.example.data.model.EffectType
import com.example.data.model.FilterType
import com.example.data.model.ImageClip
import com.example.data.model.QuranBackgroundType
import com.example.data.model.QuranClip
import com.example.data.model.StickerClip
import com.example.data.model.TextClip
import com.example.data.model.VideoClipConfig
import com.example.data.quran.AyahItem
import com.example.data.quran.QuranData
import com.example.data.quran.SurahMeta
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class AppScreen {
    HOME,
    PROJECTS,
    MAIN_EDITOR,
    QURAN_EDITOR,
    TEXT_EDITOR,
    MEDIA_EFFECTS,
    SETTINGS
}

enum class EditorModal {
    NONE,
    CANVAS,
    VIDEO,
    SPEED,
    AUTO_CAPTION,
    STICKER,
    EXPORT
}

enum class BottomToolType {
    TIMELINE,
    QURAN,
    TEXT,
    TEXT_TEMPLATES,
    ADJUST,
    FILTER,
    EFFECTS,
    AUDIO,
    CANVAS,
    SPEED,
    STICKER,
    AUTO_CAPTION,
    CUSTOMIZE_TOOLBAR
}

data class ToolbarFeatureItem(
    val id: String,
    val name: String,
    val isVisible: Boolean = true
)

enum class TrackType {
    NONE,
    VIDEO,
    QURAN,
    TEXT,
    AUDIO,
    IMAGE
}

class EditorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProjectRepository(AppDatabase.getInstance(application).projectDao())

    val projects: StateFlow<List<EditingProject>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Screen and Modal Navigation
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _activeModal = MutableStateFlow(EditorModal.NONE)
    val activeModal: StateFlow<EditorModal> = _activeModal.asStateFlow()

    // Active project being edited - clean initial default project
    private val _currentProject = MutableStateFlow(
        EditingProject(
            id = "default-project",
            title = "New Quran Video",
            videoUri = null,
            thumbnailUri = null,
            durationMs = 60000L,
            createdAt = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date()),
            quranClips = emptyList(),
            textClips = emptyList(),
            audioClips = emptyList()
        )
    )
    val currentProject: StateFlow<EditingProject> = _currentProject.asStateFlow()

    // Undo / Redo history
    private val undoStack = mutableListOf<EditingProject>()
    private val redoStack = mutableListOf<EditingProject>()

    // Playback state
    private val _currentPlayheadMs = MutableStateFlow(0L)
    val currentPlayheadMs: StateFlow<Long> = _currentPlayheadMs.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _isFullscreen = MutableStateFlow(false)
    val isFullscreen: StateFlow<Boolean> = _isFullscreen.asStateFlow()

    // Track Selection & Timeline Zoom
    private val _selectedTrack = MutableStateFlow(TrackType.VIDEO)
    val selectedTrack: StateFlow<TrackType> = _selectedTrack.asStateFlow()

    private val _selectedClipId = MutableStateFlow<String?>(null)
    val selectedClipId: StateFlow<String?> = _selectedClipId.asStateFlow()

    private val _timelineZoom = MutableStateFlow(1.0f) // 0.5f (zoomed out) to 3.0f (zoomed in)
    val timelineZoom: StateFlow<Float> = _timelineZoom.asStateFlow()

    // Inline Bottom Tool Panel State (Video stays visible on top)
    private val _activeBottomTool = MutableStateFlow(BottomToolType.TIMELINE)
    val activeBottomTool: StateFlow<BottomToolType> = _activeBottomTool.asStateFlow()

    // Customizable Toolbar Features
    private val _toolbarFeatures = MutableStateFlow(
        listOf(
            ToolbarFeatureItem("timeline", "Timeline"),
            ToolbarFeatureItem("quran", "Quran"),
            ToolbarFeatureItem("text", "Text"),
            ToolbarFeatureItem("templates", "Templates"),
            ToolbarFeatureItem("adjust", "Adjust"),
            ToolbarFeatureItem("filter", "Filter"),
            ToolbarFeatureItem("effects", "Effects"),
            ToolbarFeatureItem("canvas", "Canvas"),
            ToolbarFeatureItem("audio", "Audio"),
            ToolbarFeatureItem("speed", "Speed"),
            ToolbarFeatureItem("sticker", "Sticker"),
            ToolbarFeatureItem("autocaption", "Auto Caption")
        )
    )
    val toolbarFeatures: StateFlow<List<ToolbarFeatureItem>> = _toolbarFeatures.asStateFlow()

    fun openBottomTool(tool: BottomToolType) {
        _activeBottomTool.value = tool
    }

    fun closeBottomTool() {
        _activeBottomTool.value = BottomToolType.TIMELINE
    }

    fun moveToolbarFeature(index: Int, direction: Int) {
        val current = _toolbarFeatures.value.toMutableList()
        val targetIndex = index + direction
        if (index in current.indices && targetIndex in current.indices) {
            val item = current.removeAt(index)
            current.add(targetIndex, item)
            _toolbarFeatures.value = current
        }
    }

    fun toggleToolbarFeatureVisibility(id: String) {
        val current = _toolbarFeatures.value.map {
            if (it.id == id) it.copy(isVisible = !it.isVisible) else it
        }
        _toolbarFeatures.value = current
    }

    fun resetToolbarFeatures() {
        _toolbarFeatures.value = listOf(
            ToolbarFeatureItem("timeline", "Timeline"),
            ToolbarFeatureItem("quran", "Quran"),
            ToolbarFeatureItem("text", "Text"),
            ToolbarFeatureItem("templates", "Templates"),
            ToolbarFeatureItem("adjust", "Adjust"),
            ToolbarFeatureItem("filter", "Filter"),
            ToolbarFeatureItem("effects", "Effects"),
            ToolbarFeatureItem("canvas", "Canvas"),
            ToolbarFeatureItem("audio", "Audio"),
            ToolbarFeatureItem("speed", "Speed"),
            ToolbarFeatureItem("sticker", "Sticker"),
            ToolbarFeatureItem("autocaption", "Auto Caption")
        )
    }

    fun applyTextTemplate(templateId: String) {
        pushSnapshot()
        val existingClips = _currentProject.value.textClips
        val playhead = _currentPlayheadMs.value

        val updated = if (existingClips.isNotEmpty()) {
            val first = existingClips[0].copy(
                templateId = templateId
            )
            listOf(first) + existingClips.drop(1)
        } else {
            listOf(
                TextClip(
                    id = java.util.UUID.randomUUID().toString(),
                    text = "Original subtitle shown here",
                    startTimeMs = playhead,
                    durationMs = 8000L,
                    templateId = templateId
                )
            )
        }
        _currentProject.value = _currentProject.value.copy(textClips = updated)
        _selectedTrack.value = TrackType.TEXT
        _selectedClipId.value = updated.firstOrNull()?.id
        saveCurrentProject()
    }

    // Quran Editor State
    private val _quranSearchQuery = MutableStateFlow("")
    val quranSearchQuery: StateFlow<String> = _quranSearchQuery.asStateFlow()

    private val _selectedSurah = MutableStateFlow(QuranData.ALL_SURAHS[93]) // Al-Inshirah (94)
    val selectedSurah: StateFlow<SurahMeta> = _selectedSurah.asStateFlow()

    private val _selectedAyah = MutableStateFlow(QuranData.getAyah(94, 6)) // "إِنَّ مَعَ الْعُسْرِ يُسْرًا"
    val selectedAyah: StateFlow<AyahItem> = _selectedAyah.asStateFlow()

    private val _quranFont = MutableStateFlow("Al-Fatihah")
    val quranFont: StateFlow<String> = _quranFont.asStateFlow()

    private val _quranFontSize = MutableStateFlow(26f)
    val quranFontSize: StateFlow<Float> = _quranFontSize.asStateFlow()

    private val _quranArabicColor = MutableStateFlow(0xFFFFFFFF)
    val quranArabicColor: StateFlow<Long> = _quranArabicColor.asStateFlow()

    private val _quranTranslationColor = MutableStateFlow(0xFFE6B74A)
    val quranTranslationColor: StateFlow<Long> = _quranTranslationColor.asStateFlow()

    private val _quranHasOutline = MutableStateFlow(true)
    val quranHasOutline: StateFlow<Boolean> = _quranHasOutline.asStateFlow()

    private val _quranHasShadow = MutableStateFlow(true)
    val quranHasShadow: StateFlow<Boolean> = _quranHasShadow.asStateFlow()

    private val _quranBackground = MutableStateFlow(QuranBackgroundType.ROUNDED_BOX)
    val quranBackground: StateFlow<QuranBackgroundType> = _quranBackground.asStateFlow()

    private val _quranAnimation = MutableStateFlow(AnimationType.SLIDE_UP)
    val quranAnimation: StateFlow<AnimationType> = _quranAnimation.asStateFlow()

    // Text Editor State
    private val _textInput = MutableStateFlow("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ\nشروع اللہ کے نام سے جو نہایت مہربان، رحم کرنے والا ہے")
    val textInput: StateFlow<String> = _textInput.asStateFlow()

    private val _textSubTab = MutableStateFlow(0)
    val textSubTab: StateFlow<Int> = _textSubTab.asStateFlow()

    private val _textAnimation = MutableStateFlow(AnimationType.SLIDE_UP)
    val textAnimation: StateFlow<AnimationType> = _textAnimation.asStateFlow()

    // Media & Effects tab
    private val _mediaEffectsTab = MutableStateFlow(0)
    val mediaEffectsTab: StateFlow<Int> = _mediaEffectsTab.asStateFlow()

    // Settings
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    // Export Process state
    private val _exportProgress = MutableStateFlow(0f)
    val exportProgress: StateFlow<Float> = _exportProgress.asStateFlow()

    private val _exportStage = MutableStateFlow("")
    val exportStage: StateFlow<String> = _exportStage.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _exportCompleted = MutableStateFlow(false)
    val exportCompleted: StateFlow<Boolean> = _exportCompleted.asStateFlow()

    // Auto-caption state
    private val _autoCaptionStatus = MutableStateFlow("Ready to analyze audio")
    val autoCaptionStatus: StateFlow<String> = _autoCaptionStatus.asStateFlow()

    private val _isAnalyzingCaption = MutableStateFlow(false)
    val isAnalyzingCaption: StateFlow<Boolean> = _isAnalyzingCaption.asStateFlow()

    private var playbackJob: Job? = null

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun openModal(modal: EditorModal) {
        _activeModal.value = modal
    }

    fun closeModal() {
        _activeModal.value = EditorModal.NONE
    }

    private fun pushSnapshot() {
        undoStack.add(_currentProject.value.copy())
        if (undoStack.size > 30) undoStack.removeAt(0)
        redoStack.clear()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val prev = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(_currentProject.value.copy())
            _currentProject.value = prev
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(_currentProject.value.copy())
            _currentProject.value = next
        }
    }

    fun createBlankProject() {
        val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date())
        val defaultDurationMs = 15000L
        val blankClip = com.example.data.model.VideoTimelineClip(
            id = UUID.randomUUID().toString(),
            uri = null,
            name = "Blank Canvas",
            isBlank = true,
            startTimeMs = 0L,
            durationMs = defaultDurationMs
        )
        val newProj = EditingProject(
            id = UUID.randomUUID().toString(),
            title = "Project ${projects.value.size + 1}",
            videoUri = null,
            thumbnailUri = null,
            createdAt = dateStr,
            durationMs = defaultDurationMs,
            videoConfig = VideoClipConfig(
                startTimeMs = 0L,
                endTimeMs = defaultDurationMs
            ),
            videoClips = listOf(blankClip),
            quranClips = emptyList(),
            textClips = emptyList(),
            audioClips = emptyList(),
            imageClips = emptyList()
        )
        pushSnapshot()
        _currentProject.value = newProj
        _currentPlayheadMs.value = 0L
        _selectedTrack.value = TrackType.VIDEO
        _selectedClipId.value = blankClip.id
        _currentScreen.value = AppScreen.MAIN_EDITOR

        viewModelScope.launch {
            repository.saveProject(newProj)
        }
    }

    fun onMediaSelected(uri: Uri, title: String? = null) {
        val context = getApplication<Application>()
        val mime = context.contentResolver.getType(uri) ?: ""
        if (mime.startsWith("image/")) {
            val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date())
            val durationMs = 10000L
            val blankClip = com.example.data.model.VideoTimelineClip(
                id = UUID.randomUUID().toString(),
                uri = null,
                name = "Canvas",
                isBlank = true,
                startTimeMs = 0L,
                durationMs = durationMs
            )
            val imgClip = com.example.data.model.ImageClip(
                id = UUID.randomUUID().toString(),
                uri = uri.toString(),
                startTimeMs = 0L,
                durationMs = durationMs
            )
            val newProj = EditingProject(
                id = UUID.randomUUID().toString(),
                title = title ?: "Project ${projects.value.size + 1}",
                videoUri = null,
                thumbnailUri = uri.toString(),
                createdAt = dateStr,
                durationMs = durationMs,
                videoConfig = VideoClipConfig(
                    startTimeMs = 0L,
                    endTimeMs = durationMs
                ),
                videoClips = listOf(blankClip),
                imageClips = listOf(imgClip),
                quranClips = emptyList(),
                textClips = emptyList(),
                audioClips = emptyList()
            )
            pushSnapshot()
            _currentProject.value = newProj
            _currentPlayheadMs.value = 0L
            _selectedTrack.value = TrackType.IMAGE
            _selectedClipId.value = imgClip.id
            _currentScreen.value = AppScreen.MAIN_EDITOR

            viewModelScope.launch {
                repository.saveProject(newProj)
            }
        } else {
            onVideoSelected(uri, title)
        }
    }

    // Video selection from gallery with real duration extraction
    fun onVideoSelected(uri: Uri, title: String? = null) {
        var videoDurationMs = 60000L
        val context = getApplication<Application>()

        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            val dur = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
            if (dur != null && dur > 1000L) {
                videoDurationMs = dur
            }
        } catch (_: Exception) {
            // fallback
        } finally {
            try { retriever.release() } catch (_: Exception) {}
        }

        val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date())
        val initialVideoClip = com.example.data.model.VideoTimelineClip(
            id = UUID.randomUUID().toString(),
            uri = uri.toString(),
            name = title ?: "Video 1",
            startTimeMs = 0L,
            durationMs = videoDurationMs
        )
        val newProj = EditingProject(
            id = UUID.randomUUID().toString(),
            title = title ?: "Project ${projects.value.size + 1}",
            videoUri = uri.toString(),
            thumbnailUri = uri.toString(),
            createdAt = dateStr,
            durationMs = videoDurationMs,
            videoConfig = VideoClipConfig(
                startTimeMs = 0L,
                endTimeMs = videoDurationMs
            ),
            videoClips = listOf(initialVideoClip),
            quranClips = emptyList(),
            textClips = emptyList(),
            audioClips = emptyList()
        )
        pushSnapshot()
        _currentProject.value = newProj
        _currentPlayheadMs.value = 0L
        _selectedTrack.value = TrackType.VIDEO
        _selectedClipId.value = initialVideoClip.id
        _currentScreen.value = AppScreen.MAIN_EDITOR

        viewModelScope.launch {
            repository.saveProject(newProj)
        }
    }

    fun openProject(project: EditingProject) {
        pushSnapshot()
        val populatedProject = if (project.videoClips.isEmpty() && project.videoUri != null) {
            project.copy(
                videoClips = listOf(
                    com.example.data.model.VideoTimelineClip(
                        id = UUID.randomUUID().toString(),
                        uri = project.videoUri,
                        name = project.title,
                        startTimeMs = 0L,
                        durationMs = project.durationMs
                    )
                )
            )
        } else {
            project
        }
        _currentProject.value = populatedProject
        _currentPlayheadMs.value = 0L
        _selectedTrack.value = TrackType.VIDEO
        _selectedClipId.value = populatedProject.videoClips.firstOrNull()?.id
        _currentScreen.value = AppScreen.MAIN_EDITOR
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
        }
    }

    private var audioPlayer: MediaPlayer? = null
    private var currentlyPlayingAudioClipId: String? = null

    // Playback control
    fun togglePlayPause() {
        if (_isPlaying.value) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val total = _currentProject.value.durationMs
            while (_isPlaying.value) {
                delay(100)
                val speed = _currentProject.value.videoConfig.speed.coerceIn(0.25f, 3.0f)
                val step = (100 * speed).toLong()
                var next = _currentPlayheadMs.value + step
                if (next >= total) {
                    next = 0
                }
                _currentPlayheadMs.value = next
                syncAudioPlayback(next)
            }
        }
    }

    private fun pausePlayback() {
        _isPlaying.value = false
        playbackJob?.cancel()
        try {
            audioPlayer?.pause()
        } catch (_: Exception) {}
    }

    private fun syncAudioPlayback(currentMs: Long) {
        val clips = _currentProject.value.audioClips
        val activeAudio = clips.find { currentMs >= it.startTimeMs && currentMs < (it.startTimeMs + it.durationMs) }
        val globalMuted = _isMuted.value

        if (activeAudio == null || globalMuted || activeAudio.volume <= 0f) {
            stopAudioPlayer()
            return
        }

        if (currentlyPlayingAudioClipId != activeAudio.id) {
            stopAudioPlayer()
            startAudioClip(activeAudio, currentMs)
        } else {
            val vol = activeAudio.volume.coerceIn(0f, 1f)
            try {
                audioPlayer?.setVolume(vol, vol)
            } catch (_: Exception) {}
        }
    }

    private fun startAudioClip(clip: AudioClip, currentMs: Long) {
        if (clip.uri == null) return
        try {
            currentlyPlayingAudioClipId = clip.id
            val ctx = getApplication<Application>()
            audioPlayer = MediaPlayer().apply {
                setDataSource(ctx, Uri.parse(clip.uri))
                setOnPreparedListener { mp ->
                    val offset = (currentMs - clip.startTimeMs).coerceAtLeast(0L).toInt()
                    mp.seekTo(offset)
                    val vol = if (_isMuted.value) 0f else clip.volume.coerceIn(0f, 1f)
                    mp.setVolume(vol, vol)
                    if (_isPlaying.value) {
                        mp.start()
                    }
                }
                prepareAsync()
            }
        } catch (_: Exception) {}
    }

    private fun stopAudioPlayer() {
        try {
            audioPlayer?.stop()
            audioPlayer?.release()
        } catch (_: Exception) {}
        audioPlayer = null
        currentlyPlayingAudioClipId = null
    }

    fun seekTo(timeMs: Long) {
        val total = _currentProject.value.durationMs
        _currentPlayheadMs.value = timeMs.coerceIn(0L, total)
        if (_isPlaying.value) {
            syncAudioPlayback(_currentPlayheadMs.value)
        } else {
            stopAudioPlayer()
        }
    }

    fun stepForward() {
        seekTo(_currentPlayheadMs.value + 2000L)
    }

    fun stepBackward() {
        seekTo(_currentPlayheadMs.value - 2000L)
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
        val vol = if (_isMuted.value) 0f else 1f
        try {
            audioPlayer?.setVolume(vol, vol)
        } catch (_: Exception) {}
    }

    fun toggleFullscreen() {
        _isFullscreen.value = !_isFullscreen.value
    }

    fun selectTrack(track: TrackType, clipId: String? = null) {
        _selectedTrack.value = track
        _selectedClipId.value = clipId
    }

    // Timeline Zoom Control
    fun setTimelineZoom(zoom: Float) {
        _timelineZoom.value = zoom.coerceIn(0.5f, 3.5f)
    }

    fun zoomInTimeline() {
        setTimelineZoom(_timelineZoom.value + 0.35f)
    }

    fun zoomOutTimeline() {
        setTimelineZoom(_timelineZoom.value - 0.35f)
    }

    // =====================================================================
    // TIMELINE CLIP EDITING: LENGTHEN, SHORTEN, MOVE, SPLIT, DELETE
    // =====================================================================

    fun adjustSelectedClipDuration(deltaMs: Long) {
        pushSnapshot()
        val totalDuration = _currentProject.value.durationMs
        val clipId = _selectedClipId.value

        when (_selectedTrack.value) {
            TrackType.QURAN -> {
                val clips = _currentProject.value.quranClips.toMutableList()
                val idx = if (clipId != null) clips.indexOfFirst { it.id == clipId } else 0
                if (idx in clips.indices) {
                    val clip = clips[idx]
                    val maxDur = totalDuration - clip.startTimeMs
                    val newDur = (clip.durationMs + deltaMs).coerceIn(1000L, maxDur.coerceAtLeast(1000L))
                    clips[idx] = clip.copy(durationMs = newDur)
                    _currentProject.value = _currentProject.value.copy(quranClips = clips)
                    saveCurrentProject()
                }
            }
            TrackType.TEXT -> {
                val clips = _currentProject.value.textClips.toMutableList()
                val idx = if (clipId != null) clips.indexOfFirst { it.id == clipId } else 0
                if (idx in clips.indices) {
                    val clip = clips[idx]
                    val maxDur = totalDuration - clip.startTimeMs
                    val newDur = (clip.durationMs + deltaMs).coerceIn(1000L, maxDur.coerceAtLeast(1000L))
                    clips[idx] = clip.copy(durationMs = newDur)
                    _currentProject.value = _currentProject.value.copy(textClips = clips)
                    saveCurrentProject()
                }
            }
            TrackType.AUDIO -> {
                val clips = _currentProject.value.audioClips.toMutableList()
                val idx = if (clipId != null) clips.indexOfFirst { it.id == clipId } else 0
                if (idx in clips.indices) {
                    val clip = clips[idx]
                    val maxDur = totalDuration - clip.startTimeMs
                    val newDur = (clip.durationMs + deltaMs).coerceIn(1000L, maxDur.coerceAtLeast(1000L))
                    clips[idx] = clip.copy(durationMs = newDur)
                    _currentProject.value = _currentProject.value.copy(audioClips = clips)
                    saveCurrentProject()
                }
            }
            TrackType.VIDEO -> {
                val cfg = _currentProject.value.videoConfig
                val newEnd = (cfg.endTimeMs + deltaMs).coerceIn(cfg.startTimeMs + 2000L, 600000L)
                _currentProject.value = _currentProject.value.copy(
                    durationMs = newEnd,
                    videoConfig = cfg.copy(endTimeMs = newEnd)
                )
                saveCurrentProject()
            }
            else -> {}
        }
    }

    fun adjustSelectedClipStart(deltaMs: Long) {
        pushSnapshot()
        val totalDuration = _currentProject.value.durationMs
        val clipId = _selectedClipId.value

        when (_selectedTrack.value) {
            TrackType.QURAN -> {
                val clips = _currentProject.value.quranClips.toMutableList()
                val idx = if (clipId != null) clips.indexOfFirst { it.id == clipId } else 0
                if (idx in clips.indices) {
                    val clip = clips[idx]
                    val maxStart = (totalDuration - clip.durationMs).coerceAtLeast(0L)
                    val newStart = (clip.startTimeMs + deltaMs).coerceIn(0L, maxStart)
                    clips[idx] = clip.copy(startTimeMs = newStart)
                    _currentProject.value = _currentProject.value.copy(quranClips = clips)
                    saveCurrentProject()
                }
            }
            TrackType.TEXT -> {
                val clips = _currentProject.value.textClips.toMutableList()
                val idx = if (clipId != null) clips.indexOfFirst { it.id == clipId } else 0
                if (idx in clips.indices) {
                    val clip = clips[idx]
                    val maxStart = (totalDuration - clip.durationMs).coerceAtLeast(0L)
                    val newStart = (clip.startTimeMs + deltaMs).coerceIn(0L, maxStart)
                    clips[idx] = clip.copy(startTimeMs = newStart)
                    _currentProject.value = _currentProject.value.copy(textClips = clips)
                    saveCurrentProject()
                }
            }
            TrackType.AUDIO -> {
                val clips = _currentProject.value.audioClips.toMutableList()
                val idx = if (clipId != null) clips.indexOfFirst { it.id == clipId } else 0
                if (idx in clips.indices) {
                    val clip = clips[idx]
                    val maxStart = (totalDuration - clip.durationMs).coerceAtLeast(0L)
                    val newStart = (clip.startTimeMs + deltaMs).coerceIn(0L, maxStart)
                    clips[idx] = clip.copy(startTimeMs = newStart)
                    _currentProject.value = _currentProject.value.copy(audioClips = clips)
                    saveCurrentProject()
                }
            }
            else -> {}
        }
    }

    fun onDragGestureStarted() {
        pushSnapshot()
    }

    fun onDragGestureEnded() {
        saveCurrentProject()
    }

    fun trimClipStart(track: TrackType, clipId: String, deltaMs: Long) {
        when (track) {
            TrackType.VIDEO -> {
                val list = _currentProject.value.videoClips.map { clip ->
                    if (clip.id == clipId) {
                        val currentEnd = clip.startTimeMs + clip.durationMs
                        val newStart = (clip.startTimeMs + deltaMs).coerceIn(0L, currentEnd - 500L)
                        val newDur = (currentEnd - newStart).coerceAtLeast(500L)
                        clip.copy(startTimeMs = newStart, durationMs = newDur)
                    } else clip
                }
                _currentProject.value = _currentProject.value.copy(videoClips = list)
            }
            TrackType.QURAN -> {
                val list = _currentProject.value.quranClips.map { clip ->
                    if (clip.id == clipId) {
                        val currentEnd = clip.startTimeMs + clip.durationMs
                        val newStart = (clip.startTimeMs + deltaMs).coerceIn(0L, currentEnd - 500L)
                        val newDur = (currentEnd - newStart).coerceAtLeast(500L)
                        clip.copy(startTimeMs = newStart, durationMs = newDur)
                    } else clip
                }
                _currentProject.value = _currentProject.value.copy(quranClips = list)
            }
            TrackType.TEXT -> {
                val list = _currentProject.value.textClips.map { clip ->
                    if (clip.id == clipId) {
                        val currentEnd = clip.startTimeMs + clip.durationMs
                        val newStart = (clip.startTimeMs + deltaMs).coerceIn(0L, currentEnd - 500L)
                        val newDur = (currentEnd - newStart).coerceAtLeast(500L)
                        clip.copy(startTimeMs = newStart, durationMs = newDur)
                    } else clip
                }
                _currentProject.value = _currentProject.value.copy(textClips = list)
            }
            TrackType.AUDIO -> {
                val list = _currentProject.value.audioClips.map { clip ->
                    if (clip.id == clipId) {
                        val currentEnd = clip.startTimeMs + clip.durationMs
                        val newStart = (clip.startTimeMs + deltaMs).coerceIn(0L, currentEnd - 500L)
                        val newDur = (currentEnd - newStart).coerceAtLeast(500L)
                        clip.copy(startTimeMs = newStart, durationMs = newDur)
                    } else clip
                }
                _currentProject.value = _currentProject.value.copy(audioClips = list)
            }
            TrackType.IMAGE -> {
                val list = _currentProject.value.imageClips.map { clip ->
                    if (clip.id == clipId) {
                        val currentEnd = clip.startTimeMs + clip.durationMs
                        val newStart = (clip.startTimeMs + deltaMs).coerceIn(0L, currentEnd - 500L)
                        val newDur = (currentEnd - newStart).coerceAtLeast(500L)
                        clip.copy(startTimeMs = newStart, durationMs = newDur)
                    } else clip
                }
                _currentProject.value = _currentProject.value.copy(imageClips = list)
            }
            else -> {}
        }
    }

    fun trimClipEnd(track: TrackType, clipId: String, deltaMs: Long) {
        when (track) {
            TrackType.VIDEO -> {
                val list = _currentProject.value.videoClips.map { clip ->
                    if (clip.id == clipId) {
                        val newDur = (clip.durationMs + deltaMs).coerceAtLeast(500L)
                        clip.copy(durationMs = newDur)
                    } else clip
                }
                _currentProject.value = _currentProject.value.copy(videoClips = list)
            }
            TrackType.QURAN -> {
                val list = _currentProject.value.quranClips.map { clip ->
                    if (clip.id == clipId) {
                        val newDur = (clip.durationMs + deltaMs).coerceAtLeast(500L)
                        clip.copy(durationMs = newDur)
                    } else clip
                }
                _currentProject.value = _currentProject.value.copy(quranClips = list)
            }
            TrackType.TEXT -> {
                val list = _currentProject.value.textClips.map { clip ->
                    if (clip.id == clipId) {
                        val newDur = (clip.durationMs + deltaMs).coerceAtLeast(500L)
                        clip.copy(durationMs = newDur)
                    } else clip
                }
                _currentProject.value = _currentProject.value.copy(textClips = list)
            }
            TrackType.AUDIO -> {
                val list = _currentProject.value.audioClips.map { clip ->
                    if (clip.id == clipId) {
                        val newDur = (clip.durationMs + deltaMs).coerceAtLeast(500L)
                        clip.copy(durationMs = newDur)
                    } else clip
                }
                _currentProject.value = _currentProject.value.copy(audioClips = list)
            }
            TrackType.IMAGE -> {
                val list = _currentProject.value.imageClips.map { clip ->
                    if (clip.id == clipId) {
                        val newDur = (clip.durationMs + deltaMs).coerceAtLeast(500L)
                        clip.copy(durationMs = newDur)
                    } else clip
                }
                _currentProject.value = _currentProject.value.copy(imageClips = list)
            }
            else -> {}
        }
    }

    fun moveClip(track: TrackType, clipId: String, deltaMs: Long) {
        when (track) {
            TrackType.VIDEO -> {
                val list = _currentProject.value.videoClips.map { clip ->
                    if (clip.id == clipId) {
                        val newStart = (clip.startTimeMs + deltaMs).coerceAtLeast(0L)
                        clip.copy(startTimeMs = newStart)
                    } else clip
                }
                _currentProject.value = _currentProject.value.copy(videoClips = list)
            }
            TrackType.QURAN -> {
                val list = _currentProject.value.quranClips.map { clip ->
                    if (clip.id == clipId) {
                        val newStart = (clip.startTimeMs + deltaMs).coerceAtLeast(0L)
                        clip.copy(startTimeMs = newStart)
                    } else clip
                }
                _currentProject.value = _currentProject.value.copy(quranClips = list)
            }
            TrackType.TEXT -> {
                val list = _currentProject.value.textClips.map { clip ->
                    if (clip.id == clipId) {
                        val newStart = (clip.startTimeMs + deltaMs).coerceAtLeast(0L)
                        clip.copy(startTimeMs = newStart)
                    } else clip
                }
                _currentProject.value = _currentProject.value.copy(textClips = list)
            }
            TrackType.AUDIO -> {
                val list = _currentProject.value.audioClips.map { clip ->
                    if (clip.id == clipId) {
                        val newStart = (clip.startTimeMs + deltaMs).coerceAtLeast(0L)
                        clip.copy(startTimeMs = newStart)
                    } else clip
                }
                _currentProject.value = _currentProject.value.copy(audioClips = list)
            }
            TrackType.IMAGE -> {
                val list = _currentProject.value.imageClips.map { clip ->
                    if (clip.id == clipId) {
                        val newStart = (clip.startTimeMs + deltaMs).coerceAtLeast(0L)
                        clip.copy(startTimeMs = newStart)
                    } else clip
                }
                _currentProject.value = _currentProject.value.copy(imageClips = list)
            }
            else -> {}
        }
    }

    fun splitSelectedClip() {
        pushSnapshot()
        val playhead = _currentPlayheadMs.value
        val clipId = _selectedClipId.value
        val track = _selectedTrack.value

        when (track) {
            TrackType.VIDEO -> {
                val clips = _currentProject.value.videoClips
                val idx = clips.indexOfFirst { it.id == clipId || (playhead in it.startTimeMs until (it.startTimeMs + it.durationMs)) }
                if (idx != -1) {
                    val clip = clips[idx]
                    val offset = playhead - clip.startTimeMs
                    if (offset >= 400L && (clip.durationMs - offset) >= 400L) {
                        val clip1 = clip.copy(durationMs = offset)
                        val clip2 = clip.copy(
                            id = UUID.randomUUID().toString(),
                            startTimeMs = playhead,
                            durationMs = clip.durationMs - offset
                        )
                        val newList = clips.toMutableList().apply {
                            removeAt(idx)
                            add(idx, clip2)
                            add(idx, clip1)
                        }
                        _currentProject.value = _currentProject.value.copy(videoClips = newList)
                        _selectedClipId.value = clip2.id
                        saveCurrentProject()
                    }
                }
            }
            TrackType.QURAN -> {
                val clips = _currentProject.value.quranClips
                val idx = clips.indexOfFirst { it.id == clipId || (playhead in it.startTimeMs until (it.startTimeMs + it.durationMs)) }
                if (idx != -1) {
                    val clip = clips[idx]
                    val offset = playhead - clip.startTimeMs
                    if (offset >= 400L && (clip.durationMs - offset) >= 400L) {
                        val clip1 = clip.copy(durationMs = offset)
                        val clip2 = clip.copy(
                            id = UUID.randomUUID().toString(),
                            startTimeMs = playhead,
                            durationMs = clip.durationMs - offset
                        )
                        val newList = clips.toMutableList().apply {
                            removeAt(idx)
                            add(idx, clip2)
                            add(idx, clip1)
                        }
                        _currentProject.value = _currentProject.value.copy(quranClips = newList)
                        _selectedClipId.value = clip2.id
                        saveCurrentProject()
                    }
                }
            }
            TrackType.TEXT -> {
                val clips = _currentProject.value.textClips
                val idx = clips.indexOfFirst { it.id == clipId || (playhead in it.startTimeMs until (it.startTimeMs + it.durationMs)) }
                if (idx != -1) {
                    val clip = clips[idx]
                    val offset = playhead - clip.startTimeMs
                    if (offset >= 400L && (clip.durationMs - offset) >= 400L) {
                        val clip1 = clip.copy(durationMs = offset)
                        val clip2 = clip.copy(
                            id = UUID.randomUUID().toString(),
                            startTimeMs = playhead,
                            durationMs = clip.durationMs - offset
                        )
                        val newList = clips.toMutableList().apply {
                            removeAt(idx)
                            add(idx, clip2)
                            add(idx, clip1)
                        }
                        _currentProject.value = _currentProject.value.copy(textClips = newList)
                        _selectedClipId.value = clip2.id
                        saveCurrentProject()
                    }
                }
            }
            TrackType.AUDIO -> {
                val clips = _currentProject.value.audioClips
                val idx = clips.indexOfFirst { it.id == clipId || (playhead in it.startTimeMs until (it.startTimeMs + it.durationMs)) }
                if (idx != -1) {
                    val clip = clips[idx]
                    val offset = playhead - clip.startTimeMs
                    if (offset >= 400L && (clip.durationMs - offset) >= 400L) {
                        val clip1 = clip.copy(durationMs = offset)
                        val clip2 = clip.copy(
                            id = UUID.randomUUID().toString(),
                            startTimeMs = playhead,
                            durationMs = clip.durationMs - offset
                        )
                        val newList = clips.toMutableList().apply {
                            removeAt(idx)
                            add(idx, clip2)
                            add(idx, clip1)
                        }
                        _currentProject.value = _currentProject.value.copy(audioClips = newList)
                        _selectedClipId.value = clip2.id
                        saveCurrentProject()
                    }
                }
            }
            else -> {}
        }
    }

    fun toggleSelectedClipMute() {
        pushSnapshot()
        val clipId = _selectedClipId.value
        when (_selectedTrack.value) {
            TrackType.VIDEO -> {
                val list = _currentProject.value.videoClips.map {
                    if (it.id == clipId || clipId == null) it.copy(isMuted = !it.isMuted) else it
                }
                _currentProject.value = _currentProject.value.copy(videoClips = list)
                saveCurrentProject()
            }
            TrackType.AUDIO -> {
                val list = _currentProject.value.audioClips.map {
                    if (it.id == clipId || clipId == null) it.copy(volume = if (it.volume > 0f) 0f else 1f) else it
                }
                _currentProject.value = _currentProject.value.copy(audioClips = list)
                saveCurrentProject()
            }
            else -> {}
        }
    }

    fun addBlankClip(durationMs: Long = 5000L) {
        pushSnapshot()
        val playhead = _currentPlayheadMs.value
        val newClip = com.example.data.model.VideoTimelineClip(
            id = UUID.randomUUID().toString(),
            uri = null,
            name = "Blank Canvas",
            isBlank = true,
            startTimeMs = playhead,
            durationMs = durationMs
        )
        val list = _currentProject.value.videoClips + newClip
        val newTotalDuration = maxOf(_currentProject.value.durationMs, playhead + durationMs + 5000L)
        _currentProject.value = _currentProject.value.copy(
            videoClips = list,
            durationMs = newTotalDuration
        )
        _selectedTrack.value = TrackType.VIDEO
        _selectedClipId.value = newClip.id
        saveCurrentProject()
    }

    fun addMediaFromUri(uri: Uri) {
        pushSnapshot()
        val context = getApplication<Application>()
        var isVideo = true
        var mediaDurationMs = 10000L

        val mime = context.contentResolver.getType(uri)
        if (mime != null && mime.startsWith("image/")) {
            isVideo = false
            mediaDurationMs = 8000L
        }

        if (isVideo) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, uri)
                val dur = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
                if (dur != null && dur > 1000L) {
                    mediaDurationMs = dur
                }
            } catch (_: Exception) {
            } finally {
                try { retriever.release() } catch (_: Exception) {}
            }

            val playhead = _currentPlayheadMs.value
            val newClip = com.example.data.model.VideoTimelineClip(
                id = UUID.randomUUID().toString(),
                uri = uri.toString(),
                name = "Video Clip",
                isBlank = false,
                startTimeMs = playhead,
                durationMs = mediaDurationMs
            )
            val list = _currentProject.value.videoClips + newClip
            val newTotalDuration = maxOf(_currentProject.value.durationMs, playhead + mediaDurationMs + 5000L)
            _currentProject.value = _currentProject.value.copy(
                videoUri = _currentProject.value.videoUri ?: uri.toString(),
                videoClips = list,
                durationMs = newTotalDuration
            )
            _selectedTrack.value = TrackType.VIDEO
            _selectedClipId.value = newClip.id
        } else {
            val playhead = _currentPlayheadMs.value
            val newClip = com.example.data.model.ImageClip(
                id = UUID.randomUUID().toString(),
                uri = uri.toString(),
                startTimeMs = playhead,
                durationMs = mediaDurationMs
            )
            val list = _currentProject.value.imageClips + newClip
            val newTotalDuration = maxOf(_currentProject.value.durationMs, playhead + mediaDurationMs + 5000L)
            _currentProject.value = _currentProject.value.copy(
                imageClips = list,
                durationMs = newTotalDuration
            )
            _selectedTrack.value = TrackType.IMAGE
            _selectedClipId.value = newClip.id
        }
        saveCurrentProject()
    }

    fun updateClipTiming(track: TrackType, clipId: String, newStartMs: Long, newDurationMs: Long) {
        val totalDuration = _currentProject.value.durationMs
        val safeStart = newStartMs.coerceIn(0L, totalDuration - 500L)
        val safeDuration = newDurationMs.coerceIn(1000L, totalDuration - safeStart)

        when (track) {
            TrackType.VIDEO -> {
                val list = _currentProject.value.videoClips.map {
                    if (it.id == clipId) it.copy(startTimeMs = safeStart, durationMs = safeDuration) else it
                }
                _currentProject.value = _currentProject.value.copy(videoClips = list)
            }
            TrackType.QURAN -> {
                val list = _currentProject.value.quranClips.map {
                    if (it.id == clipId) it.copy(startTimeMs = safeStart, durationMs = safeDuration) else it
                }
                _currentProject.value = _currentProject.value.copy(quranClips = list)
            }
            TrackType.TEXT -> {
                val list = _currentProject.value.textClips.map {
                    if (it.id == clipId) it.copy(startTimeMs = safeStart, durationMs = safeDuration) else it
                }
                _currentProject.value = _currentProject.value.copy(textClips = list)
            }
            TrackType.AUDIO -> {
                val list = _currentProject.value.audioClips.map {
                    if (it.id == clipId) it.copy(startTimeMs = safeStart, durationMs = safeDuration) else it
                }
                _currentProject.value = _currentProject.value.copy(audioClips = list)
            }
            TrackType.IMAGE -> {
                val list = _currentProject.value.imageClips.map {
                    if (it.id == clipId) it.copy(startTimeMs = safeStart, durationMs = safeDuration) else it
                }
                _currentProject.value = _currentProject.value.copy(imageClips = list)
            }
            else -> {}
        }
        saveCurrentProject()
    }

    fun deleteSelectedClip() {
        pushSnapshot()
        val clipId = _selectedClipId.value ?: return
        when (_selectedTrack.value) {
            TrackType.VIDEO -> {
                val updated = _currentProject.value.videoClips.filterNot { it.id == clipId }
                _currentProject.value = _currentProject.value.copy(videoClips = updated)
                if (updated.isEmpty()) {
                    _selectedTrack.value = TrackType.NONE
                }
            }
            TrackType.QURAN -> {
                val updated = _currentProject.value.quranClips.filterNot { it.id == clipId }
                _currentProject.value = _currentProject.value.copy(quranClips = updated)
                if (updated.isEmpty()) {
                    _selectedTrack.value = TrackType.VIDEO
                }
            }
            TrackType.TEXT -> {
                val updated = _currentProject.value.textClips.filterNot { it.id == clipId }
                _currentProject.value = _currentProject.value.copy(textClips = updated)
                if (updated.isEmpty()) {
                    _selectedTrack.value = TrackType.VIDEO
                }
            }
            TrackType.AUDIO -> {
                val updated = _currentProject.value.audioClips.filterNot { it.id == clipId }
                _currentProject.value = _currentProject.value.copy(audioClips = updated)
                stopAudioPlayer()
                if (updated.isEmpty()) {
                    _selectedTrack.value = TrackType.VIDEO
                }
            }
            TrackType.IMAGE -> {
                val updated = _currentProject.value.imageClips.filterNot { it.id == clipId }
                _currentProject.value = _currentProject.value.copy(imageClips = updated)
                if (updated.isEmpty()) {
                    _selectedTrack.value = TrackType.VIDEO
                }
            }
            else -> {}
        }
        _selectedClipId.value = null
        saveCurrentProject()
    }

    fun duplicateSelectedClip() {
        pushSnapshot()
        val clipId = _selectedClipId.value ?: return
        when (_selectedTrack.value) {
            TrackType.VIDEO -> {
                val orig = _currentProject.value.videoClips.find { it.id == clipId } ?: return
                val copy = orig.copy(
                    id = UUID.randomUUID().toString(),
                    name = "${orig.name} (Copy)",
                    startTimeMs = (orig.startTimeMs + orig.durationMs).coerceAtMost(_currentProject.value.durationMs - 2000L)
                )
                _currentProject.value = _currentProject.value.copy(
                    videoClips = _currentProject.value.videoClips + copy
                )
                _selectedClipId.value = copy.id
            }
            TrackType.QURAN -> {
                val orig = _currentProject.value.quranClips.find { it.id == clipId } ?: return
                val copy = orig.copy(
                    id = UUID.randomUUID().toString(),
                    startTimeMs = (orig.startTimeMs + orig.durationMs).coerceAtMost(_currentProject.value.durationMs - 2000L)
                )
                _currentProject.value = _currentProject.value.copy(
                    quranClips = _currentProject.value.quranClips + copy
                )
                _selectedClipId.value = copy.id
            }
            TrackType.TEXT -> {
                val orig = _currentProject.value.textClips.find { it.id == clipId } ?: return
                val copy = orig.copy(
                    id = UUID.randomUUID().toString(),
                    startTimeMs = (orig.startTimeMs + orig.durationMs).coerceAtMost(_currentProject.value.durationMs - 2000L)
                )
                _currentProject.value = _currentProject.value.copy(
                    textClips = _currentProject.value.textClips + copy
                )
                _selectedClipId.value = copy.id
            }
            TrackType.AUDIO -> {
                val orig = _currentProject.value.audioClips.find { it.id == clipId } ?: return
                val copy = orig.copy(
                    id = UUID.randomUUID().toString(),
                    startTimeMs = (orig.startTimeMs + orig.durationMs).coerceAtMost(_currentProject.value.durationMs - 2000L)
                )
                _currentProject.value = _currentProject.value.copy(
                    audioClips = _currentProject.value.audioClips + copy
                )
                _selectedClipId.value = copy.id
            }
            TrackType.IMAGE -> {
                val orig = _currentProject.value.imageClips.find { it.id == clipId } ?: return
                val copy = orig.copy(
                    id = UUID.randomUUID().toString(),
                    startTimeMs = (orig.startTimeMs + orig.durationMs).coerceAtMost(_currentProject.value.durationMs - 2000L)
                )
                _currentProject.value = _currentProject.value.copy(
                    imageClips = _currentProject.value.imageClips + copy
                )
                _selectedClipId.value = copy.id
            }
            else -> {}
        }
        saveCurrentProject()
    }

    // Quran Editor Actions
    fun setQuranSearch(query: String) {
        _quranSearchQuery.value = query
    }

    fun selectSurah(surah: SurahMeta) {
        _selectedSurah.value = surah
        val ayahs = QuranData.getAyahsForSurah(surah.number)
        if (ayahs.isNotEmpty()) {
            _selectedAyah.value = ayahs[0]
        }
    }

    fun selectAyah(ayah: AyahItem) {
        _selectedAyah.value = ayah
    }

    fun setQuranFont(font: String) {
        _quranFont.value = font
    }

    fun setQuranFontSize(size: Float) {
        _quranFontSize.value = size.coerceIn(16f, 48f)
    }

    fun setQuranArabicColor(color: Long) {
        _quranArabicColor.value = color
    }

    fun setQuranTranslationColor(color: Long) {
        _quranTranslationColor.value = color
    }

    fun toggleQuranOutline() {
        _quranHasOutline.value = !_quranHasOutline.value
    }

    fun toggleQuranShadow() {
        _quranHasShadow.value = !_quranHasShadow.value
    }

    fun setQuranBackground(bg: QuranBackgroundType) {
        _quranBackground.value = bg
    }

    fun setQuranAnimation(anim: AnimationType) {
        _quranAnimation.value = anim
    }

    fun addQuranToVideo() {
        pushSnapshot()
        val currentAyah = _selectedAyah.value
        val surah = _selectedSurah.value
        val newClip = QuranClip(
            id = UUID.randomUUID().toString(),
            surahNumber = surah.number,
            surahName = surah.nameEnglish,
            ayahStart = currentAyah.ayahNumber,
            ayahEnd = currentAyah.ayahNumber,
            arabicText = currentAyah.arabicText,
            urduTranslation = currentAyah.urduTranslation,
            startTimeMs = _currentPlayheadMs.value,
            durationMs = 8000L.coerceAtMost((_currentProject.value.durationMs - _currentPlayheadMs.value).coerceAtLeast(3000L)),
            fontStyle = _quranFont.value,
            fontSize = _quranFontSize.value,
            arabicColor = _quranArabicColor.value,
            translationColor = _quranTranslationColor.value,
            hasOutline = _quranHasOutline.value,
            hasShadow = _quranHasShadow.value,
            backgroundType = _quranBackground.value,
            animation = _quranAnimation.value
        )
        val updatedClips = _currentProject.value.quranClips.toMutableList().apply { add(newClip) }
        _currentProject.value = _currentProject.value.copy(quranClips = updatedClips)
        _selectedTrack.value = TrackType.QURAN
        _selectedClipId.value = newClip.id
        _currentScreen.value = AppScreen.MAIN_EDITOR
        _activeBottomTool.value = BottomToolType.TIMELINE
        saveCurrentProject()
    }

    fun addBasmalaToVideo() {
        pushSnapshot()
        val basmala = QuranData.BISMILLAH
        val newClip = QuranClip(
            id = UUID.randomUUID().toString(),
            surahNumber = 1,
            surahName = "Al-Fatihah",
            ayahStart = 0,
            ayahEnd = 0,
            arabicText = basmala.arabicText,
            urduTranslation = basmala.urduTranslation,
            startTimeMs = _currentPlayheadMs.value,
            durationMs = 6000L,
            fontStyle = "Al-Fatihah",
            fontSize = 28f,
            arabicColor = 0xFFFFFFFF,
            translationColor = 0xFFE6B74A,
            hasOutline = true,
            hasShadow = true,
            animation = AnimationType.SLIDE_UP
        )
        val updated = _currentProject.value.quranClips.toMutableList().apply { add(newClip) }
        _currentProject.value = _currentProject.value.copy(quranClips = updated)
        _selectedTrack.value = TrackType.QURAN
        _selectedClipId.value = newClip.id
        saveCurrentProject()
    }

    // Text Editor Actions
    fun setTextInput(text: String) {
        _textInput.value = text
    }

    fun setTextSubTab(tab: Int) {
        _textSubTab.value = tab
    }

    fun setTextAnimation(anim: AnimationType) {
        _textAnimation.value = anim
    }

    fun applyEditedText() {
        pushSnapshot()
        val text = _textInput.value
        val existingClips = _currentProject.value.textClips
        val updated = if (existingClips.isNotEmpty()) {
            val first = existingClips[0].copy(
                text = text,
                animation = _textAnimation.value
            )
            listOf(first) + existingClips.drop(1)
        } else {
            listOf(
                TextClip(
                    id = UUID.randomUUID().toString(),
                    text = text,
                    startTimeMs = _currentPlayheadMs.value,
                    durationMs = 8000L,
                    animation = _textAnimation.value
                )
            )
        }
        _currentProject.value = _currentProject.value.copy(textClips = updated)
        _selectedTrack.value = TrackType.TEXT
        _selectedClipId.value = updated.firstOrNull()?.id
        _currentScreen.value = AppScreen.MAIN_EDITOR
        _activeBottomTool.value = BottomToolType.TIMELINE
        saveCurrentProject()
    }

    // Media & Effects Actions
    fun setMediaEffectsTab(tab: Int) {
        _mediaEffectsTab.value = tab
    }

    fun applyVideoFilter(filter: FilterType) {
        pushSnapshot()
        val currentCfg = _currentProject.value.videoConfig
        _currentProject.value = _currentProject.value.copy(videoConfig = currentCfg.copy(activeFilter = filter))
        saveCurrentProject()
    }

    fun applyVideoEffect(effect: EffectType) {
        pushSnapshot()
        val currentCfg = _currentProject.value.videoConfig
        _currentProject.value = _currentProject.value.copy(videoConfig = currentCfg.copy(activeEffect = effect))
        saveCurrentProject()
    }

    fun updateVideoAdjustment(
        brightness: Float? = null,
        contrast: Float? = null,
        saturation: Float? = null,
        exposure: Float? = null,
        temperature: Float? = null
    ) {
        pushSnapshot()
        val cfg = _currentProject.value.videoConfig
        _currentProject.value = _currentProject.value.copy(
            videoConfig = cfg.copy(
                brightness = brightness ?: cfg.brightness,
                contrast = contrast ?: cfg.contrast,
                saturation = saturation ?: cfg.saturation,
                exposure = exposure ?: cfg.exposure,
                temperature = temperature ?: cfg.temperature
            )
        )
        saveCurrentProject()
    }

    fun setVideoSpeed(speed: Float) {
        pushSnapshot()
        val cfg = _currentProject.value.videoConfig
        _currentProject.value = _currentProject.value.copy(videoConfig = cfg.copy(speed = speed))
        saveCurrentProject()
    }

    fun setCanvasRatio(ratio: AspectRatioOption) {
        pushSnapshot()
        val canvas = _currentProject.value.canvas
        _currentProject.value = _currentProject.value.copy(canvas = canvas.copy(aspectRatio = ratio))
        saveCurrentProject()
    }

    fun setCanvasBackground(bgType: CanvasBgType, color: Long = 0xFF0E0E12) {
        pushSnapshot()
        val canvas = _currentProject.value.canvas
        _currentProject.value = _currentProject.value.copy(canvas = canvas.copy(bgType = bgType, bgColor = color))
        saveCurrentProject()
    }

    fun addSticker(emojiOrIcon: String, category: String = "Islamic") {
        pushSnapshot()
        val newSticker = StickerClip(
            emojiOrIcon = emojiOrIcon,
            category = category,
            startTimeMs = _currentPlayheadMs.value,
            durationMs = 6000L
        )
        val updated = _currentProject.value.stickerClips.toMutableList().apply { add(newSticker) }
        _currentProject.value = _currentProject.value.copy(stickerClips = updated)
        saveCurrentProject()
    }

    fun addAudio(title: String) {
        pushSnapshot()
        val newAudio = AudioClip(
            title = title,
            startTimeMs = _currentPlayheadMs.value,
            durationMs = (_currentProject.value.durationMs - _currentPlayheadMs.value).coerceAtLeast(5000L)
        )
        val updated = _currentProject.value.audioClips.toMutableList().apply { add(newAudio) }
        _currentProject.value = _currentProject.value.copy(audioClips = updated)
        _selectedTrack.value = TrackType.AUDIO
        _selectedClipId.value = newAudio.id
        saveCurrentProject()
    }

    // Auto Caption Generator
    fun generateAutoCaptions(surahNumber: Int = 94) {
        _isAnalyzingCaption.value = true
        val surah = com.example.data.QuranData.SURAH_LIST.find { it.number == surahNumber }
            ?: com.example.data.QuranData.SURAH_LIST.first()
        _autoCaptionStatus.value = "Analyzing audio cadence & waveform..."
        viewModelScope.launch {
            delay(700)
            _autoCaptionStatus.value = "Matching ${surah.nameEnglish} (${surah.nameArabic})..."
            delay(700)

            pushSnapshot()
            val totalMs = _currentProject.value.durationMs.coerceAtLeast(10000L)
            val ayahs = surah.ayahs
            val clipCount = ayahs.size.coerceAtMost(8)
            val durationPerAyah = (totalMs / clipCount.coerceAtLeast(1)).coerceIn(3000L, 12000L)

            val autoGeneratedClips = ayahs.take(clipCount).mapIndexed { idx, ayah ->
                QuranClip(
                    id = UUID.randomUUID().toString(),
                    surahNumber = surah.number,
                    surahName = surah.nameEnglish,
                    ayahStart = ayah.ayahNumber,
                    ayahEnd = ayah.ayahNumber,
                    arabicText = ayah.arabicText,
                    urduTranslation = ayah.urduTranslation,
                    startTimeMs = (idx * durationPerAyah).coerceAtMost(totalMs - 2000L),
                    durationMs = durationPerAyah,
                    animation = AnimationType.SLIDE_UP
                )
            }
            _currentProject.value = _currentProject.value.copy(quranClips = autoGeneratedClips)
            _selectedTrack.value = TrackType.QURAN
            _selectedClipId.value = autoGeneratedClips.firstOrNull()?.id
            _isAnalyzingCaption.value = false
            _autoCaptionStatus.value = "${autoGeneratedClips.size} Ayahs of ${surah.nameEnglish} synchronized to timeline!"
            saveCurrentProject()
        }
    }

    // Settings actions
    fun updateSettings(
        iconSize: Float? = null,
        textSize: Float? = null,
        accentColorIndex: Int? = null,
        isDarkTheme: Boolean? = null,
        language: String? = null,
        featureQuran: String? = null,
        featureAutoCaption: String? = null,
        featureAudio: String? = null,
        featureEffects: String? = null,
        watermark: Boolean? = null
    ) {
        val curr = _settings.value
        _settings.value = curr.copy(
            iconSize = iconSize ?: curr.iconSize,
            textSize = textSize ?: curr.textSize,
            accentColorIndex = accentColorIndex ?: curr.accentColorIndex,
            isDarkTheme = isDarkTheme ?: curr.isDarkTheme,
            language = language ?: curr.language,
            featureQuran = featureQuran ?: curr.featureQuran,
            featureAutoCaption = featureAutoCaption ?: curr.featureAutoCaption,
            featureAudio = featureAudio ?: curr.featureAudio,
            featureEffects = featureEffects ?: curr.featureEffects,
            watermarkEnabled = watermark ?: curr.watermarkEnabled
        )
    }

    // Export flow
    fun startExport(resolution: String = "1080p", fps: Int = 30, quality: String = "High") {
        _isExporting.value = true
        _exportCompleted.value = false
        _exportProgress.value = 0f
        _exportStage.value = "Preparing media tracks..."

        viewModelScope.launch {
            val stages = listOf(
                "Decoding source video ($resolution)..." to 20f,
                "Rendering video filters & color adjustments..." to 40f,
                "Compositing Quran Ayah calligraphy & Urdu typography..." to 65f,
                "Synchronizing audio waveforms & reverb..." to 85f,
                "Muxing MP4 container with H.264 / AAC codecs..." to 100f
            )

            for ((stageText, targetProgress) in stages) {
                _exportStage.value = stageText
                while (_exportProgress.value < targetProgress) {
                    delay(120)
                    _exportProgress.value = (_exportProgress.value + 6f).coerceAtMost(targetProgress)
                }
            }

            _exportStage.value = "Export Complete!"
            _exportCompleted.value = true
            _isExporting.value = false
        }
    }

    fun dismissExport() {
        _isExporting.value = false
        _exportCompleted.value = false
        _exportProgress.value = 0f
        _activeModal.value = EditorModal.NONE
    }

    // Move and Scale Elements in Video Preview
    fun moveQuranClipPosition(clipId: String, deltaNormX: Float, deltaNormY: Float) {
        val list = _currentProject.value.quranClips.map { clip ->
            if (clip.id == clipId) {
                clip.copy(
                    positionX = (clip.positionX + deltaNormX).coerceIn(0.05f, 0.95f),
                    positionY = (clip.positionY + deltaNormY).coerceIn(0.05f, 0.95f)
                )
            } else clip
        }
        _currentProject.value = _currentProject.value.copy(quranClips = list)
        saveCurrentProject()
    }

    fun scaleQuranClip(clipId: String, scaleFactor: Float) {
        val list = _currentProject.value.quranClips.map { clip ->
            if (clip.id == clipId) {
                clip.copy(scale = (clip.scale * scaleFactor).coerceIn(0.5f, 3.0f))
            } else clip
        }
        _currentProject.value = _currentProject.value.copy(quranClips = list)
        saveCurrentProject()
    }

    fun moveTextClipPosition(clipId: String, deltaNormX: Float, deltaNormY: Float) {
        val list = _currentProject.value.textClips.map { clip ->
            if (clip.id == clipId) {
                clip.copy(
                    positionX = (clip.positionX + deltaNormX).coerceIn(0.05f, 0.95f),
                    positionY = (clip.positionY + deltaNormY).coerceIn(0.05f, 0.95f)
                )
            } else clip
        }
        _currentProject.value = _currentProject.value.copy(textClips = list)
        saveCurrentProject()
    }

    fun scaleTextClip(clipId: String, scaleFactor: Float) {
        val list = _currentProject.value.textClips.map { clip ->
            if (clip.id == clipId) {
                clip.copy(scale = (clip.scale * scaleFactor).coerceIn(0.5f, 3.0f))
            } else clip
        }
        _currentProject.value = _currentProject.value.copy(textClips = list)
        saveCurrentProject()
    }

    fun moveStickerPosition(clipId: String, deltaNormX: Float, deltaNormY: Float) {
        val list = _currentProject.value.stickerClips.map { clip ->
            if (clip.id == clipId) {
                clip.copy(
                    positionX = (clip.positionX + deltaNormX).coerceIn(0.05f, 0.95f),
                    positionY = (clip.positionY + deltaNormY).coerceIn(0.05f, 0.95f)
                )
            } else clip
        }
        _currentProject.value = _currentProject.value.copy(stickerClips = list)
        saveCurrentProject()
    }

    fun moveImagePosition(clipId: String, deltaNormX: Float, deltaNormY: Float) {
        val list = _currentProject.value.imageClips.map { clip ->
            if (clip.id == clipId) {
                clip.copy(
                    positionX = (clip.positionX + deltaNormX).coerceIn(0.05f, 0.95f),
                    positionY = (clip.positionY + deltaNormY).coerceIn(0.05f, 0.95f)
                )
            } else clip
        }
        _currentProject.value = _currentProject.value.copy(imageClips = list)
        saveCurrentProject()
    }

    fun setAudioClipVolume(clipId: String, volume: Float) {
        pushSnapshot()
        val list = _currentProject.value.audioClips.map { clip ->
            if (clip.id == clipId) clip.copy(volume = volume.coerceIn(0f, 1f)) else clip
        }
        _currentProject.value = _currentProject.value.copy(audioClips = list)
        if (currentlyPlayingAudioClipId == clipId) {
            val vol = if (_isMuted.value) 0f else volume.coerceIn(0f, 1f)
            try {
                audioPlayer?.setVolume(vol, vol)
            } catch (_: Exception) {}
        }
        saveCurrentProject()
    }

    fun setVideoClipVolume(clipId: String, volume: Float) {
        pushSnapshot()
        val list = _currentProject.value.videoClips.map { clip ->
            if (clip.id == clipId) clip.copy(volume = volume.coerceIn(0f, 1f)) else clip
        }
        _currentProject.value = _currentProject.value.copy(videoClips = list)
        saveCurrentProject()
    }

    fun addAudioFromUri(uri: Uri, title: String) {
        pushSnapshot()
        var durationMs = 30000L
        val context = getApplication<Application>()
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            val dur = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
            if (dur != null && dur > 1000L) {
                durationMs = dur
            }
        } catch (_: Exception) {
        } finally {
            try { retriever.release() } catch (_: Exception) {}
        }

        val newAudio = AudioClip(
            id = UUID.randomUUID().toString(),
            title = title,
            uri = uri.toString(),
            startTimeMs = _currentPlayheadMs.value,
            durationMs = durationMs
        )
        val list = _currentProject.value.audioClips + newAudio
        val newTotal = maxOf(_currentProject.value.durationMs, _currentPlayheadMs.value + durationMs)
        _currentProject.value = _currentProject.value.copy(audioClips = list, durationMs = newTotal)
        _selectedTrack.value = TrackType.AUDIO
        _selectedClipId.value = newAudio.id
        saveCurrentProject()
    }

    override fun onCleared() {
        super.onCleared()
        stopAudioPlayer()
    }

    private fun saveCurrentProject() {
        viewModelScope.launch {
            repository.saveProject(_currentProject.value)
        }
    }
}
