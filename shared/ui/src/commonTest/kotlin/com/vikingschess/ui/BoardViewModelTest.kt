package com.vikingschess.ui

import com.vikingschess.logic.Player
import com.vikingschess.logic.Position
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BoardViewModelTest {
    @Test
    fun `tap select then tap destination switches turn`() {
        val vm = BoardViewModel()

        vm.onCellTapped(Position(0, 3))
        vm.onCellTapped(Position(2, 3))

        assertEquals(Player.DEFENDER, vm.uiState.game.currentTurn)
    }

    @Test
    fun `undo via view model restores attackers turn`() {
        val vm = BoardViewModel()

        vm.onCellTapped(Position(0, 3))
        vm.onCellTapped(Position(2, 3))
        vm.undo()

        assertEquals(Player.ATTACKER, vm.uiState.game.currentTurn)
    }

    @Test
    fun `toggle theme flips dark mode flag`() {
        val vm = BoardViewModel()
        val start = vm.uiState.isDarkMode

        vm.toggleTheme()

        assertTrue(vm.uiState.isDarkMode != start)
    }
}
