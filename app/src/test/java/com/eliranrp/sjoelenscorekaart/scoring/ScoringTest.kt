package com.eliranrp.sjoelenscorekaart.scoring

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScoringTest {

    @Test
    fun legeRondeIsNul() {
        assertEquals(0, Scoring.rondePunten(PoortAantallen(), huisregelVolleBak = true))
    }

    @Test
    fun formuleZonderVolleBak() {
        val stand = PoortAantallen(n2 = 4, n3 = 3, n4 = 2, n1 = 5)
        // (4×2)+(3×3)+(2×4)+(5×1) = 8+9+8+5 = 30, geen volle bak-bonus nodig maar wel volle bak
        assertEquals(30 + 20, Scoring.rondePunten(stand, huisregelVolleBak = true))
        assertEquals(30, Scoring.rondePunten(stand, huisregelVolleBak = false))
    }

    @Test
    fun alleenTweePuntenPoortGeenBonus() {
        val stand = PoortAantallen(n2 = 10)
        assertFalse(stand.isVolleBak)
        assertEquals(20, Scoring.rondePunten(stand, huisregelVolleBak = true))
    }

    @Test
    fun driePoortenGeenBonus() {
        val stand = PoortAantallen(n2 = 1, n3 = 1, n4 = 1, n1 = 0)
        assertFalse(stand.isVolleBak)
        assertEquals(2 + 3 + 4, Scoring.rondePunten(stand, huisregelVolleBak = true))
    }

    @Test
    fun eenSchijfPerPoortMetHuisregelIsDertig() {
        val stand = PoortAantallen(n2 = 1, n3 = 1, n4 = 1, n1 = 1)
        assertTrue(stand.isVolleBak)
        assertEquals(10, stand.basisPunten())
        assertEquals(30, Scoring.rondePunten(stand, huisregelVolleBak = true))
        assertEquals(10, Scoring.rondePunten(stand, huisregelVolleBak = false))
    }

    @Test
    fun dertigSchijvenInVierIsHonderdtwintigZonderVolleBak() {
        val stand = PoortAantallen(n4 = 30)
        assertEquals(120, Scoring.rondePunten(stand, huisregelVolleBak = true))
        assertEquals(30, stand.totaalSchijven)
    }
}
