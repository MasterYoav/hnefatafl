package com.vikingschess.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.vikingschess.logic.GameEngine
import com.vikingschess.logic.GameState
import com.vikingschess.logic.Position

class BoardViewModel(
    private val engine: GameEngine = GameEngine(),
) {
    var uiState: GameState by mutableStateOf(engine.state())
        private set

    fun onCellTapped(position: Position) {
        if (uiState.selected == null) {
            engine.select(position)
        } else {
            engine.moveSelected(position)
        }
        uiState = engine.state()
    }
}
