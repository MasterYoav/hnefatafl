package com.vikingschess.ui

import androidx.compose.ui.graphics.Color

enum class BackgroundMode {
    DEFAULT,
    SOLID,
    IMAGE,
}

enum class PawnColorTarget {
    ATTACKER,
    DEFENDER,
    KING,
}

data class PawnColors(
    val attackerHex: String,
    val defenderHex: String,
    val kingHex: String,
)

data class BackgroundSettings(
    val mode: BackgroundMode,
    val solidHex: String,
    val imagePath: String?,
    val opacity: Float,
)

data class UiSettings(
    val pawnColors: PawnColors,
    val background: BackgroundSettings,
)

data class SettingsValidation(
    val attackerValid: Boolean,
    val defenderValid: Boolean,
    val kingValid: Boolean,
    val solidValid: Boolean,
) {
    val isValid: Boolean = attackerValid && defenderValid && kingValid && solidValid
}

private const val DEFAULT_ATTACKER = "#D64545"
private const val DEFAULT_DEFENDER = "#4B7CF0"
private const val DEFAULT_KING = "#FFD66E"
private const val DEFAULT_SOLID = "#0B1220"

fun defaultUiSettings(): UiSettings = UiSettings(
    pawnColors = PawnColors(
        attackerHex = DEFAULT_ATTACKER,
        defenderHex = DEFAULT_DEFENDER,
        kingHex = DEFAULT_KING,
    ),
    background = BackgroundSettings(
        mode = BackgroundMode.DEFAULT,
        solidHex = DEFAULT_SOLID,
        imagePath = null,
        opacity = 0.65f,
    ),
)

fun settingsValidation(settings: UiSettings): SettingsValidation {
    val pawn = settings.pawnColors
    val solidValid = settings.background.mode != BackgroundMode.SOLID || isValidHexColor(settings.background.solidHex)
    return SettingsValidation(
        attackerValid = isValidHexColor(pawn.attackerHex),
        defenderValid = isValidHexColor(pawn.defenderHex),
        kingValid = isValidHexColor(pawn.kingHex),
        solidValid = solidValid,
    )
}

fun parseHexColorOrNull(input: String): Color? {
    val trimmed = input.trim()
    val hex = if (trimmed.startsWith("#")) trimmed.drop(1) else trimmed
    if (hex.length != 6) return null
    val intColor = hex.toLongOrNull(16) ?: return null
    return Color(0xFF000000L or intColor)
}

fun isValidHexColor(input: String): Boolean = parseHexColorOrNull(input) != null

fun defaultPawnColors(): PawnColors = PawnColors(
    attackerHex = DEFAULT_ATTACKER,
    defenderHex = DEFAULT_DEFENDER,
    kingHex = DEFAULT_KING,
)
