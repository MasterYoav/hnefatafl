package com.vikingschess.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.vikingschess.logic.GameEngine
import com.vikingschess.logic.GameState
import com.vikingschess.logic.Position
import com.vikingschess.logic.Winner

data class VikingsChessUiState(
    val game: GameState,
    val isDarkMode: Boolean = true,
    val highlightedMoves: Set<Position> = emptySet(),
    val redTeamWins: Int = 0,
    val blueTeamWins: Int = 0,
    val settings: UiSettings = defaultUiSettings(),
    val settingsDraft: UiSettings = defaultUiSettings(),
)

class BoardViewModel(
    private val engine: GameEngine = GameEngine(),
) {
    var uiState: VikingsChessUiState by mutableStateOf(
        VikingsChessUiState(
            game = engine.state(),
            highlightedMoves = emptySet(),
        ),
    )
        private set

    fun onCellTapped(position: Position) {
        if (uiState.game.winner != null) return

        if (uiState.game.selected == null) {
            engine.select(position)
        } else {
            val moved = engine.moveSelected(position)
            if (!moved) {
                engine.select(position)
            }
        }
        sync()
    }

    fun newGame() {
        engine.newGame()
        sync()
    }

    fun undo() {
        engine.undo()
        sync()
    }

    fun canUndo(): Boolean = engine.canUndo()

    fun toggleTheme() {
        uiState = uiState.copy(isDarkMode = !uiState.isDarkMode)
    }

    fun beginSettingsEdit() {
        uiState = uiState.copy(settingsDraft = uiState.settings)
    }

    fun cancelSettingsEdit() {
        uiState = uiState.copy(settingsDraft = uiState.settings)
    }

    fun updatePawnColor(target: PawnColorTarget, value: String) {
        val pawn = uiState.settingsDraft.pawnColors
        val updated = when (target) {
            PawnColorTarget.ATTACKER -> pawn.copy(attackerHex = value)
            PawnColorTarget.DEFENDER -> pawn.copy(defenderHex = value)
            PawnColorTarget.KING -> pawn.copy(kingHex = value)
        }
        val nextDraft = uiState.settingsDraft.copy(pawnColors = updated)
        uiState = uiState.copy(settingsDraft = nextDraft)
        commitDraftIfValid()
    }

    fun updateBackgroundMode(mode: BackgroundMode) {
        val nextDraft = uiState.settingsDraft.copy(
            background = uiState.settingsDraft.background.copy(mode = mode),
        )
        uiState = uiState.copy(settingsDraft = nextDraft)
        commitDraftIfValid()
    }

    fun updateBackgroundSolidHex(value: String) {
        val nextDraft = uiState.settingsDraft.copy(
            background = uiState.settingsDraft.background.copy(solidHex = value),
        )
        uiState = uiState.copy(settingsDraft = nextDraft)
        commitDraftIfValid()
    }

    fun updateBackgroundOpacity(value: Float) {
        val nextDraft = uiState.settingsDraft.copy(
            background = uiState.settingsDraft.background.copy(opacity = value.coerceIn(0.1f, 1f)),
        )
        uiState = uiState.copy(settingsDraft = nextDraft)
        commitDraftIfValid()
    }

    fun updateBackgroundImagePath(path: String?) {
        val nextDraft = uiState.settingsDraft.copy(
            background = uiState.settingsDraft.background.copy(imagePath = path),
        )
        uiState = uiState.copy(settingsDraft = nextDraft)
        commitDraftIfValid()
    }

    fun applySettings(): Boolean {
        val draft = uiState.settingsDraft
        if (!settingsValidation(draft).isValid) return false
        uiState = uiState.copy(settings = draft, settingsDraft = draft)
        return true
    }

    fun resetSettings() {
        val defaults = defaultUiSettings()
        uiState = uiState.copy(settings = defaults, settingsDraft = defaults)
    }

    private fun commitDraftIfValid() {
        val draft = uiState.settingsDraft
        if (settingsValidation(draft).isValid) {
            uiState = uiState.copy(settings = draft)
        }
    }

    private fun sync() {
        val previousWinner = uiState.game.winner
        val game = engine.state()
        val highlightedMoves = game.selected
            ?.let(engine::legalMoves)
            ?.toSet()
            .orEmpty()

        val redWinsIncrement = if (previousWinner == null && game.winner == Winner.ATTACKERS) 1 else 0
        val blueWinsIncrement = if (previousWinner == null && game.winner == Winner.DEFENDERS) 1 else 0

        uiState = uiState.copy(
            game = game,
            highlightedMoves = highlightedMoves,
            redTeamWins = uiState.redTeamWins + redWinsIncrement,
            blueTeamWins = uiState.blueTeamWins + blueWinsIncrement,
        )
    }
}
