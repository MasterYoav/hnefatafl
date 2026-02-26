package com.vikingschess.ui

import com.vikingschess.logic.Player
import com.vikingschess.logic.Position
import kotlin.test.Test
import kotlin.test.assertEquals

class BoardViewModelTest {
    @Test
    fun `tap select then tap destination switches turn`() {
        val vm = BoardViewModel()

        vm.onCellTapped(Position(0, 5))
        vm.onCellTapped(Position(1, 5))

        assertEquals(Player.DEFENDER, vm.uiState.currentTurn)
    }
}
