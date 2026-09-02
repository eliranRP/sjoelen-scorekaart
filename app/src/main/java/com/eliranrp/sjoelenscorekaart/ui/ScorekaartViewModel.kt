package com.eliranrp.sjoelenscorekaart.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eliranrp.sjoelenscorekaart.data.NameStore
import com.eliranrp.sjoelenscorekaart.scoring.Poort
import com.eliranrp.sjoelenscorekaart.scoring.PoortAantallen
import com.eliranrp.sjoelenscorekaart.scoring.Scoring
import com.eliranrp.sjoelenscorekaart.scoring.Wedstrijd
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScorekaartUiState(
    val spelerNaam: String = "",
    val teamNaam: String = "",
    val huidigeRonde: PoortAantallen = PoortAantallen(),
    val afgerondeRonden: List<PoortAantallen> = emptyList(),
    val huisregelVolleBak: Boolean = true,
    val rondeNummer: Int = 1,
    val schijvenOver: Int = Scoring.SCHIJVEN_PER_RONDE,
    val huidigePunten: Int = 0,
    val wedstrijdTotaal: Int = 0,
    val volleBakBonusActief: Boolean = false,
    val kanOngedaan: Boolean = false,
    val kanPlus: Boolean = true,
)

class ScorekaartViewModel(
    application: Application,
    private val names: NameStore = NameStore(application),
    private val wedstrijd: Wedstrijd = Wedstrijd(),
) : AndroidViewModel(application) {

    private val _ui = MutableStateFlow(snapshot())
    val ui: StateFlow<ScorekaartUiState> = _ui.asStateFlow()

    private var saveJob: Job? = null

    init {
        viewModelScope.launch {
            val (speler, team) = names.load()
            wedstrijd.setSpelerNaam(speler)
            wedstrijd.setTeamNaam(team)
            publish()
        }
    }

    fun plus(poort: Poort) {
        wedstrijd.plus(poort)
        publish()
    }

    fun min(poort: Poort) {
        wedstrijd.min(poort)
        publish()
    }

    fun undo() {
        wedstrijd.undo()
        publish()
    }

    fun nieuweRonde() {
        wedstrijd.nieuweRonde()
        publish()
    }

    fun resetRonde() {
        wedstrijd.resetRonde()
        publish()
    }

    fun resetWedstrijd() {
        wedstrijd.resetWedstrijd()
        publish()
    }

    fun setHuisregelVolleBak(aan: Boolean) {
        wedstrijd.setHuisregelVolleBak(aan)
        publish()
    }

    fun setSpelerNaam(naam: String) {
        wedstrijd.setSpelerNaam(naam)
        publish()
        persistNames()
    }

    fun setTeamNaam(naam: String) {
        wedstrijd.setTeamNaam(naam)
        publish()
        persistNames()
    }

    private fun persistNames() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(400)
            names.save(wedstrijd.spelerNaam, wedstrijd.teamNaam)
        }
    }

    private fun publish() {
        _ui.value = snapshot()
    }

    private fun snapshot(): ScorekaartUiState = ScorekaartUiState(
        spelerNaam = wedstrijd.spelerNaam,
        teamNaam = wedstrijd.teamNaam,
        huidigeRonde = wedstrijd.huidigeRonde,
        afgerondeRonden = wedstrijd.afgerondeRonden,
        huisregelVolleBak = wedstrijd.huisregelVolleBak,
        rondeNummer = wedstrijd.rondeNummer,
        schijvenOver = wedstrijd.schijvenOver,
        huidigePunten = wedstrijd.huidigePunten,
        wedstrijdTotaal = wedstrijd.wedstrijdTotaal,
        volleBakBonusActief = wedstrijd.volleBakBonusActief,
        kanOngedaan = wedstrijd.kanOngedaan,
        kanPlus = wedstrijd.huidigeRonde.totaalSchijven < Scoring.SCHIJVEN_PER_RONDE,
    )
}
