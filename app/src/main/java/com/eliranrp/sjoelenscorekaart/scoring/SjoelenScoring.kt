package com.eliranrp.sjoelenscorekaart.scoring

/**
 * Standard NSB / common sjoelen puntentelling for one player in one round.
 *
 * A complete set ("volle bak") is one schijf in every poort (2, 3, 4 and 1).
 * Each complete set scores [SET_POINTS] (20), not the face-value 2+3+4+1=10.
 * Remaining schijven in a poort score that poort's face value.
 * Schijven that never entered a poort are not counted here and score 0.
 */
object SjoelenScoring {
    const val DISCS_PER_ROUND: Int = 30
    const val SET_POINTS: Int = 20

    /**
     * @param gateTwo disc count in poort 2 (leftmost)
     * @param gateThree disc count in poort 3
     * @param gateFour disc count in poort 4
     * @param gateOne disc count in poort 1 (rightmost)
     */
    fun roundScore(
        gateTwo: Int,
        gateThree: Int,
        gateFour: Int,
        gateOne: Int,
    ): Int {
        require(gateTwo >= 0 && gateThree >= 0 && gateFour >= 0 && gateOne >= 0) {
            "Gate counts must be zero or more"
        }
        val inGates = gateTwo + gateThree + gateFour + gateOne
        require(inGates <= DISCS_PER_ROUND) {
            "At most $DISCS_PER_ROUND discs per round (got $inGates)"
        }
        val sets = completeSets(gateTwo, gateThree, gateFour, gateOne)
        val remainderTwo = gateTwo - sets
        val remainderThree = gateThree - sets
        val remainderFour = gateFour - sets
        val remainderOne = gateOne - sets
        return SET_POINTS * sets +
            Gate.TWO.points * remainderTwo +
            Gate.THREE.points * remainderThree +
            Gate.FOUR.points * remainderFour +
            Gate.ONE.points * remainderOne
    }

    fun completeSets(
        gateTwo: Int,
        gateThree: Int,
        gateFour: Int,
        gateOne: Int,
    ): Int = minOf(gateTwo, gateThree, gateFour, gateOne)

    fun hasVolleBak(
        gateTwo: Int,
        gateThree: Int,
        gateFour: Int,
        gateOne: Int,
    ): Boolean = completeSets(gateTwo, gateThree, gateFour, gateOne) > 0

    /** Extra points from complete sets versus face-value only (10 per set). */
    fun setBonusPoints(
        gateTwo: Int,
        gateThree: Int,
        gateFour: Int,
        gateOne: Int,
    ): Int = completeSets(gateTwo, gateThree, gateFour, gateOne) * (SET_POINTS - 10)
}
