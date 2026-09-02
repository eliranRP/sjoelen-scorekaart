package com.eliranrp.sjoelenscorekaart.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eliranrp.sjoelenscorekaart.R
import com.eliranrp.sjoelenscorekaart.domain.GateCounts
import com.eliranrp.sjoelenscorekaart.domain.PlayerState
import com.eliranrp.sjoelenscorekaart.domain.Scorekaart
import com.eliranrp.sjoelenscorekaart.scoring.Gate
import com.eliranrp.sjoelenscorekaart.scoring.SjoelenScoring
import com.eliranrp.sjoelenscorekaart.ui.theme.BakRail
import com.eliranrp.sjoelenscorekaart.ui.theme.Brass
import com.eliranrp.sjoelenscorekaart.ui.theme.BrassDeep
import com.eliranrp.sjoelenscorekaart.ui.theme.ChipIdle
import com.eliranrp.sjoelenscorekaart.ui.theme.ChipSelected
import com.eliranrp.sjoelenscorekaart.ui.theme.Felt
import com.eliranrp.sjoelenscorekaart.ui.theme.GateWood
import com.eliranrp.sjoelenscorekaart.ui.theme.InkBrown
import com.eliranrp.sjoelenscorekaart.ui.theme.MinusRed
import com.eliranrp.sjoelenscorekaart.ui.theme.PaperCream
import com.eliranrp.sjoelenscorekaart.ui.theme.PaperEdge
import com.eliranrp.sjoelenscorekaart.ui.theme.PlusGreen
import com.eliranrp.sjoelenscorekaart.ui.theme.VolleBak
import com.eliranrp.sjoelenscorekaart.ui.theme.WoodDark
import com.eliranrp.sjoelenscorekaart.ui.theme.WoodLight
import com.eliranrp.sjoelenscorekaart.ui.theme.WoodMid

@Composable
fun ScorekaartRoute(viewModel: ScorekaartViewModel = viewModel()) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    ScorekaartScreen(
        ui = ui,
        onSelectPlayer = viewModel::selectPlayer,
        onIncrement = viewModel::increment,
        onDecrement = viewModel::decrement,
        onUndo = viewModel::undo,
        onNextRound = viewModel::nextRound,
        onReset = viewModel::resetKeepPlayers,
        onAddPlayer = viewModel::addPlayer,
        onRemovePlayer = viewModel::removeSelectedPlayer,
        onRename = viewModel::renameSelected,
    )
}

@Composable
fun ScorekaartScreen(
    ui: ScorekaartUiState,
    onSelectPlayer: (Long) -> Unit,
    onIncrement: (Gate) -> Unit,
    onDecrement: (Gate) -> Unit,
    onUndo: () -> Unit,
    onNextRound: () -> Unit,
    onReset: () -> Unit,
    onAddPlayer: () -> Unit,
    onRemovePlayer: () -> Unit,
    onRename: (String) -> Unit,
) {
    val game = ui.game
    val selected = game.selected
    var showResetConfirm by rememberSaveable { mutableStateOf(false) }
    var showRename by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(WoodDark, WoodMid, Color(0xFF2A160C)),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            PaperHeader(round = game.currentRound)
            Spacer(Modifier.height(10.dp))
            PlayerRow(
                players = game.players,
                selectedId = game.selectedPlayerId,
                canAdd = game.players.size < Scorekaart.MAX_PLAYERS,
                canRemove = game.players.size > 1,
                onSelect = onSelectPlayer,
                onAdd = onAddPlayer,
                onRemove = onRemovePlayer,
                onRename = { showRename = true },
            )
            Spacer(Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                TotalsCard(player = selected)
                Spacer(Modifier.height(12.dp))
                SjoelbakBoard(
                    counts = selected.current,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement,
                )
                Spacer(Modifier.height(12.dp))
                RoundHistory(rounds = selected.completedRounds)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.footer_hint),
                    color = Brass.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.height(8.dp))
            ActionBar(
                canUndo = ui.canUndo,
                onUndo = onUndo,
                onNextRound = onNextRound,
                onReset = { showResetConfirm = true },
            )
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text(stringResource(R.string.reset_title)) },
            text = { Text(stringResource(R.string.reset_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetConfirm = false
                        onReset()
                    },
                ) {
                    Text(stringResource(R.string.reset_confirm), color = MinusRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (showRename) {
        var draft by rememberSaveable(selected.id) { mutableStateOf(selected.customName) }
        AlertDialog(
            onDismissRequest = { showRename = false },
            title = { Text(stringResource(R.string.rename_player)) },
            text = {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it.take(24) },
                    label = { Text(stringResource(R.string.player_name_optional)) },
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRename(draft)
                        showRename = false
                    },
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRename = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun PaperHeader(round: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(8.dp)),
        color = PaperCream,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, BrassDeep),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.header_brand).uppercase(),
                color = WoodMid,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
            )
            Text(
                text = stringResource(R.string.header_title).uppercase(),
                color = InkBrown,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                letterSpacing = 1.sp,
            )
            Text(
                text = stringResource(R.string.round_label, round),
                color = WoodMid,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
            )
        }
    }
}

@Composable
private fun PlayerRow(
    players: List<PlayerState>,
    selectedId: Long,
    canAdd: Boolean,
    canRemove: Boolean,
    onSelect: (Long) -> Unit,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onRename: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(players, key = { it.id }) { player ->
                val selected = player.id == selectedId
                val custom = player.customName.trim()
                val name = custom.ifEmpty { stringResource(R.string.player_default, player.number) }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) ChipSelected else ChipIdle)
                        .border(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) BrassDeep else WoodLight,
                            shape = RoundedCornerShape(20.dp),
                        )
                        .clickable { onSelect(player.id) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = name,
                        color = InkBrown,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        IconButton(onClick = onRename) {
            Icon(
                Icons.Filled.Edit,
                contentDescription = stringResource(R.string.rename_player),
                tint = Brass,
            )
        }
        IconButton(onClick = onAdd, enabled = canAdd) {
            Icon(
                Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_player),
                tint = if (canAdd) Brass else Brass.copy(alpha = 0.4f),
            )
        }
        IconButton(onClick = onRemove, enabled = canRemove) {
            Icon(
                Icons.Filled.PersonRemove,
                contentDescription = stringResource(R.string.remove_player),
                tint = if (canRemove) Brass else Brass.copy(alpha = 0.4f),
            )
        }
    }
}

@Composable
private fun TotalsCard(player: PlayerState) {
    val current = player.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PaperEdge,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrassDeep),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.total_label).uppercase(),
                        color = WoodMid,
                        fontSize = 13.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = player.runningTotal.toString(),
                        color = InkBrown,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 56.sp,
                        lineHeight = 58.sp,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.this_round_label),
                        color = WoodMid,
                        fontSize = 14.sp,
                    )
                    Text(
                        text = current.points.toString(),
                        color = InkBrown,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = stringResource(
                            R.string.discs_used,
                            current.discsInGates,
                            SjoelenScoring.DISCS_PER_ROUND,
                        ),
                        color = InkBrown,
                        fontSize = 15.sp,
                    )
                    Text(
                        text = stringResource(R.string.outside_gates, current.discsOutside),
                        color = WoodMid,
                        fontSize = 13.sp,
                    )
                }
                if (current.hasVolleBak) {
                    VolleBakBadge(sets = current.completeSets)
                }
            }
        }
    }
}

@Composable
private fun VolleBakBadge(sets: Int) {
    val label = if (sets <= 1) {
        stringResource(R.string.volle_bak)
    } else {
        stringResource(R.string.volle_bak_sets, sets)
    }
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(VolleBak)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label.uppercase(),
            color = PaperCream,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
        Text(
            text = stringResource(R.string.volle_bak_points, sets * SjoelenScoring.SET_POINTS),
            color = PaperCream,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
    }
}

@Composable
private fun SjoelbakBoard(
    counts: GateCounts,
    onIncrement: (Gate) -> Unit,
    onDecrement: (Gate) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFA06C3A), BakRail, Color(0xFF4A2C14)),
                ),
            )
            .padding(10.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(BrassDeep),
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Gate.leftToRight.forEach { gate ->
                GateColumn(
                    gate = gate,
                    count = counts[gate],
                    canIncrement = counts.canIncrement(),
                    canDecrement = counts.canDecrement(gate),
                    onIncrement = { onIncrement(gate) },
                    onDecrement = { onDecrement(gate) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(WoodDark),
        )
    }
}

@Composable
private fun GateColumn(
    gate: Gate,
    count: Int,
    canIncrement: Boolean,
    canDecrement: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val incrementDesc = stringResource(R.string.increment_gate, gate.points)
    val decrementDesc = stringResource(R.string.decrement_gate, gate.points)
    val countDesc = stringResource(R.string.discs_in_gate, count, gate.points)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(GateWood)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = gate.points.toString(),
            color = Brass,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 42.sp,
            lineHeight = 44.sp,
        )
        GateOpening()
        Spacer(Modifier.height(6.dp))
        HugeTapButton(
            label = "+",
            contentDescription = incrementDesc,
            enabled = canIncrement,
            container = PlusGreen,
            onClick = onIncrement,
        )
        Text(
            text = count.toString(),
            color = PaperCream,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 48.sp,
            lineHeight = 50.sp,
            modifier = Modifier.semantics { contentDescription = countDesc },
        )
        HugeTapButton(
            label = "−",
            contentDescription = decrementDesc,
            enabled = canDecrement,
            container = MinusRed,
            onClick = onDecrement,
        )
    }
}

@Composable
private fun GateOpening() {
    Box(
        modifier = Modifier
            .width(52.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
            .background(Felt)
            .border(2.dp, Brass, RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp, bottomStart = 4.dp, bottomEnd = 4.dp)),
    )
}

@Composable
private fun HugeTapButton(
    label: String,
    contentDescription: String,
    enabled: Boolean,
    container: Color,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(width = 72.dp, height = 64.dp)
            .semantics { this.contentDescription = contentDescription },
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = PaperCream,
            disabledContainerColor = container.copy(alpha = 0.35f),
            disabledContentColor = PaperCream.copy(alpha = 0.6f),
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
    ) {
        Text(
            text = label,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RoundHistory(rounds: List<Int>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PaperCream.copy(alpha = 0.92f),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(R.string.history_title),
                color = WoodMid,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            if (rounds.isEmpty()) {
                Text(
                    text = stringResource(R.string.history_empty),
                    color = WoodMid,
                    fontSize = 14.sp,
                )
            } else {
                Text(
                    text = rounds.mapIndexed { index, score ->
                        stringResource(R.string.history_round, index + 1, score)
                    }.joinToString("   ·   "),
                    color = InkBrown,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun ActionBar(
    canUndo: Boolean,
    onUndo: () -> Unit,
    onNextRound: () -> Unit,
    onReset: () -> Unit,
) {
    Column {
        Button(
            onClick = onNextRound,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrassDeep,
                contentColor = InkBrown,
            ),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(
                text = stringResource(R.string.next_round).uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 1.sp,
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = onUndo,
                enabled = canUndo,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Brass),
                border = androidx.compose.foundation.BorderStroke(1.dp, Brass),
            ) {
                Text(
                    text = stringResource(R.string.undo),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
            }
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PaperCream),
                border = androidx.compose.foundation.BorderStroke(1.dp, PaperCream.copy(alpha = 0.5f)),
            ) {
                Text(
                    text = stringResource(R.string.reset),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
            }
        }
    }
}
