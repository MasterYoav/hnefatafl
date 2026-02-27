package com.vikingschess.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectDragGestures
import com.vikingschess.logic.PieceType
import com.vikingschess.logic.Player
import com.vikingschess.logic.Position
import com.vikingschess.logic.Winner

@Composable
fun VikingsChessApp(
    viewModel: BoardViewModel = remember { BoardViewModel() },
    onPickImage: () -> String? = { null },
    onPickColor: (String) -> String? = { null },
    imagePainter: (String) -> Painter? = { null },
    onThemeChanged: (Boolean) -> Unit = {},
    onTransparencyModeChanged: (Boolean) -> Unit = {},
    onWindowClose: () -> Unit = {},
    onWindowMinimize: () -> Unit = {},
    onWindowToggleMaximize: () -> Unit = {},
    onWindowDrag: (Float, Float) -> Unit = { _, _ -> },
) {
    val ui = viewModel.uiState
    val state = ui.game
    var showRules by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    val settings = ui.settings
    val draft = ui.settingsDraft
    val transparentMode = settings.background.mode == BackgroundMode.TRANSPARENT
    val baseBg = when {
        transparentMode -> Color.Transparent
        ui.isDarkMode -> Color(0xFF0B1220)
        else -> Color(0xFFF4F7FB)
    }
    val surface = if (ui.isDarkMode) Color(0x4D1E2530) else Color(0x66FFFFFF)
    val textPrimary = if (ui.isDarkMode) Color(0xFFF2F4F8) else Color(0xFF121722)
    val textSecondary = if (ui.isDarkMode) Color(0xFFB6BCC8) else Color(0xFF3B465B)
    val validation = settingsValidation(draft)

    val roundedFont = FontFamily.SansSerif
    val backgroundPainter = remember(settings.background.imagePath, settings.background.mode) {
        if (settings.background.mode == BackgroundMode.IMAGE) {
            settings.background.imagePath?.let(imagePainter)
        } else {
            null
        }
    }
    val solidColor = parseHexColorOrNull(settings.background.solidHex)

    SideEffect {
        onThemeChanged(ui.isDarkMode)
        onTransparencyModeChanged(transparentMode)
    }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().background(baseBg)) {
            if (settings.background.mode == BackgroundMode.SOLID && solidColor != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(solidColor),
                )
            }
            if (settings.background.mode == BackgroundMode.IMAGE && backgroundPainter != null) {
                Image(
                    painter = backgroundPainter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            if (settings.background.mode == BackgroundMode.TRANSPARENT) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur((settings.background.blur * 28f).dp)
                        .background(Color(0xCC0B1220).copy(alpha = settings.background.opacity)),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                CustomTitleBar(
                    isDarkMode = ui.isDarkMode,
                    onClose = onWindowClose,
                    onMinimize = onWindowMinimize,
                    onToggleMaximize = onWindowToggleMaximize,
                    onDrag = onWindowDrag,
                )

                GlassToolbar(
                    isDarkMode = ui.isDarkMode,
                    canUndo = viewModel.canUndo(),
                    onNewGame = viewModel::newGame,
                    onUndo = viewModel::undo,
                    onRules = { showRules = true },
                    onSettings = {
                        viewModel.beginSettingsEdit()
                        showSettings = true
                    },
                )

                Text(
                    text = statusText(state.currentTurn, state.winner),
                    color = if (state.winner == null) textSecondary else Color(0xFF7CFFB2),
                    fontSize = 15.sp,
                    fontFamily = roundedFont,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "Red Team: ${ui.redTeamWins}   •   Blue Team: ${ui.blueTeamWins}",
                    color = textPrimary,
                    fontSize = 13.sp,
                    fontFamily = roundedFont,
                    fontWeight = FontWeight.Bold,
                )

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    val boardSide = minOf(maxWidth, maxHeight)
                    val outerPadding = (boardSide * 0.02f).coerceIn(6.dp, 12.dp)
                    val gap = (boardSide * 0.004f).coerceIn(1.dp, 3.dp)
                    val cellSize =
                        ((boardSide - (outerPadding * 2) - (gap * (state.board.size - 1))) / state.board.size)
                            .coerceIn(18.dp, 56.dp)
                    val pieceSize = (cellSize * 0.6f).coerceIn(12.dp, 34.dp)
                    val kingPieceSize = (cellSize * 0.68f).coerceIn(14.dp, 38.dp)
                    val hintSize = (cellSize * 0.22f).coerceIn(4.dp, 12.dp)
                    val cornerRadius = (cellSize * 0.26f).coerceIn(5.dp, 14.dp)

                    val pawnDefaults = defaultPawnColors()
                    val attackerColor = parseHexColorOrNull(settings.pawnColors.attackerHex)
                        ?: parseHexColorOrNull(pawnDefaults.attackerHex)
                        ?: Color(0xFFD64545)
                    val defenderColor = parseHexColorOrNull(settings.pawnColors.defenderHex)
                        ?: parseHexColorOrNull(pawnDefaults.defenderHex)
                        ?: Color(0xFF4B7CF0)
                    val kingColor = parseHexColorOrNull(settings.pawnColors.kingHex)
                        ?: parseHexColorOrNull(pawnDefaults.kingHex)
                        ?: Color(0xFFFFD66E)

                    Column(
                        verticalArrangement = Arrangement.spacedBy(gap),
                        modifier = Modifier
                            .size(boardSide)
                            .clip(RoundedCornerShape(20.dp))
                            .background(surface)
                            .padding(outerPadding),
                    ) {
                        for (row in 0 until state.board.size) {
                            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                                for (col in 0 until state.board.size) {
                                    val pos = Position(row, col)
                                    val piece = state.board[pos]
                                    val selected = state.selected == pos
                                    val isCorner = state.board.isCorner(pos)
                                    val isMoveHint = pos in ui.highlightedMoves
                                    val cellBg = when {
                                        isCorner -> if (ui.isDarkMode) Color(0xFF7E5D2B) else Color(0xFFE6C37A)
                                        (row + col) % 2 == 0 -> if (ui.isDarkMode) Color(0xFF2B313A) else Color(
                                            0xFFDCE4F0,
                                        )

                                        else -> if (ui.isDarkMode) Color(0xFF20262E) else Color(0xFFCFD8E6)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(cellSize)
                                            .clip(RoundedCornerShape(cornerRadius))
                                            .background(cellBg)
                                            .border(
                                                width = if (selected) 2.dp else 1.dp,
                                                color = when {
                                                    selected -> Color(0xFF9AE8C5)
                                                    isMoveHint -> if (ui.isDarkMode) Color(0xFF7CEEC4) else Color(
                                                        0xFF2C8F72,
                                                    )

                                                    else -> Color(0x33000000)
                                                },
                                                shape = RoundedCornerShape(cornerRadius),
                                            )
                                            .clickable { viewModel.onCellTapped(pos) },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (isMoveHint && piece.type == PieceType.EMPTY) {
                                            Box(
                                                modifier = Modifier
                                                    .size(hintSize)
                                                    .clip(CircleShape)
                                                    .background(if (ui.isDarkMode) Color(0xB37CEEC4) else Color(0x992C8F72)),
                                            )
                                        }

                                        if (piece.type != PieceType.EMPTY) {
                                            val pieceColor = when (piece.type) {
                                                PieceType.ATTACKER -> attackerColor
                                                PieceType.DEFENDER -> defenderColor
                                                PieceType.KING -> kingColor
                                                PieceType.EMPTY -> Color.Transparent
                                            }
                                            val pieceScale = animateFloatAsState(
                                                targetValue = if (selected) 1.08f else 1f,
                                                animationSpec = spring(dampingRatio = 0.62f, stiffness = 460f),
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .scale(pieceScale.value)
                                                    .size(if (piece.type == PieceType.KING) kingPieceSize else pieceSize)
                                                    .clip(CircleShape)
                                                    .background(
                                                        Brush.verticalGradient(
                                                            listOf(
                                                                pieceColor.copy(alpha = 0.95f),
                                                                pieceColor.copy(alpha = 0.65f),
                                                            ),
                                                        ),
                                                    )
                                                    .border(1.dp, Color(0x66FFFFFF), CircleShape),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (showRules) {
                    RulesDialog(
                        isDarkMode = ui.isDarkMode,
                        onClose = { showRules = false },
                    )
                }

                if (showSettings) {
                    SettingsDialog(
                        isDarkMode = ui.isDarkMode,
                        draft = draft,
                        validation = validation,
                        onPawnColorChange = viewModel::updatePawnColor,
                        onBackgroundModeChange = viewModel::updateBackgroundMode,
                        onSolidHexChange = viewModel::updateBackgroundSolidHex,
                        onOpacityChange = viewModel::updateBackgroundOpacity,
                        onBlurChange = viewModel::updateBackgroundBlur,
                        onPickImage = onPickImage,
                        onPickColor = onPickColor,
                        onImagePathChange = viewModel::updateBackgroundImagePath,
                        onToggleTheme = viewModel::toggleTheme,
                        isDarkModeState = ui.isDarkMode,
                        onClose = { showSettings = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomTitleBar(
    isDarkMode: Boolean,
    onClose: () -> Unit,
    onMinimize: () -> Unit,
    onToggleMaximize: () -> Unit,
    onDrag: (Float, Float) -> Unit,
) {
    val titleColor = if (isDarkMode) Color(0xFFEFF5FF) else Color(0xFF243246)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x, dragAmount.y)
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.size(13.dp).clip(CircleShape).background(Color(0xFFFF5F57)).clickable(onClick = onClose),
            )
            Box(
                modifier = Modifier.size(13.dp).clip(CircleShape).background(Color(0xFFFEBC2E)).clickable(onClick = onMinimize),
            )
            Box(
                modifier = Modifier.size(13.dp).clip(CircleShape).background(Color(0xFF28C840)).clickable(onClick = onToggleMaximize),
            )
        }
        Text(
            text = "Hnefatafl",
            color = titleColor,
            fontSize = 16.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
        )
        Box(modifier = Modifier.size(60.dp))
    }
}

@Composable
private fun GlassToolbar(
    isDarkMode: Boolean,
    canUndo: Boolean,
    onNewGame: () -> Unit,
    onUndo: () -> Unit,
    onRules: () -> Unit,
    onSettings: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
    ) {
        GlassButton("New Game", enabled = true, isDarkMode = isDarkMode, onClick = onNewGame)
        GlassButton("Undo", enabled = canUndo, isDarkMode = isDarkMode, onClick = onUndo)
        GlassButton("Settings", enabled = true, isDarkMode = isDarkMode, onClick = onSettings)
        GlassButton("Game Rules", enabled = true, isDarkMode = isDarkMode, onClick = onRules)
    }
}

@Composable
private fun GlassButton(
    label: String,
    enabled: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val outline = if (isDarkMode) Color(0xCCFFFFFF) else Color(0xFF9DB4D6)
    val textColor = if (enabled) {
        if (isDarkMode) Color(0xFFF4F7FF) else Color(0xFF1D2A3B)
    } else {
        if (isDarkMode) Color(0x77E7EEFF) else Color(0x88435975)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, outline, RoundedCornerShape(6.dp))
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 9.dp),
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun RulesDialog(
    isDarkMode: Boolean,
    onClose: () -> Unit,
) {
    val cardBg = if (isDarkMode) Color(0xEE182333) else Color(0xF7FFFFFF)
    val textColor = if (isDarkMode) Color(0xFFF0F5FF) else Color(0xFF1C2A3C)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66000000))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(16.dp))
                .padding(18.dp)
                .clickable(enabled = false) {},
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Game Rules", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("• Blue Team: help the king reach any corner", color = textColor, fontSize = 14.sp)
            Text("• Red Team: capture the king by surrounding all 4 sides", color = textColor, fontSize = 14.sp)
            Text("• All pieces move orthogonally any distance", color = textColor, fontSize = 14.sp)
            Text("• No jumping over pieces", color = textColor, fontSize = 14.sp)
            Text("• Only king may enter corner cells", color = textColor, fontSize = 14.sp)
            Text("• Pawn capture: sandwich enemy between two allies or ally + board edge", color = textColor, fontSize = 14.sp)
            GlassButton("Close", enabled = true, isDarkMode = isDarkMode, onClick = onClose)
        }
    }
}

@Composable
private fun SettingsDialog(
    isDarkMode: Boolean,
    draft: UiSettings,
    validation: SettingsValidation,
    onPawnColorChange: (PawnColorTarget, String) -> Unit,
    onBackgroundModeChange: (BackgroundMode) -> Unit,
    onSolidHexChange: (String) -> Unit,
    onOpacityChange: (Float) -> Unit,
    onBlurChange: (Float) -> Unit,
    onPickImage: () -> String?,
    onPickColor: (String) -> String?,
    onImagePathChange: (String?) -> Unit,
    onToggleTheme: () -> Unit,
    isDarkModeState: Boolean,
    onClose: () -> Unit,
) {
    val cardBg = if (isDarkMode) Color(0xEE182333) else Color(0xF7FFFFFF)
    val textColor = if (isDarkMode) Color(0xFFF0F5FF) else Color(0xFF1C2A3C)
    val accent = if (isDarkMode) Color(0xFF9AE8C5) else Color(0xFF2C8F72)
    val error = if (isDarkMode) Color(0xFFFFB6B6) else Color(0xFFB00020)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66000000))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(16.dp))
                .padding(18.dp)
                .clickable(enabled = false) {},
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Settings", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                GlassButton("✕", enabled = true, isDarkMode = isDarkMode, onClick = onClose)
            }

            Text("Pawn colors", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            ColorInputRow(
                label = "Red team",
                value = draft.pawnColors.attackerHex,
                isValid = validation.attackerValid,
                textColor = textColor,
                errorColor = error,
                previewFallback = Color(0xFFD64545),
                onValueChange = { onPawnColorChange(PawnColorTarget.ATTACKER, it) },
                onPickColor = onPickColor,
            )
            ColorInputRow(
                label = "Blue team",
                value = draft.pawnColors.defenderHex,
                isValid = validation.defenderValid,
                textColor = textColor,
                errorColor = error,
                previewFallback = Color(0xFF4B7CF0),
                onValueChange = { onPawnColorChange(PawnColorTarget.DEFENDER, it) },
                onPickColor = onPickColor,
            )
            ColorInputRow(
                label = "King",
                value = draft.pawnColors.kingHex,
                isValid = validation.kingValid,
                textColor = textColor,
                errorColor = error,
                previewFallback = Color(0xFFFFD66E),
                onValueChange = { onPawnColorChange(PawnColorTarget.KING, it) },
                onPickColor = onPickColor,
            )

            Text("Background", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModeChip(
                    label = "Default",
                    selected = draft.background.mode == BackgroundMode.DEFAULT,
                    accent = accent,
                    textColor = textColor,
                    onClick = { onBackgroundModeChange(BackgroundMode.DEFAULT) },
                )
                ModeChip(
                    label = "Solid",
                    selected = draft.background.mode == BackgroundMode.SOLID,
                    accent = accent,
                    textColor = textColor,
                    onClick = { onBackgroundModeChange(BackgroundMode.SOLID) },
                )
                ModeChip(
                    label = "Image",
                    selected = draft.background.mode == BackgroundMode.IMAGE,
                    accent = accent,
                    textColor = textColor,
                    onClick = { onBackgroundModeChange(BackgroundMode.IMAGE) },
                )
                ModeChip(
                    label = "Transparent",
                    selected = draft.background.mode == BackgroundMode.TRANSPARENT,
                    accent = accent,
                    textColor = textColor,
                    onClick = { onBackgroundModeChange(BackgroundMode.TRANSPARENT) },
                )
            }

            if (draft.background.mode == BackgroundMode.DEFAULT) {
                GlassButton(
                    label = if (isDarkModeState) "☀️" else "🌙",
                    enabled = true,
                    isDarkMode = isDarkMode,
                    onClick = onToggleTheme,
                )
            }

            if (draft.background.mode == BackgroundMode.SOLID) {
                ColorInputRow(
                    label = "Solid hex",
                    value = draft.background.solidHex,
                    isValid = validation.solidValid,
                    textColor = textColor,
                    errorColor = error,
                    previewFallback = Color(0xFF0B1220),
                    onValueChange = onSolidHexChange,
                    onPickColor = onPickColor,
                )
            }

            if (draft.background.mode == BackgroundMode.IMAGE) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = draft.background.imagePath ?: "No image selected",
                        color = textColor.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                    )
                    GlassButton("Choose image", enabled = true, isDarkMode = isDarkMode) {
                        val picked = onPickImage()
                        if (picked != null) {
                            onImagePathChange(picked)
                        }
                    }
                }
            }

            if (draft.background.mode == BackgroundMode.TRANSPARENT) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Blur %", color = textColor, fontSize = 13.sp)
                        TextField(
                            value = ((draft.background.blur * 100).toInt()).toString(),
                            onValueChange = { raw ->
                                raw.toIntOrNull()?.let { n -> onBlurChange((n.coerceIn(0, 100) / 100f)) }
                            },
                            singleLine = true,
                            modifier = Modifier.width(90.dp),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                cursorColor = textColor,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                            ),
                        )
                        Text("(default 50)", color = textColor.copy(alpha = 0.65f), fontSize = 11.sp)
                    }

                    Text("Background opacity", color = textColor, fontSize = 13.sp)
                    Slider(
                        value = draft.background.opacity,
                        onValueChange = onOpacityChange,
                        valueRange = 0f..1f,
                    )
                    Text(
                        text = "${(draft.background.opacity * 100).toInt()}%",
                        color = textColor.copy(alpha = 0.75f),
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorInputRow(
    label: String,
    value: String,
    isValid: Boolean,
    textColor: Color,
    errorColor: Color,
    previewFallback: Color,
    onValueChange: (String) -> Unit,
    onPickColor: (String) -> String?,
) {
    val previewColor = parseHexColorOrNull(value) ?: previewFallback
    val fieldColors = TextFieldDefaults.colors(
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        cursorColor = textColor,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedIndicatorColor = if (isValid) textColor.copy(alpha = 0.6f) else errorColor,
        unfocusedIndicatorColor = if (isValid) textColor.copy(alpha = 0.3f) else errorColor,
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(label, color = textColor, fontSize = 12.sp)
            TextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                modifier = Modifier.width(140.dp),
                colors = fieldColors,
            )
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(previewColor)
                    .border(1.dp, Color(0x66000000), CircleShape)
                    .clickable {
                        val picked = onPickColor(value)
                        if (picked != null) onValueChange(picked)
                    },
            )
        }
        if (!isValid) {
            Text("Invalid hex", color = errorColor, fontSize = 11.sp)
        }
    }
}

@Composable
private fun ModeChip(
    label: String,
    selected: Boolean,
    accent: Color,
    textColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) accent.copy(alpha = 0.25f) else Color.Transparent)
            .border(1.dp, if (selected) accent else textColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(label, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun statusText(turn: Player, winner: Winner?): String = when (winner) {
    Winner.ATTACKERS -> "Red Team wins: king captured"
    Winner.DEFENDERS -> "Blue Team wins: king escaped"
    null -> "Turn: ${if (turn == Player.ATTACKER) "Red Team" else "Blue Team"}"
}
