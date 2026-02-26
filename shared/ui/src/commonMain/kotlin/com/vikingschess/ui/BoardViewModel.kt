package com.vikingschess.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.vikingschess.logic.GameEngine
import com.vikingschess.logic.GameState
import com.vikingschess.logic.Position

data class VikingsChessUiState(
    val game: GameState,
    val isDarkMode: Boolean = true,
)

class BoardViewModel(
    private val engine: GameEngine = GameEngine(),
) {
    var uiState: VikingsChessUiState by mutableStateOf(VikingsChessUiState(engine.state()))
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

    private fun sync() {
        uiState = uiState.copy(game = engine.state())
    }
}
