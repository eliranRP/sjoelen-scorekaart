package com.eliranrp.sjoelenscorekaart.scoring

/**
 * Poorten as seen from the player: 2 / 3 / 4 / 1 left to right.
 */
enum class Poort(val label: String, val waarde: Int) {
    TWEE("2", 2),
    DRIE("3", 3),
    VIER("4", 4),
    EEN("1", 1);

    companion object {
        val vanLinksNaarRechts: List<Poort> = listOf(TWEE, DRIE, VIER, EEN)
    }
}
