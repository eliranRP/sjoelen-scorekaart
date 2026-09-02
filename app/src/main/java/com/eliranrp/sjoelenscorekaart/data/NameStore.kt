package com.eliranrp.sjoelenscorekaart.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.nameDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "sjoelen_namen",
)

/**
 * Last player/team names only. Fully on-device, no network.
 */
class NameStore(private val context: Context) {
    private val spelerKey = stringPreferencesKey("speler")
    private val teamKey = stringPreferencesKey("team")

    suspend fun load(): Pair<String, String> {
        val prefs = context.nameDataStore.data.first()
        return (prefs[spelerKey].orEmpty()) to (prefs[teamKey].orEmpty())
    }

    suspend fun save(speler: String, team: String) {
        context.nameDataStore.edit { prefs ->
            prefs[spelerKey] = speler
            prefs[teamKey] = team
        }
    }
}
