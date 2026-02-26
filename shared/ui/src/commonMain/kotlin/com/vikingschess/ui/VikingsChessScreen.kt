package com.vikingschess.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

    val bg = if (ui.isDarkMode) Color(0xFF111316) else Color(0xFFEFF4FA)
    val surface = if (ui.isDarkMode) Color(0x40FFFFFF) else Color(0x99FFFFFF)
    val textPrimary = if (ui.isDarkMode) Color(0xFFF2F4F8) else Color(0xFF121722)
    val textSecondary = if (ui.isDarkMode) Color(0xFFB6BCC8) else Color(0xFF3B465B)

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(20.dp),
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
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(surface)
                    .padding(10.dp),
            ) {
                for (row in 0 until state.board.size) {
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        for (col in 0 until state.board.size) {
                            val pos = Position(row, col)
                            val piece = state.board[pos]
                            val selected = state.selected == pos
                            val isCorner = state.board.isCorner(pos)
                            val cellBg = when {
                                isCorner -> if (ui.isDarkMode) Color(0xFF7E5D2B) else Color(0xFFE6C37A)
                                (row + col) % 2 == 0 -> if (ui.isDarkMode) Color(0xFF2B313A) else Color(0xFFDCE4F0)
                                else -> if (ui.isDarkMode) Color(0xFF20262E) else Color(0xFFCFD8E6)
                            }

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(cellBg)
                                    .border(
                                        width = if (selected) 2.dp else 1.dp,
                                        color = if (selected) Color(0xFF9AE8C5) else Color(0x33000000),
                                        shape = RoundedCornerShape(10.dp),
                                    )
                                    .clickable { viewModel.onCellTapped(pos) },
                                contentAlignment = Alignment.Center,
                            ) {
                                if (piece.type != PieceType.EMPTY) {
                                    val pieceColor = when (piece.type) {
                                        PieceType.ATTACKER -> Color(0xFFD64545)
                                        PieceType.DEFENDER -> Color(0xFF4B7CF0)
                                        PieceType.KING -> Color(0xFFFFD66E)
                                        PieceType.EMPTY -> Color.Transparent
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(if (piece.type == PieceType.KING) 29.dp else 25.dp)
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

            Text(
                text = "Rule set: 11x11 Hnefatafl · king escapes to corners · four-side king capture",
                color = textPrimary,
                fontSize = 13.sp,
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(if (isDarkMode) Color(0x2EFFFFFF) else Color(0xBBFFFFFF))
            .blur(0.2.dp)
            .padding(horizontal = 10.dp, vertical = 8.dp),
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
    val base = if (enabled) Color(0x44FFFFFF) else Color(0x22FFFFFF)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(base)
            .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(999.dp))
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            color = if (enabled) Color.White else Color(0x88FFFFFF),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

private fun statusText(turn: Player, winner: Winner?): String = when (winner) {
    Winner.ATTACKERS -> "Attackers win: king captured"
    Winner.DEFENDERS -> "Defenders win: king escaped"
    null -> "Turn: $turn"
}
