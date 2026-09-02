package com.eliranrp.sjoelenscorekaart.scoring

/**
 * The four poorten of a sjoelbak, left to right as the player faces the bak.
 * Values are 2, 3, 4, 1 — never 1-2-3-4.
 */
enum class Gate(
    val points: Int,
    val leftToRightIndex: Int,
) {
    TWO(points = 2, leftToRightIndex = 0),
    THREE(points = 3, leftToRightIndex = 1),
    FOUR(points = 4, leftToRightIndex = 2),
    ONE(points = 1, leftToRightIndex = 3),
    ;

    companion object {
        val leftToRight: List<Gate> = entries.sortedBy { it.leftToRightIndex }
    }
}
