package com.eliranrp.sjoelenscorekaart.scoring

data class PoortAantallen(
    val n2: Int = 0,
    val n3: Int = 0,
    val n4: Int = 0,
    val n1: Int = 0,
) {
    init {
        require(n2 >= 0 && n3 >= 0 && n4 >= 0 && n1 >= 0) {
            "Schijven per poort kunnen niet negatief zijn"
        }
    }

    val totaalSchijven: Int get() = n2 + n3 + n4 + n1

    val isVolleBak: Boolean get() = n2 > 0 && n3 > 0 && n4 > 0 && n1 > 0

    fun aantal(poort: Poort): Int = when (poort) {
        Poort.TWEE -> n2
        Poort.DRIE -> n3
        Poort.VIER -> n4
        Poort.EEN -> n1
    }

    fun met(poort: Poort, aantal: Int): PoortAantallen = when (poort) {
        Poort.TWEE -> copy(n2 = aantal)
        Poort.DRIE -> copy(n3 = aantal)
        Poort.VIER -> copy(n4 = aantal)
        Poort.EEN -> copy(n1 = aantal)
    }

    fun basisPunten(): Int = n2 * 2 + n3 * 3 + n4 * 4 + n1 * 1
}

object Scoring {
    const val SCHIJVEN_PER_RONDE = 30
    const val VOLLE_BAK_BONUS = 20

    fun rondePunten(
        aantallen: PoortAantallen,
        huisregelVolleBak: Boolean,
    ): Int {
        val bonus = if (huisregelVolleBak && aantallen.isVolleBak) VOLLE_BAK_BONUS else 0
        return aantallen.basisPunten() + bonus
    }
}
