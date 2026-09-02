package com.eliranrp.sjoelenscorekaart.ui

import androidx.lifecycle.ViewModel
import com.eliranrp.sjoelenscorekaart.domain.Scorekaart
import com.eliranrp.sjoelenscorekaart.domain.ScorekaartState
import com.eliranrp.sjoelenscorekaart.scoring.Gate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ScorekaartUiState(
    val game: ScorekaartState,
    val canUndo: Boolean,
)

class ScorekaartViewModel(
    private val scorekaart: Scorekaart = Scorekaart(),
) : ViewModel() {
    private val _ui = MutableStateFlow(snapshot())
    val ui: StateFlow<ScorekaartUiState> = _ui.asStateFlow()

    fun selectPlayer(id: Long) {
        scorekaart.selectPlayer(id)
        publish()
    }

    fun increment(gate: Gate) {
        scorekaart.increment(gate)
        publish()
    }

    fun decrement(gate: Gate) {
        scorekaart.decrement(gate)
        publish()
    }

    fun nextRound() {
        scorekaart.nextRound()
        publish()
    }

    fun undo() {
        scorekaart.undo()
        publish()
    }

    fun resetKeepPlayers() {
        scorekaart.resetKeepPlayers()
        publish()
    }

    fun addPlayer() {
        scorekaart.addPlayer()
        publish()
    }

    fun removeSelectedPlayer() {
        scorekaart.removeSelectedPlayer()
        publish()
    }

    fun renameSelected(name: String) {
        scorekaart.renameSelected(name)
        publish()
    }

    private fun snapshot(): ScorekaartUiState = ScorekaartUiState(
        game = scorekaart.state,
        canUndo = scorekaart.canUndo,
    )

    private fun publish() {
        _ui.value = snapshot()
    }
}
