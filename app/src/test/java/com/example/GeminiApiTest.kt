package com.example

import com.example.data.api.GeminiContent
import com.example.data.api.GeminiContentPart
import com.example.data.api.GeminiGenerateRequest
import com.example.data.api.GeminiGenerateResponse
import com.example.data.api.GeminiCandidate
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GeminiApiTest {

    @Test
    fun testGeminiRequestSerialization() {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(GeminiGenerateRequest::class.java)

        val request = GeminiGenerateRequest(
            contents = listOf(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiContentPart(text = "ویڈیو کے نیچے اردو ترجمہ لگاؤ"))
                )
            ),
            systemInstruction = GeminiContent(
                role = "system",
                parts = listOf(GeminiContentPart(text = "You are the Quran Video AI assistant"))
            )
        )

        val json = adapter.toJson(request)
        assertNotNull(json)
        assertTrue("JSON should contain user text", json.contains("ویڈیو کے نیچے اردو ترجمہ لگاؤ"))
        assertTrue("JSON should contain system prompt", json.contains("Quran Video AI assistant"))
    }

    @Test
    fun testGeminiResponseDeserialization() {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(GeminiGenerateResponse::class.java)

        val rawJson = """
            {
                "candidates": [
                    {
                        "content": {
                            "role": "model",
                            "parts": [
                                {
                                    "text": "قرآنی آیت کے نیچے اردو ترجمہ کامیابی سے سیٹ کر دیا گیا ہے۔"
                                }
                            ]
                        },
                        "finishReason": "STOP"
                    }
                ]
            }
        """.trimIndent()

        val response = adapter.fromJson(rawJson)
        assertNotNull(response)
        val candidate = response?.candidates?.firstOrNull()
        assertNotNull(candidate)
        val text = candidate?.content?.parts?.firstOrNull()?.text
        assertEquals("قرآنی آیت کے نیچے اردو ترجمہ کامیابی سے سیٹ کر دیا گیا ہے۔", text)
    }
}
