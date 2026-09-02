package com.eliranrp.sjoelenscorekaart.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eliranrp.sjoelenscorekaart.R
import com.eliranrp.sjoelenscorekaart.scoring.Poort
import com.eliranrp.sjoelenscorekaart.scoring.PoortAantallen
import com.eliranrp.sjoelenscorekaart.scoring.Scoring
import com.eliranrp.sjoelenscorekaart.ui.theme.Ink
import com.eliranrp.sjoelenscorekaart.ui.theme.InkMuted
import com.eliranrp.sjoelenscorekaart.ui.theme.OnPlus
import com.eliranrp.sjoelenscorekaart.ui.theme.Paper
import com.eliranrp.sjoelenscorekaart.ui.theme.PaperDark
import com.eliranrp.sjoelenscorekaart.ui.theme.PlusFill
import com.eliranrp.sjoelenscorekaart.ui.theme.Rule
import com.eliranrp.sjoelenscorekaart.ui.theme.Stamp

private val SheetShape = RoundedCornerShape(4.dp)

@Composable
fun ScorekaartScreen(
    modifier: Modifier = Modifier,
    viewModel: ScorekaartViewModel = viewModel(),
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    ScorekaartContent(
        state = state,
        onPlus = viewModel::plus,
        onMin = viewModel::min,
        onUndo = viewModel::undo,
        onNieuweRonde = viewModel::nieuweRonde,
        onResetRonde = viewModel::resetRonde,
        onResetWedstrijd = viewModel::resetWedstrijd,
        onHuisregel = viewModel::setHuisregelVolleBak,
        onSpeler = viewModel::setSpelerNaam,
        onTeam = viewModel::setTeamNaam,
        modifier = modifier,
    )
}

@Composable
fun ScorekaartContent(
    state: ScorekaartUiState,
    onPlus: (Poort) -> Unit,
    onMin: (Poort) -> Unit,
    onUndo: () -> Unit,
    onNieuweRonde: () -> Unit,
    onResetRonde: () -> Unit,
    onResetWedstrijd: () -> Unit,
    onHuisregel: (Boolean) -> Unit,
    onSpeler: (String) -> Unit,
    onTeam: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var resetRondeOpen by rememberSaveable { mutableStateOf(false) }
    var resetWedstrijdOpen by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Paper,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding(),
        ) {
            val landscape = maxWidth > maxHeight && maxWidth >= 700.dp
            if (landscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(0.42f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        Kop()
                        NamenVelden(state, onSpeler, onTeam)
                        Spacer(Modifier.height(8.dp))
                        TotaalBlok(state, compact = true)
                        Spacer(Modifier.height(8.dp))
                        VolleBakRij(state, onHuisregel)
                        Spacer(Modifier.height(8.dp))
                        ActieRij(
                            state = state,
                            onUndo = onUndo,
                            onNieuweRonde = onNieuweRonde,
                            onResetRonde = { resetRondeOpen = true },
                            onResetWedstrijd = { resetWedstrijdOpen = true },
                        )
                        Spacer(Modifier.height(12.dp))
                        RondenLijst(state)
                    }
                    PoortenRij(
                        state = state,
                        onPlus = onPlus,
                        onMin = onMin,
                        huge = true,
                        modifier = Modifier
                            .weight(0.58f)
                            .fillMaxHeight(),
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Kop()
                    NamenVelden(state, onSpeler, onTeam)
                    Spacer(Modifier.height(8.dp))
                    TotaalBlok(state, compact = false)
                    Spacer(Modifier.height(12.dp))
                    PoortenRij(
                        state = state,
                        onPlus = onPlus,
                        onMin = onMin,
                        huge = false,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(12.dp))
                    VolleBakRij(state, onHuisregel)
                    Spacer(Modifier.height(8.dp))
                    ActieRij(
                        state = state,
                        onUndo = onUndo,
                        onNieuweRonde = onNieuweRonde,
                        onResetRonde = { resetRondeOpen = true },
                        onResetWedstrijd = { resetWedstrijdOpen = true },
                    )
                    Spacer(Modifier.height(16.dp))
                    RondenLijst(state)
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }

    if (resetRondeOpen) {
        BevestigDialog(
            titel = stringResource(R.string.reset_ronde_titel),
            tekst = stringResource(R.string.reset_ronde_tekst),
            onBevestig = {
                onResetRonde()
                resetRondeOpen = false
            },
            onAnnuleer = { resetRondeOpen = false },
        )
    }
    if (resetWedstrijdOpen) {
        BevestigDialog(
            titel = stringResource(R.string.reset_wedstrijd_titel),
            tekst = stringResource(R.string.reset_wedstrijd_tekst),
            onBevestig = {
                onResetWedstrijd()
                resetWedstrijdOpen = false
            },
            onAnnuleer = { resetWedstrijdOpen = false },
        )
    }
}

@Composable
private fun Kop() {
    Text(
        text = stringResource(R.string.app_name),
        style = MaterialTheme.typography.headlineLarge,
        color = Ink,
        modifier = Modifier.testTag("app_title"),
    )
    Text(
        text = stringResource(R.string.kaart_ondertitel),
        style = MaterialTheme.typography.bodyLarge,
        color = InkMuted,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun NamenVelden(
    state: ScorekaartUiState,
    onSpeler: (String) -> Unit,
    onTeam: (String) -> Unit,
) {
    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Ink,
        unfocusedBorderColor = Rule,
        focusedLabelColor = Ink,
        cursorColor = Ink,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = state.spelerNaam,
            onValueChange = onSpeler,
            label = { Text(stringResource(R.string.speler_hint)) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = colors,
        )
        OutlinedTextField(
            value = state.teamNaam,
            onValueChange = onTeam,
            label = { Text(stringResource(R.string.team_hint)) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = colors,
        )
    }
}

@Composable
private fun TotaalBlok(state: ScorekaartUiState, compact: Boolean) {
    val scoreSize = if (compact) 56.sp else 72.sp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SheetShape)
            .background(PaperDark)
            .border(1.dp, Rule, SheetShape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = stringResource(R.string.wedstrijd_totaal).uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = InkMuted,
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = state.wedstrijdTotaal.toString(),
                color = Ink,
                fontWeight = FontWeight.Black,
                fontSize = scoreSize,
                lineHeight = scoreSize,
                letterSpacing = (-1.5).sp,
                modifier = Modifier.testTag("wedstrijd_totaal"),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.punten),
                style = MaterialTheme.typography.titleLarge,
                color = InkMuted,
                modifier = Modifier.padding(bottom = 10.dp),
            )
        }
        Text(
            text = stringResource(R.string.ronde_label, state.rondeNummer) +
                " · ${state.huidigePunten} " + stringResource(R.string.punten),
            style = MaterialTheme.typography.titleLarge,
            color = Ink,
        )
        Text(
            text = stringResource(R.string.schijven_over, state.schijvenOver),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Ink,
            modifier = Modifier
                .padding(top = 4.dp)
                .testTag("schijven_over"),
        )
        Text(
            text = stringResource(R.string.schijven_geplaatst, state.huidigeRonde.totaalSchijven),
            style = MaterialTheme.typography.bodyLarge,
            color = InkMuted,
        )
    }
}

@Composable
private fun PoortenRij(
    state: ScorekaartUiState,
    onPlus: (Poort) -> Unit,
    onMin: (Poort) -> Unit,
    huge: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Poort.vanLinksNaarRechts.forEach { poort ->
            PoortKolom(
                poort = poort,
                aantal = state.huidigeRonde.aantal(poort),
                kanPlus = state.kanPlus,
                onPlus = { onPlus(poort) },
                onMin = { onMin(poort) },
                huge = huge,
            )
        }
    }
}

@Composable
private fun RowScope.PoortKolom(
    poort: Poort,
    aantal: Int,
    kanPlus: Boolean,
    onPlus: () -> Unit,
    onMin: () -> Unit,
    huge: Boolean,
) {
    val plusDesc = stringResource(R.string.plus_poort, poort.label)
    val minDesc = stringResource(R.string.min_poort, poort.label)
    val countSize = if (huge) 64.sp else 48.sp
    val tap = if (huge) 88.dp else 76.dp

    Column(
        modifier = Modifier
            .weight(1f)
            .clip(SheetShape)
            .background(Paper)
            .border(2.dp, Ink, SheetShape)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.poort_label, poort.label),
            style = MaterialTheme.typography.labelLarge,
            color = InkMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = poort.label,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = Ink,
        )
        Text(
            text = aantal.toString(),
            fontWeight = FontWeight.Black,
            fontSize = countSize,
            lineHeight = countSize,
            color = Ink,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("poort_${poort.label}"),
        )
        FilledIconButton(
            onClick = onPlus,
            enabled = kanPlus,
            modifier = Modifier
                .size(tap)
                .semantics { contentDescription = plusDesc }
                .testTag("plus_${poort.label}"),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = PlusFill,
                contentColor = OnPlus,
                disabledContainerColor = Rule,
                disabledContentColor = InkMuted,
            ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(36.dp))
        }
        Spacer(Modifier.height(6.dp))
        FilledIconButton(
            onClick = onMin,
            enabled = aantal > 0,
            modifier = Modifier
                .size(tap)
                .semantics { contentDescription = minDesc }
                .testTag("min_${poort.label}"),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = PaperDark,
                contentColor = Ink,
                disabledContainerColor = PaperDark.copy(alpha = 0.4f),
                disabledContentColor = InkMuted,
            ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Icon(Icons.Filled.Remove, contentDescription = null, modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
private fun VolleBakRij(
    state: ScorekaartUiState,
    onHuisregel: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SheetShape)
            .background(if (state.volleBakBonusActief) Stamp.copy(alpha = 0.12f) else PaperDark)
            .border(1.dp, if (state.volleBakBonusActief) Stamp else Rule, SheetShape)
            .padding(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.volle_bak_huisregel),
                    style = MaterialTheme.typography.titleLarge,
                    color = if (state.volleBakBonusActief) Stamp else Ink,
                )
                Text(
                    text = stringResource(R.string.volle_bak_uitleg),
                    style = MaterialTheme.typography.bodyLarge,
                    color = InkMuted,
                )
            }
            Switch(
                checked = state.huisregelVolleBak,
                onCheckedChange = onHuisregel,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Paper,
                    checkedTrackColor = Stamp,
                    uncheckedThumbColor = InkMuted,
                    uncheckedTrackColor = Rule,
                ),
                modifier = Modifier.testTag("volle_bak_switch"),
            )
        }
        if (state.volleBakBonusActief) {
            Text(
                text = stringResource(R.string.volle_bak_actief),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Stamp,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .testTag("volle_bak_bonus"),
            )
        }
    }
}

@Composable
private fun ActieRij(
    state: ScorekaartUiState,
    onUndo: () -> Unit,
    onNieuweRonde: () -> Unit,
    onResetRonde: () -> Unit,
    onResetWedstrijd: () -> Unit,
) {
    val tonal = ButtonDefaults.filledTonalButtonColors(
        containerColor = PaperDark,
        contentColor = Ink,
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            FilledTonalButton(
                onClick = onUndo,
                enabled = state.kanOngedaan,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .testTag("undo"),
                colors = tonal,
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) {
                Icon(Icons.Filled.Undo, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.ongedaan), fontWeight = FontWeight.Bold)
            }
            FilledTonalButton(
                onClick = onNieuweRonde,
                enabled = state.huidigeRonde.totaalSchijven > 0,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .testTag("nieuwe_ronde"),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Ink,
                    contentColor = Paper,
                    disabledContainerColor = Rule,
                    disabledContentColor = InkMuted,
                ),
            ) {
                Text(stringResource(R.string.nieuwe_ronde), fontWeight = FontWeight.Bold)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            FilledTonalButton(
                onClick = onResetRonde,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .testTag("reset_ronde"),
                colors = tonal,
            ) {
                Text(stringResource(R.string.reset_ronde), fontWeight = FontWeight.Bold)
            }
            FilledTonalButton(
                onClick = onResetWedstrijd,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .testTag("reset_wedstrijd"),
                colors = tonal,
            ) {
                Text(stringResource(R.string.reset_wedstrijd), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RondenLijst(state: ScorekaartUiState) {
    Text(
        text = stringResource(R.string.ronden),
        style = MaterialTheme.typography.headlineMedium,
        color = Ink,
    )
    HorizontalDivider(color = Rule, thickness = 1.dp, modifier = Modifier.padding(vertical = 6.dp))
    if (state.afgerondeRonden.isEmpty()) {
        Text(
            text = stringResource(R.string.lege_ronden),
            style = MaterialTheme.typography.bodyLarge,
            color = InkMuted,
        )
    } else {
        state.afgerondeRonden.forEachIndexed { index, ronde ->
            val punten = Scoring.rondePunten(ronde, state.huisregelVolleBak)
            RondeRegel(nummer = index + 1, ronde = ronde, punten = punten)
        }
    }
    if (state.huidigeRonde.totaalSchijven > 0) {
        RondeRegel(
            nummer = state.rondeNummer,
            ronde = state.huidigeRonde,
            punten = state.huidigePunten,
            huidig = true,
        )
    }
}

@Composable
private fun RondeRegel(
    nummer: Int,
    ronde: PoortAantallen,
    punten: Int,
    huidig: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
    ) {
        Text(
            text = stringResource(R.string.ronde_regel, nummer, punten) +
                if (huidig) " · " + stringResource(R.string.huidige_ronde) else "",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Ink,
        )
        Text(
            text = stringResource(
                R.string.ronde_detail,
                ronde.n2,
                ronde.n3,
                ronde.n4,
                ronde.n1,
            ),
            fontSize = 20.sp,
            color = InkMuted,
        )
    }
}

@Composable
private fun BevestigDialog(
    titel: String,
    tekst: String,
    onBevestig: () -> Unit,
    onAnnuleer: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onAnnuleer,
        title = { Text(titel, fontWeight = FontWeight.Bold) },
        text = { Text(tekst) },
        confirmButton = {
            TextButton(onClick = onBevestig) {
                Text(stringResource(R.string.bevestig_reset), color = Stamp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onAnnuleer) {
                Text(stringResource(R.string.annuleer), color = Ink)
            }
        },
        containerColor = Paper,
    )
}
