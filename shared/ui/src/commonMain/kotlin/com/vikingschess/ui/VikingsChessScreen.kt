package com.vikingschess.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vikingschess.logic.PieceType
import com.vikingschess.logic.Player
import com.vikingschess.logic.Position
import com.vikingschess.logic.Winner

@Composable
fun VikingsChessApp(viewModel: BoardViewModel = remember { BoardViewModel() }) {
    val ui = viewModel.uiState
    val state = ui.game

    val bg = Color.Transparent
    val surface = if (ui.isDarkMode) Color(0x4D1E2530) else Color(0x66FFFFFF)
    val textPrimary = if (ui.isDarkMode) Color(0xFFF2F4F8) else Color(0xFF121722)
    val textSecondary = if (ui.isDarkMode) Color(0xFFB6BCC8) else Color(0xFF3B465B)

    val roundedFont = FontFamily.SansSerif

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            GlassToolbar(
                isDarkMode = ui.isDarkMode,
                canUndo = viewModel.canUndo(),
                onNewGame = viewModel::newGame,
                onUndo = viewModel::undo,
                onToggleTheme = viewModel::toggleTheme,
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
                val cellSize = ((boardSide - (outerPadding * 2) - (gap * (state.board.size - 1))) / state.board.size)
                    .coerceIn(18.dp, 56.dp)
                val pieceSize = (cellSize * 0.6f).coerceIn(12.dp, 34.dp)
                val kingPieceSize = (cellSize * 0.68f).coerceIn(14.dp, 38.dp)
                val hintSize = (cellSize * 0.22f).coerceIn(4.dp, 12.dp)
                val cornerRadius = (cellSize * 0.26f).coerceIn(5.dp, 14.dp)

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
                                    (row + col) % 2 == 0 -> if (ui.isDarkMode) Color(0xFF2B313A) else Color(0xFFDCE4F0)
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
                                                isMoveHint -> if (ui.isDarkMode) Color(0xFF7CEEC4) else Color(0xFF2C8F72)
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
                                            PieceType.ATTACKER -> Color(0xFFD64545)
                                            PieceType.DEFENDER -> Color(0xFF4B7CF0)
                                            PieceType.KING -> Color(0xFFFFD66E)
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
                                                        listOf(pieceColor.copy(alpha = 0.95f), pieceColor.copy(alpha = 0.65f)),
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

            Text(
                text = "Rule set: 11x11 Hnefatafl · Blue escorts king to corners · Red captures king on 4 sides",
                color = textPrimary,
                fontSize = 13.sp,
                fontFamily = roundedFont,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun GlassToolbar(
    isDarkMode: Boolean,
    canUndo: Boolean,
    onNewGame: () -> Unit,
    onUndo: () -> Unit,
    onToggleTheme: () -> Unit,
) {
    val barColor = if (isDarkMode) Color(0x6634404F) else Color(0x88EAF2FF)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(barColor)
            .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(28.dp))
            .blur(10.dp)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        GlassButton("New Game", enabled = true, onClick = onNewGame)
        GlassButton("Undo", enabled = canUndo, onClick = onUndo)
        GlassButton(if (isDarkMode) "Light" else "Dark", enabled = true, onClick = onToggleTheme)
    }
}

@Composable
private fun GlassButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val capsule = if (enabled) Color(0x55FFFFFF) else Color(0x33FFFFFF)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        capsule.copy(alpha = 0.75f),
                        capsule.copy(alpha = 0.35f),
                    ),
                ),
            )
            .border(1.dp, Color(0x99FFFFFF), RoundedCornerShape(999.dp))
            .blur(3.dp)
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 18.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            color = if (enabled) Color.White else Color(0x88FFFFFF),
            fontSize = 13.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun statusText(turn: Player, winner: Winner?): String = when (winner) {
    Winner.ATTACKERS -> "Red Team wins: king captured"
    Winner.DEFENDERS -> "Blue Team wins: king escaped"
    null -> "Turn: ${if (turn == Player.ATTACKER) "Red Team" else "Blue Team"}"
}
