package com.eliranrp.sjoelenscorekaart.scoring

/**
 * In-memory match: current round, finished rounds, house-rule toggle, undo stack.
 * Names are held here for the UI but persist separately.
 */
class Wedstrijd(
    private val maxSchijven: Int = Scoring.SCHIJVEN_PER_RONDE,
) {
    var huidigeRonde: PoortAantallen = PoortAantallen()
        private set

    var afgerondeRonden: List<PoortAantallen> = emptyList()
        private set

    var huisregelVolleBak: Boolean = true
        private set

    var spelerNaam: String = ""
        private set

    var teamNaam: String = ""
        private set

    private val undoStack = ArrayDeque<Snapshot>()

    val kanOngedaan: Boolean get() = undoStack.isNotEmpty()

    val rondeNummer: Int get() = afgerondeRonden.size + 1

    val schijvenOver: Int get() = (maxSchijven - huidigeRonde.totaalSchijven).coerceAtLeast(0)

    val huidigePunten: Int
        get() = Scoring.rondePunten(huidigeRonde, huisregelVolleBak)

    val volleBakBonusActief: Boolean
        get() = huisregelVolleBak && huidigeRonde.isVolleBak

    val wedstrijdTotaal: Int
        get() = afgerondeRonden.sumOf { Scoring.rondePunten(it, huisregelVolleBak) } + huidigePunten

    fun plus(poort: Poort): Boolean {
        if (huidigeRonde.totaalSchijven >= maxSchijven) return false
        snapshot()
        huidigeRonde = huidigeRonde.met(poort, huidigeRonde.aantal(poort) + 1)
        return true
    }

    fun min(poort: Poort): Boolean {
        val huidig = huidigeRonde.aantal(poort)
        if (huidig <= 0) return false
        snapshot()
        huidigeRonde = huidigeRonde.met(poort, huidig - 1)
        return true
    }

    fun setHuisregelVolleBak(aan: Boolean) {
        if (huisregelVolleBak == aan) return
        snapshot()
        huisregelVolleBak = aan
    }

    /**
     * Archives the current round (if it has discs) and starts a fresh one.
     * Match total keeps completed rounds plus the new empty current round.
     */
    fun nieuweRonde(): Boolean {
        if (huidigeRonde.totaalSchijven == 0) return false
        snapshot()
        afgerondeRonden = afgerondeRonden + huidigeRonde
        huidigeRonde = PoortAantallen()
        return true
    }

    fun resetRonde(): Boolean {
        if (huidigeRonde == PoortAantallen()) return false
        snapshot()
        huidigeRonde = PoortAantallen()
        return true
    }

    fun resetWedstrijd(): Boolean {
        if (huidigeRonde == PoortAantallen() && afgerondeRonden.isEmpty()) return false
        snapshot()
        huidigeRonde = PoortAantallen()
        afgerondeRonden = emptyList()
        return true
    }

    fun undo(): Boolean {
        val vorige = undoStack.removeLastOrNull() ?: return false
        huidigeRonde = vorige.huidigeRonde
        afgerondeRonden = vorige.afgerondeRonden
        huisregelVolleBak = vorige.huisregelVolleBak
        return true
    }

    fun setSpelerNaam(naam: String) {
        spelerNaam = naam
    }

    fun setTeamNaam(naam: String) {
        teamNaam = naam
    }

    private fun snapshot() {
        undoStack.addLast(
            Snapshot(
                huidigeRonde = huidigeRonde,
                afgerondeRonden = afgerondeRonden,
                huisregelVolleBak = huisregelVolleBak,
            ),
        )
    }

    private data class Snapshot(
        val huidigeRonde: PoortAantallen,
        val afgerondeRonden: List<PoortAantallen>,
        val huisregelVolleBak: Boolean,
    )
}
