package com.eliranrp.sjoelenscorekaart.scoring

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SjoelenScoringTest {

    @Test
    fun oneDiscInEveryGate_isTwenty_notTen() {
        // Common NSB / house rule: 2-3-4-1 complete set ("volle bak") scores 20, not 10.
        assertEquals(20, SjoelenScoring.roundScore(1, 1, 1, 1))
        assertTrue(SjoelenScoring.hasVolleBak(1, 1, 1, 1))
        assertEquals(1, SjoelenScoring.completeSets(1, 1, 1, 1))
        assertEquals(10, SjoelenScoring.setBonusPoints(1, 1, 1, 1))
    }

    @Test
    fun missingAnyGate_noSetBonus_faceValuesOnly() {
        assertEquals(9, SjoelenScoring.roundScore(1, 1, 1, 0)) // 2+3+4
        assertEquals(8, SjoelenScoring.roundScore(0, 1, 1, 1)) // 3+4+1
        assertEquals(7, SjoelenScoring.roundScore(1, 0, 1, 1)) // 2+4+1
        assertEquals(6, SjoelenScoring.roundScore(1, 1, 0, 1)) // 2+3+1
        assertFalse(SjoelenScoring.hasVolleBak(1, 1, 1, 0))
        assertEquals(0, SjoelenScoring.setBonusPoints(1, 1, 1, 0))
    }

    @Test
    fun twoCompleteSets_areForty() {
        assertEquals(40, SjoelenScoring.roundScore(2, 2, 2, 2))
        assertEquals(2, SjoelenScoring.completeSets(2, 2, 2, 2))
    }

    @Test
    fun setPlusRemainder_countsFaceValueOnExtras() {
        // One set (20) + extra disc in poort 1.
        assertEquals(21, SjoelenScoring.roundScore(1, 1, 1, 2))
        // One set (20) + extra disc in poort 4.
        assertEquals(24, SjoelenScoring.roundScore(1, 1, 2, 1))
        // Five in each plus extra 4: official example 100 + 4 = 104.
        assertEquals(104, SjoelenScoring.roundScore(5, 5, 6, 5))
    }

    @Test
    fun emptyRound_isZero() {
        assertEquals(0, SjoelenScoring.roundScore(0, 0, 0, 0))
        assertFalse(SjoelenScoring.hasVolleBak(0, 0, 0, 0))
    }

    @Test
    fun discsOutsideGates_scoreZero() {
        // 4 discs in a complete set; 26 remaining outside are not passed in → 0.
        assertEquals(20, SjoelenScoring.roundScore(1, 1, 1, 1))
        // All 30 in poort 4, none in the others: no set, 30×4 = 120.
        assertEquals(120, SjoelenScoring.roundScore(0, 0, 30, 0))
        // All 30 in poort 1: 30.
        assertEquals(30, SjoelenScoring.roundScore(0, 0, 0, 30))
    }

    @Test
    fun maximumWithThirtyDiscs_is148() {
        // 7 complete sets (28 discs = 140) + two extras in poort 4 (8) = 148.
        assertEquals(148, SjoelenScoring.roundScore(7, 7, 9, 7))
    }

    @Test(expected = IllegalArgumentException::class)
    fun moreThanThirtyDiscs_isRejected() {
        SjoelenScoring.roundScore(8, 8, 8, 8)
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeCount_isRejected() {
        SjoelenScoring.roundScore(-1, 0, 0, 0)
    }

    @Test
    fun gatesAreTwoThreeFourOneLeftToRight() {
        assertEquals(listOf(2, 3, 4, 1), Gate.leftToRight.map { it.points })
    }
}
