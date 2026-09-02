package com.eliranrp.sjoelenscorekaart.domain

import com.eliranrp.sjoelenscorekaart.scoring.Gate
import com.eliranrp.sjoelenscorekaart.scoring.SjoelenScoring
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScorekaartTest {

    @Test
    fun incrementAndUndo_restoresPreviousGateCount() {
        val card = Scorekaart(playerCount = 1)
        assertTrue(card.increment(Gate.TWO))
        assertEquals(1, card.state.selected.current.two)
        assertTrue(card.canUndo)
        assertTrue(card.undo())
        assertEquals(0, card.state.selected.current.two)
        assertFalse(card.canUndo)
    }

    @Test
    fun undoLastChange_onlyRevertsMostRecentIncrement() {
        val card = Scorekaart(playerCount = 1)
        card.increment(Gate.TWO)
        card.increment(Gate.THREE)
        card.increment(Gate.FOUR)
        card.undo()
        assertEquals(1, card.state.selected.current.two)
        assertEquals(1, card.state.selected.current.three)
        assertEquals(0, card.state.selected.current.four)
    }

    @Test
    fun cannotDecrementBelowZero() {
        val card = Scorekaart(playerCount = 1)
        assertFalse(card.decrement(Gate.ONE))
        assertEquals(0, card.state.selected.current.one)
        assertFalse(card.canUndo)
    }

    @Test
    fun cannotIncrementPastThirtyDiscs() {
        val card = Scorekaart(playerCount = 1)
        repeat(SjoelenScoring.DISCS_PER_ROUND) { card.increment(Gate.FOUR) }
        assertEquals(30, card.state.selected.current.discsInGates)
        assertFalse(card.increment(Gate.TWO))
        assertEquals(0, card.state.selected.current.two)
        assertEquals(120, card.state.selected.current.points)
    }

    @Test
    fun completeSet_addsTwentyToCurrentRound() {
        val card = Scorekaart(playerCount = 1)
        card.increment(Gate.TWO)
        card.increment(Gate.THREE)
        card.increment(Gate.FOUR)
        card.increment(Gate.ONE)
        assertEquals(20, card.state.selected.current.points)
        assertTrue(card.state.selected.current.hasVolleBak)
        assertEquals(20, card.state.selected.runningTotal)
    }

    @Test
    fun nextRound_banksScoreAndResetsGates() {
        val card = Scorekaart(playerCount = 2)
        fillVolleBak(card)
        card.nextRound()
        assertEquals(2, card.state.currentRound)
        val first = card.state.players.first()
        assertEquals(listOf(20), first.completedRounds)
        assertEquals(0, first.current.discsInGates)
        assertEquals(20, first.runningTotal)
        val second = card.state.players[1]
        assertEquals(listOf(0), second.completedRounds)
    }

    @Test
    fun undoNextRound_restoresCurrentGates() {
        val card = Scorekaart(playerCount = 1)
        fillVolleBak(card)
        card.nextRound()
        assertTrue(card.undo())
        assertEquals(1, card.state.currentRound)
        assertEquals(20, card.state.selected.current.points)
        assertTrue(card.state.selected.completedRounds.isEmpty())
    }

    @Test
    fun runningTotals_accumulateAcrossRounds() {
        val card = Scorekaart(playerCount = 1)
        fillVolleBak(card)
        card.nextRound()
        card.increment(Gate.FOUR)
        card.increment(Gate.FOUR)
        assertEquals(28, card.state.selected.runningTotal) // 20 + 8
        card.nextRound()
        assertEquals(listOf(20, 8), card.state.selected.completedRounds)
        assertEquals(28, card.state.selected.runningTotal)
    }

    @Test
    fun reset_clearsScoresKeepsPlayersAndNames() {
        val card = Scorekaart(playerCount = 2)
        card.renameSelected("Anna")
        fillVolleBak(card)
        card.nextRound()
        card.resetKeepPlayers()
        assertEquals(1, card.state.currentRound)
        assertEquals("Anna", card.state.players.first().customName)
        assertEquals(2, card.state.players.size)
        assertTrue(card.state.players.first().completedRounds.isEmpty())
        assertEquals(0, card.state.players.first().runningTotal)
    }

    @Test
    fun undoReset_restoresBankedRounds() {
        val card = Scorekaart(playerCount = 1)
        fillVolleBak(card)
        card.nextRound()
        card.resetKeepPlayers()
        card.undo()
        assertEquals(listOf(20), card.state.selected.completedRounds)
        assertEquals(2, card.state.currentRound)
    }

    @Test
    fun leftoverDiscs_doNotChangeScore() {
        val card = Scorekaart(playerCount = 1)
        fillVolleBak(card)
        assertEquals(26, card.state.selected.current.discsOutside)
        assertEquals(20, card.state.selected.current.points)
    }

    @Test
    fun addAndRemovePlayers() {
        val card = Scorekaart(playerCount = 2)
        card.addPlayer()
        assertEquals(3, card.state.players.size)
        card.removeSelectedPlayer()
        assertEquals(2, card.state.players.size)
        card.removeSelectedPlayer()
        assertEquals(1, card.state.players.size)
        assertFalse(card.removeSelectedPlayer())
    }

    @Test
    fun selectOtherPlayer_doesNotChangePreviousGates() {
        val card = Scorekaart(playerCount = 2)
        val firstId = card.state.selectedPlayerId
        card.increment(Gate.TWO)
        val secondId = card.state.players[1].id
        card.selectPlayer(secondId)
        card.increment(Gate.FOUR)
        assertEquals(1, card.state.players.first { it.id == firstId }.current.two)
        assertEquals(1, card.state.selected.current.four)
    }

    private fun fillVolleBak(card: Scorekaart) {
        card.increment(Gate.TWO)
        card.increment(Gate.THREE)
        card.increment(Gate.FOUR)
        card.increment(Gate.ONE)
    }
}
