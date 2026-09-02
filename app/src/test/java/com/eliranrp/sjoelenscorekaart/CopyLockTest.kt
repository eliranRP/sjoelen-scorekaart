package com.eliranrp.sjoelenscorekaart

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CopyLockTest {

    @Test
    fun nederlandseLauncherNaamIsPuntentelling() {
        val xml = File("src/main/res/values/strings.xml").readText()
        assertTrue(xml.contains(">Sjoelen puntentelling<"))
        assertFalse(xml.contains("Scorekaart"))
        assertFalse(xml.contains("shuffleboard", ignoreCase = true))
        assertFalse(xml.contains("jakkolo", ignoreCase = true))
    }

    @Test
    fun engelseLauncherNaamIsScoreboard() {
        val xml = File("src/main/res/values-en/strings.xml").readText()
        assertTrue(xml.contains(">Sjoelen scoreboard<"))
        assertFalse(xml.contains("Scorekaart"))
        assertFalse(xml.contains("shuffleboard", ignoreCase = true))
        assertFalse(xml.contains("jakkolo", ignoreCase = true))
    }

    @Test
    fun bronManifestZonderInternet() {
        val xml = File("src/main/AndroidManifest.xml").readText()
        assertFalse(xml.contains("INTERNET"))
        assertFalse(xml.contains("android.permission."))
    }
}
