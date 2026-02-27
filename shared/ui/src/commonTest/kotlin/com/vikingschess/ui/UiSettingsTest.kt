package com.vikingschess.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UiSettingsTest {
    @Test
    fun `parse hex accepts 6 digit rgb`() {
        assertNotNull(parseHexColorOrNull("#FF00AA"))
        assertNotNull(parseHexColorOrNull("ff00aa"))
    }

    @Test
    fun `parse hex rejects invalid strings`() {
        assertNull(parseHexColorOrNull("#FFF"))
        assertNull(parseHexColorOrNull("GGGGGG"))
    }

    @Test
    fun `apply settings updates pawn colors`() {
        val vm = BoardViewModel()
        vm.beginSettingsEdit()
        vm.updatePawnColor(PawnColorTarget.ATTACKER, "#112233")

        assertTrue(vm.applySettings())
        assertEquals("#112233", vm.uiState.settings.pawnColors.attackerHex)
    }

    @Test
    fun `invalid hex blocks apply`() {
        val vm = BoardViewModel()
        val original = vm.uiState.settings
        vm.beginSettingsEdit()
        vm.updatePawnColor(PawnColorTarget.DEFENDER, "GGGGGG")

        assertFalse(vm.applySettings())
        assertEquals(original, vm.uiState.settings)
    }

    @Test
    fun `reset restores defaults`() {
        val vm = BoardViewModel()
        vm.beginSettingsEdit()
        vm.updateBackgroundMode(BackgroundMode.SOLID)
        vm.updateBackgroundSolidHex("#112233")
        vm.applySettings()

        vm.resetSettings()

        assertEquals(defaultUiSettings(), vm.uiState.settings)
    }
}
