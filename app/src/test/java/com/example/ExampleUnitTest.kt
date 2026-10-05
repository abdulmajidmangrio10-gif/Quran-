package com.example

import com.example.data.quran.QuranData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testQuranHasAll114Surahs() {
        assertEquals("Holy Quran must contain exactly 114 Surahs", 114, QuranData.ALL_SURAHS.size)
        assertEquals("First Surah is Al-Fatihah", "Al-Fatihah", QuranData.ALL_SURAHS[0].nameEnglish)
        assertEquals("114th Surah is An-Nas", "An-Nas", QuranData.ALL_SURAHS[113].nameEnglish)
    }

    @Test
    fun testReferenceAyahAlInshirah() {
        // As highlighted in the prompt and reference image #3:
        val ayah = QuranData.getAyah(94, 6)
        assertEquals("إِنَّ مَعَ الْعُسْرِ يُسْرًا", ayah.arabicText)
        assertEquals("بے شک مشکل کے ساتھ آسانی ہے", ayah.urduTranslation)
    }

    @Test
    fun testQuranSearch() {
        val fatihah = QuranData.searchSurahs("Fatihah")
        assertTrue(fatihah.isNotEmpty())
        assertEquals(1, fatihah[0].number)

        val byNumber = QuranData.searchSurahs("55") // Ar-Rahman
        assertTrue(byNumber.isNotEmpty())
        assertEquals("Ar-Rahman", byNumber[0].nameEnglish)

        val byArabic = QuranData.searchSurahs("الملك")
        assertTrue(byArabic.isNotEmpty())
        assertEquals(67, byArabic[0].number)
    }

    @Test
    fun testQuranAyahVerseGeneration() {
        val ayah = QuranData.getAyah(112, 1)
        assertEquals("قُلْ هُوَ ٱللَّهُ أَحَدٌ", ayah.arabicText)
    }
}
