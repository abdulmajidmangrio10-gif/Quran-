package com.example

import com.example.data.model.EditingProject
import com.example.data.model.VideoClipConfig
import com.example.data.model.VideoTimelineClip
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class EditorTimelineTest {

    @Test
    fun testClipMovingPreservesDuration() {
        val originalDuration = 15000L
        val originalStart = 0L
        val clip = VideoTimelineClip(
            id = UUID.randomUUID().toString(),
            name = "Project 1",
            startTimeMs = originalStart,
            durationMs = originalDuration,
            sourceDurationMs = originalDuration
        )

        val deltaMs = 3500L
        val movedClip = clip.copy(startTimeMs = (clip.startTimeMs + deltaMs).coerceAtLeast(0L))

        assertEquals("Moved clip start time should increase by delta", 3500L, movedClip.startTimeMs)
        assertEquals("Moving clip must preserve duration", originalDuration, movedClip.durationMs)
        assertEquals("Moving clip must preserve source duration", originalDuration, movedClip.sourceDurationMs)
    }

    @Test
    fun testClipMovingCannotBeNegative() {
        val clip = VideoTimelineClip(
            id = UUID.randomUUID().toString(),
            name = "Project 1",
            startTimeMs = 2000L,
            durationMs = 10000L
        )

        val deltaMs = -5000L
        val movedClip = clip.copy(startTimeMs = (clip.startTimeMs + deltaMs).coerceAtLeast(0L))

        assertEquals("Clip start time cannot be negative", 0L, movedClip.startTimeMs)
        assertEquals("Clip duration remains intact", 10000L, movedClip.durationMs)
    }

    @Test
    fun testClipTrimmingStartKeepsStartTimeAndAdjustsVisibleDuration() {
        val clip = VideoTimelineClip(
            id = UUID.randomUUID().toString(),
            name = "Project 1",
            startTimeMs = 1000L,
            durationMs = 20000L,
            sourceDurationMs = 20000L,
            trimStartMs = 0L
        )

        // Trim start by +2000ms
        val deltaMs = 2000L
        val maxTrim = clip.sourceDurationMs - 500L
        val newTrimStart = (clip.trimStartMs + deltaMs).coerceIn(0L, maxTrim)
        val trimDiff = newTrimStart - clip.trimStartMs
        val newDur = (clip.durationMs - trimDiff).coerceIn(500L, clip.sourceDurationMs - newTrimStart)
        val trimmedClip = clip.copy(trimStartMs = newTrimStart, durationMs = newDur)

        assertEquals("Timeline start position must NOT change during trim start", 1000L, trimmedClip.startTimeMs)
        assertEquals("Trim start point must advance", 2000L, trimmedClip.trimStartMs)
        assertEquals("Visible clip duration decreases by trimmed portion", 18000L, trimmedClip.durationMs)
    }

    @Test
    fun testVideoTransformScalingBounds() {
        val config = VideoClipConfig(scale = 1.0f, offsetX = 0f, offsetY = 0f)

        val zoomInScale = (config.scale * 2.5f).coerceIn(1.0f, 5.0f)
        assertEquals(2.5f, zoomInScale, 0.001f)

        val overZoomScale = (config.scale * 10.0f).coerceIn(1.0f, 5.0f)
        assertEquals(5.0f, overZoomScale, 0.001f)

        val underZoomScale = (config.scale * 0.2f).coerceIn(1.0f, 5.0f)
        assertEquals(1.0f, underZoomScale, 0.001f)
    }
}
