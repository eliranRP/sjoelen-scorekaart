package com.eliranrp.sjoelenscorekaart.scoring

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WedstrijdTest {

    @Test
    fun poortenVanLinksNaarRechtsZijn2341() {
        assertEquals(
            listOf("2", "3", "4", "1"),
            Poort.vanLinksNaarRechts.map { it.label },
        )
    }

    @Test
    fun plusEnMinPerPoort() {
        val w = Wedstrijd()
        assertTrue(w.plus(Poort.TWEE))
        assertTrue(w.plus(Poort.TWEE))
        assertTrue(w.plus(Poort.VIER))
        assertEquals(2, w.huidigeRonde.n2)
        assertEquals(1, w.huidigeRonde.n4)
        assertEquals(27, w.schijvenOver)
        assertTrue(w.min(Poort.TWEE))
        assertEquals(1, w.huidigeRonde.n2)
        assertFalse(w.min(Poort.DRIE))
    }

    @Test
    fun capOpDertigSchijven() {
        val w = Wedstrijd()
        repeat(10) { w.plus(Poort.TWEE) }
        repeat(10) { w.plus(Poort.DRIE) }
        repeat(10) { w.plus(Poort.VIER) }
        assertEquals(30, w.huidigeRonde.totaalSchijven)
        assertEquals(0, w.schijvenOver)
        assertFalse(w.plus(Poort.EEN))
        assertFalse(w.plus(Poort.TWEE))
        assertEquals(0, w.huidigeRonde.n1)
        assertEquals(30, w.huidigeRonde.totaalSchijven)
    }

    @Test
    fun volleBakBonusAutoAlsAlleVierGeraaktEnHuisregelAan() {
        val w = Wedstrijd()
        w.plus(Poort.TWEE)
        w.plus(Poort.DRIE)
        w.plus(Poort.VIER)
        assertEquals(9, w.huidigePunten)
        assertFalse(w.volleBakBonusActief)
        w.plus(Poort.EEN)
        assertTrue(w.huidigeRonde.isVolleBak)
        assertTrue(w.volleBakBonusActief)
        assertEquals(10 + 20, w.huidigePunten)
    }

    @Test
    fun volleBakToggleSchakeltHuisregelUit() {
        val w = Wedstrijd()
        w.plus(Poort.TWEE)
        w.plus(Poort.DRIE)
        w.plus(Poort.VIER)
        w.plus(Poort.EEN)
        assertEquals(30, w.huidigePunten)
        w.setHuisregelVolleBak(false)
        assertFalse(w.volleBakBonusActief)
        assertEquals(10, w.huidigePunten)
        w.setHuisregelVolleBak(true)
        assertEquals(30, w.huidigePunten)
    }

    @Test
    fun nieuweRondeHoudtWedstrijdTotaal() {
        val w = Wedstrijd()
        w.plus(Poort.VIER)
        w.plus(Poort.VIER)
        val ronde1 = w.huidigePunten
        assertEquals(8, ronde1)
        assertTrue(w.nieuweRonde())
        assertEquals(0, w.huidigeRonde.totaalSchijven)
        assertEquals(1, w.afgerondeRonden.size)
        assertEquals(ronde1, w.wedstrijdTotaal)
        w.plus(Poort.EEN)
        w.plus(Poort.EEN)
        assertEquals(ronde1 + 2, w.wedstrijdTotaal)
        assertEquals(2, w.rondeNummer)
    }

    @Test
    fun nieuweRondeMetLegeRondeDoetNiets() {
        val w = Wedstrijd()
        assertFalse(w.nieuweRonde())
        assertEquals(1, w.rondeNummer)
    }

    @Test
    fun resetRondeWistHuidigeMaarHoudtEerdere() {
        val w = Wedstrijd()
        w.plus(Poort.DRIE)
        w.nieuweRonde()
        w.plus(Poort.TWEE)
        w.plus(Poort.TWEE)
        assertEquals(3 + 4, w.wedstrijdTotaal)
        assertTrue(w.resetRonde())
        assertEquals(0, w.huidigeRonde.totaalSchijven)
        assertEquals(1, w.afgerondeRonden.size)
        assertEquals(3, w.wedstrijdTotaal)
    }

    @Test
    fun resetWedstrijdWistAllesBehalveNamen() {
        val w = Wedstrijd()
        w.setSpelerNaam("Jan")
        w.setTeamNaam("De Bak")
        w.plus(Poort.EEN)
        w.nieuweRonde()
        w.plus(Poort.VIER)
        assertTrue(w.resetWedstrijd())
        assertEquals(0, w.huidigeRonde.totaalSchijven)
        assertTrue(w.afgerondeRonden.isEmpty())
        assertEquals(0, w.wedstrijdTotaal)
        assertEquals("Jan", w.spelerNaam)
        assertEquals("De Bak", w.teamNaam)
    }

    @Test
    fun undoLaatsteTap() {
        val w = Wedstrijd()
        w.plus(Poort.TWEE)
        w.plus(Poort.DRIE)
        assertEquals(5, w.huidigePunten)
        assertTrue(w.undo())
        assertEquals(0, w.huidigeRonde.n3)
        assertEquals(1, w.huidigeRonde.n2)
        assertTrue(w.undo())
        assertEquals(0, w.huidigeRonde.totaalSchijven)
        assertFalse(w.undo())
    }

    @Test
    fun undoNieuweRondeEnReset() {
        val w = Wedstrijd()
        w.plus(Poort.VIER)
        w.nieuweRonde()
        assertEquals(1, w.afgerondeRonden.size)
        assertTrue(w.undo())
        assertEquals(0, w.afgerondeRonden.size)
        assertEquals(1, w.huidigeRonde.n4)
        w.resetRonde()
        assertEquals(0, w.huidigeRonde.n4)
        w.undo()
        assertEquals(1, w.huidigeRonde.n4)
    }

    @Test
    fun undoVolleBakToggle() {
        val w = Wedstrijd()
        w.plus(Poort.TWEE)
        w.plus(Poort.DRIE)
        w.plus(Poort.VIER)
        w.plus(Poort.EEN)
        assertEquals(30, w.huidigePunten)
        w.setHuisregelVolleBak(false)
        assertEquals(10, w.huidigePunten)
        w.undo()
        assertTrue(w.huisregelVolleBak)
        assertEquals(30, w.huidigePunten)
    }
}
