package com.vikingschess.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vikingschess.logic.PieceType
import com.vikingschess.logic.Position

@Composable
fun VikingsChessApp(viewModel: BoardViewModel = remember { BoardViewModel() }) {
    val state = viewModel.uiState

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Vikings Chess v0")
            Text("Turn: ${state.currentTurn}")

            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                for (row in 0 until state.board.size) {
                    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                        for (col in 0 until state.board.size) {
                            val pos = Position(row, col)
                            val piece = state.board[pos]
                            val selected = state.selected == pos
                            val bg = when (piece.type) {
                                PieceType.ATTACKER -> Color(0xFFB71C1C)
                                PieceType.DEFENDER -> Color(0xFF1A237E)
                                PieceType.KING -> Color(0xFFF9A825)
                                PieceType.EMPTY -> Color(0xFFEFEBE9)
                            }
                            val border = if (selected) Color(0xFF00C853) else Color.DarkGray

                            Text(
                                text = when (piece.type) {
                                    PieceType.ATTACKER -> "A"
                                    PieceType.DEFENDER -> "D"
                                    PieceType.KING -> "K"
                                    PieceType.EMPTY -> " "
                                },
                                modifier = Modifier
                                    .size(28.dp)
                                    .border(1.dp, border)
                                    .background(bg)
                                    .clickable { viewModel.onCellTapped(pos) }
                                    .padding(4.dp),
                                color = Color.White,
                            )
                        }
                    }
                }
            }

            Text("Tap own piece to select, tap destination to move in straight line.")
        }
    }
}
