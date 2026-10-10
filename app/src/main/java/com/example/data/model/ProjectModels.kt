package com.example.data.model

import java.util.UUID

enum class AspectRatioOption(val label: String, val ratio: Float) {
    RATIO_16_9("16:9", 16f / 9f),
    RATIO_9_16("9:16", 9f / 16f),
    RATIO_1_1("1:1", 1f),
    RATIO_4_5("4:5", 4f / 5f),
    RATIO_ORIGINAL("Original", 16f / 9f)
}

enum class CanvasBgType {
    BLUR, COLOR, IMAGE
}

enum class FilterType(val displayName: String) {
    NONE("None"),
    NATURAL("Natural"),
    WARM("Warm"),
    VINTAGE("Vintage"),
    YELLOW("Yellow"),
    CINEMATIC("Cinematic"),
    BLACK_AND_WHITE("Black & White")
}

enum class EffectType(val displayName: String) {
    NONE("None"),
    BLUR("Blur"),
    GLITCH("Glitch"),
    CINEMATIC("Cinematic"),
    LIGHT("Light"),
    SHAKE("Shake"),
    ZOOM("Zoom"),
    FADE("Fade"),
    VIGNETTE("Vignette")
}

enum class AnimationType(val displayName: String) {
    NONE("None"),
    FADE_IN("Fade In"),
    SLIDE_UP("Slide Up"),
    SLIDE_DOWN("Slide Down"),
    SLIDE_LEFT("Slide Left"),
    SLIDE_RIGHT("Slide Right"),
    ZOOM_IN("Zoom In"),
    FADE_OUT("Fade Out"),
    ZOOM_OUT("Zoom Out")
}

enum class QuranBackgroundType {
    TRANSPARENT,
    SOLID,
    ROUNDED_BOX,
    GRADIENT
}

data class CanvasConfig(
    val aspectRatio: AspectRatioOption = AspectRatioOption.RATIO_16_9,
    val bgType: CanvasBgType = CanvasBgType.BLUR,
    val bgColor: Long = 0xFF0E0E12,
    val zoom: Float = 1.0f,
    val crop: Float = 0f
)

data class VideoClipConfig(
    val startTimeMs: Long = 0L,
    val endTimeMs: Long = 84000L, // 01:24
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val isMuted: Boolean = false,
    val isFlipped: Boolean = false,
    val rotation: Float = 0f,
    val scale: Float = 1.0f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val brightness: Float = 15f,
    val contrast: Float = 0f,
    val saturation: Float = 0f,
    val exposure: Float = 0f,
    val sharpness: Float = 0f,
    val temperature: Float = 0f,
    val activeFilter: FilterType = FilterType.NATURAL,
    val activeEffect: EffectType = EffectType.NONE
)

data class QuranClip(
    val id: String = UUID.randomUUID().toString(),
    val surahNumber: Int = 94,
    val surahName: String = "Al-Inshirah",
    val ayahStart: Int = 6,
    val ayahEnd: Int = 6,
    val arabicText: String = "إِنَّ مَعَ الْعُسْرِ يُسْرًا",
    val urduTranslation: String = "بے شک مشکل کے ساتھ آسانی ہے",
    val startTimeMs: Long = 18000L,
    val durationMs: Long = 40000L,
    val fontStyle: String = "Naskh",
    val fontSize: Float = 26f,
    val arabicColor: Long = 0xFFFFFFFF,
    val translationColor: Long = 0xFFE6B74A,
    val hasOutline: Boolean = true,
    val outlineColor: Long = 0xFF000000,
    val outlineThickness: Float = 2f,
    val hasShadow: Boolean = true,
    val shadowColor: Long = 0x99000000,
    val shadowBlur: Float = 4f,
    val backgroundType: QuranBackgroundType = QuranBackgroundType.ROUNDED_BOX,
    val backgroundColor: Long = 0xAA14141A,
    val alignment: String = "Center",
    val positionX: Float = 0.5f,
    val positionY: Float = 0.5f,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val opacity: Float = 1.0f,
    val animation: AnimationType = AnimationType.SLIDE_UP
)

data class TextClip(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ\nشروع اللہ کے نام سے جو نہایت مہربان، رحم کرنے والا ہے",
    val startTimeMs: Long = 0L,
    val durationMs: Long = 10000L,
    val font: String = "Modern",
    val size: Float = 22f,
    val color: Long = 0xFFFFFFFF,
    val isBold: Boolean = true,
    val isItalic: Boolean = false,
    val hasOutline: Boolean = true,
    val outlineColor: Long = 0xFF000000,
    val hasShadow: Boolean = true,
    val shadowColor: Long = 0x99000000,
    val backgroundColor: Long? = null,
    val opacity: Float = 1.0f,
    val alignment: String = "Center",
    val positionX: Float = 0.5f,
    val positionY: Float = 0.45f,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val animation: AnimationType = AnimationType.SLIDE_UP,
    val templateId: String? = null
)

data class AudioClip(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Mishary Rashid - Surah Rahman Recitation",
    val uri: String? = null,
    val startTimeMs: Long = 0L,
    val durationMs: Long = 84000L,
    val volume: Float = 1.0f,
    val fadeInMs: Long = 1000L,
    val fadeOutMs: Long = 1000L,
    val speed: Float = 1.0f
)

data class VideoTimelineClip(
    val id: String = UUID.randomUUID().toString(),
    val uri: String? = null,
    val name: String = "Video Clip",
    val isBlank: Boolean = false,
    val startTimeMs: Long = 0L,
    val durationMs: Long = 10000L,
    val trimStartMs: Long = 0L,
    val sourceDurationMs: Long = 60000L,
    val volume: Float = 1.0f,
    val isMuted: Boolean = false,
    val speed: Float = 1.0f,
    val scale: Float = 1.0f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f
)

data class ImageClip(
    val id: String = UUID.randomUUID().toString(),
    val uri: String,
    val startTimeMs: Long = 0L,
    val durationMs: Long = 10000L,
    val positionX: Float = 0.5f,
    val positionY: Float = 0.5f,
    val scale: Float = 1.0f,
    val opacity: Float = 1.0f
)

data class StickerClip(
    val id: String = UUID.randomUUID().toString(),
    val emojiOrIcon: String = "﷽",
    val category: String = "Islamic",
    val startTimeMs: Long = 0L,
    val durationMs: Long = 15000L,
    val positionX: Float = 0.5f,
    val positionY: Float = 0.2f,
    val scale: Float = 1.0f
)

data class ExportSettings(
    val resolution: String = "1080p", // 480p, 720p, 1080p, Original
    val fps: Int = 30, // 24, 30, 60
    val quality: String = "High", // Low, Medium, High, Maximum
    val format: String = "MP4",
    val watermarkEnabled: Boolean = false
)

data class EditingProject(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Quran Video",
    val videoUri: String? = null,
    val thumbnailUri: String? = null,
    val durationMs: Long = 60000L,
    val createdAt: String = "",
    val updatedAt: Long = System.currentTimeMillis(),
    val canvas: CanvasConfig = CanvasConfig(),
    val videoConfig: VideoClipConfig = VideoClipConfig(),
    val videoClips: List<VideoTimelineClip> = emptyList(),
    val quranClips: List<QuranClip> = emptyList(),
    val textClips: List<TextClip> = emptyList(),
    val audioClips: List<AudioClip> = emptyList(),
    val imageClips: List<ImageClip> = emptyList(),
    val stickerClips: List<StickerClip> = emptyList(),
    val exportSettings: ExportSettings = ExportSettings()
)

data class AppSettings(
    val iconSize: Float = 10f,
    val textSize: Float = 6f,
    val accentColorIndex: Int = 0, // 0=Gold, 1=Gray, 2=Teal, 3=Blue, 4=Purple
    val isDarkTheme: Boolean = true,
    val language: String = "English",
    val defaultResolution: String = "1080p",
    val defaultFps: Int = 30,
    val defaultQuality: String = "High",
    val watermarkEnabled: Boolean = false
)
