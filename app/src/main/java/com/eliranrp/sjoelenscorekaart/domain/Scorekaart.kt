package com.eliranrp.sjoelenscorekaart.domain

import com.eliranrp.sjoelenscorekaart.scoring.Gate
import com.eliranrp.sjoelenscorekaart.scoring.SjoelenScoring

data class GateCounts(
    val two: Int = 0,
    val three: Int = 0,
    val four: Int = 0,
    val one: Int = 0,
) {
    operator fun get(gate: Gate): Int = when (gate) {
        Gate.TWO -> two
        Gate.THREE -> three
        Gate.FOUR -> four
        Gate.ONE -> one
    }

    fun with(gate: Gate, value: Int): GateCounts = when (gate) {
        Gate.TWO -> copy(two = value)
        Gate.THREE -> copy(three = value)
        Gate.FOUR -> copy(four = value)
        Gate.ONE -> copy(one = value)
    }

    val discsInGates: Int get() = two + three + four + one

    val discsOutside: Int get() = (SjoelenScoring.DISCS_PER_ROUND - discsInGates).coerceAtLeast(0)

    val completeSets: Int get() = SjoelenScoring.completeSets(two, three, four, one)

    val hasVolleBak: Boolean get() = completeSets > 0

    val points: Int get() = SjoelenScoring.roundScore(two, three, four, one)

    val setBonusPoints: Int get() = SjoelenScoring.setBonusPoints(two, three, four, one)

    fun canIncrement(): Boolean = discsInGates < SjoelenScoring.DISCS_PER_ROUND

    fun canDecrement(gate: Gate): Boolean = get(gate) > 0

    fun increment(gate: Gate): GateCounts {
        if (!canIncrement()) return this
        return with(gate, get(gate) + 1)
    }

    fun decrement(gate: Gate): GateCounts {
        if (!canDecrement(gate)) return this
        return with(gate, get(gate) - 1)
    }
}

data class PlayerState(
    val id: Long,
    val customName: String,
    val number: Int,
    val completedRounds: List<Int>,
    val current: GateCounts,
) {
    val runningTotal: Int get() = completedRounds.sum() + current.points

    val completedTotal: Int get() = completedRounds.sum()
}

data class ScorekaartState(
    val players: List<PlayerState>,
    val selectedPlayerId: Long,
    val currentRound: Int,
) {
    val selected: PlayerState
        get() = players.first { it.id == selectedPlayerId }

    fun displayName(player: PlayerState, defaultLabel: (Int) -> String): String {
        val trimmed = player.customName.trim()
        return trimmed.ifEmpty { defaultLabel(player.number) }
    }
}

/**
 * In-memory sjoelen scorekaart: players, running totals, next round, undo, reset.
 * Pure Kotlin so scoring and undo can be unit-tested without Android.
 */
class Scorekaart(
    playerCount: Int = DEFAULT_PLAYER_COUNT,
) {
    private val undoStack = ArrayDeque<ScorekaartState>()
    private var nextPlayerId: Long = 1L
    private var nextPlayerNumber: Int = 1

    var state: ScorekaartState = newGame(playerCount)
        private set

    val canUndo: Boolean get() = undoStack.isNotEmpty()

    fun selectPlayer(id: Long) {
        if (state.players.none { it.id == id }) return
        if (id == state.selectedPlayerId) return
        pushUndo()
        state = state.copy(selectedPlayerId = id)
    }

    fun increment(gate: Gate): Boolean {
        val player = state.selected
        if (!player.current.canIncrement()) return false
        pushUndo()
        updateSelected { it.copy(current = it.current.increment(gate)) }
        return true
    }

    fun decrement(gate: Gate): Boolean {
        val player = state.selected
        if (!player.current.canDecrement(gate)) return false
        pushUndo()
        updateSelected { it.copy(current = it.current.decrement(gate)) }
        return true
    }

    fun nextRound() {
        pushUndo()
        state = state.copy(
            currentRound = state.currentRound + 1,
            players = state.players.map { player ->
                player.copy(
                    completedRounds = player.completedRounds + player.current.points,
                    current = GateCounts(),
                )
            },
        )
    }

    fun undo(): Boolean {
        val previous = undoStack.removeLastOrNull() ?: return false
        state = previous
        return true
    }

    fun resetKeepPlayers() {
        pushUndo()
        state = state.copy(
            currentRound = 1,
            players = state.players.map { it.copy(completedRounds = emptyList(), current = GateCounts()) },
        )
    }

    fun addPlayer(): PlayerState? {
        if (state.players.size >= MAX_PLAYERS) return null
        pushUndo()
        val player = newPlayer()
        state = state.copy(
            players = state.players + player,
            selectedPlayerId = player.id,
        )
        return player
    }

    fun removeSelectedPlayer(): Boolean {
        if (state.players.size <= 1) return false
        pushUndo()
        val remaining = state.players.filterNot { it.id == state.selectedPlayerId }
        state = state.copy(
            players = remaining,
            selectedPlayerId = remaining.first().id,
        )
        return true
    }

    fun renameSelected(name: String) {
        val trimmed = name.trim()
        if (trimmed == state.selected.customName) return
        pushUndo()
        updateSelected { it.copy(customName = trimmed) }
    }

    private fun updateSelected(transform: (PlayerState) -> PlayerState) {
        state = state.copy(
            players = state.players.map { player ->
                if (player.id == state.selectedPlayerId) transform(player) else player
            },
        )
    }

    private fun pushUndo() {
        undoStack.addLast(state)
    }

    private fun newGame(playerCount: Int): ScorekaartState {
        val count = playerCount.coerceIn(1, MAX_PLAYERS)
        nextPlayerId = 1L
        nextPlayerNumber = 1
        undoStack.clear()
        val players = List(count) { newPlayer() }
        return ScorekaartState(
            players = players,
            selectedPlayerId = players.first().id,
            currentRound = 1,
        )
    }

    private fun newPlayer(): PlayerState {
        val player = PlayerState(
            id = nextPlayerId,
            customName = "",
            number = nextPlayerNumber,
            completedRounds = emptyList(),
            current = GateCounts(),
        )
        nextPlayerId += 1
        nextPlayerNumber += 1
        return player
    }

    companion object {
        const val DEFAULT_PLAYER_COUNT = 2
        const val MAX_PLAYERS = 8
    }
}
