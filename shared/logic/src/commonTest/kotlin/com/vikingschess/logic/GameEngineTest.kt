package com.vikingschess.logic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameEngineTest {
    @Test
    fun `initial board has expected piece counts and key positions`() {
        val board = GameEngine().state().board

        var attackers = 0
        var defenders = 0
        var kings = 0

        for (row in 0 until board.size) {
            for (col in 0 until board.size) {
                when (board[Position(row, col)].type) {
                    PieceType.ATTACKER -> attackers++
                    PieceType.DEFENDER -> defenders++
                    PieceType.KING -> kings++
                    PieceType.EMPTY -> Unit
                }
            }
        }

        assertEquals(24, attackers)
        assertEquals(12, defenders)
        assertEquals(1, kings)
        assertEquals(PieceType.KING, board[Position(5, 5)].type)
        assertEquals(PieceType.ATTACKER, board[Position(0, 5)].type)
        assertEquals(PieceType.ATTACKER, board[Position(5, 0)].type)
    }

    @Test
    fun `non-king cannot move onto corner`() {
        val board = emptyBoardWith(
            attackers = listOf(Position(0, 2)),
            defenders = emptyList(),
            king = Position(5, 5),
        )
        val engine = GameEngine(GameState(board = board, currentTurn = Player.ATTACKER))

        engine.select(Position(0, 2))
        val moved = engine.moveSelected(Position(0, 0))

        assertFalse(moved)
        assertEquals(PieceType.ATTACKER, engine.state().board[Position(0, 2)].type)
    }

    @Test
    fun `cannot jump over pieces`() {
        val board = emptyBoardWith(
            attackers = listOf(Position(0, 5), Position(2, 5)),
            defenders = emptyList(),
            king = Position(5, 5),
        )
        val engine = GameEngine(GameState(board = board, currentTurn = Player.ATTACKER))

        engine.select(Position(0, 5))
        val moved = engine.moveSelected(Position(3, 5))

        assertFalse(moved)
    }

    @Test
    fun `sandwich capture removes enemy pawn`() {
        val board = emptyBoardWith(
            attackers = listOf(Position(3, 5), Position(3, 8)),
            defenders = listOf(Position(3, 6)),
            king = Position(5, 5),
        )
        val engine = GameEngine(GameState(board = board, currentTurn = Player.ATTACKER))

        engine.select(Position(3, 8))
        val moved = engine.moveSelected(Position(3, 7))

        assertTrue(moved)
        assertEquals(PieceType.EMPTY, engine.state().board[Position(3, 6)].type)
    }

    @Test
    fun `king on corner means defenders win`() {
        val board = emptyBoardWith(
            attackers = listOf(Position(0, 5), Position(10, 5), Position(5, 0), Position(5, 10)),
            defenders = emptyList(),
            king = Position(0, 1),
        )
        val engine = GameEngine(GameState(board = board, currentTurn = Player.DEFENDER))

        engine.select(Position(0, 1))
        val moved = engine.moveSelected(Position(0, 0))

        assertTrue(moved)
        assertEquals(Winner.DEFENDERS, engine.state().winner)
    }

    @Test
    fun `king surrounded on all four sides means attackers win`() {
        val board = emptyBoardWith(
            attackers = listOf(Position(4, 5), Position(6, 5), Position(5, 4), Position(5, 7)),
            defenders = emptyList(),
            king = Position(5, 5),
        )
        val engine = GameEngine(GameState(board = board, currentTurn = Player.ATTACKER))

        engine.select(Position(5, 7))
        val moved = engine.moveSelected(Position(5, 6))

        assertTrue(moved)
        assertEquals(Winner.ATTACKERS, engine.state().winner)
    }

    @Test
    fun `undo restores previous position and turn`() {
        val engine = GameEngine()
        val before = engine.state()

        engine.select(Position(0, 3))
        assertTrue(engine.moveSelected(Position(2, 3)))
        assertTrue(engine.undo())

        assertEquals(before.board, engine.state().board)
        assertEquals(before.currentTurn, engine.state().currentTurn)
        assertNull(engine.state().winner)
    }
}

private fun emptyBoardWith(
    attackers: List<Position>,
    defenders: List<Position>,
    king: Position,
): Board {
    val size = 11
    val cells = MutableList(size) { MutableList(size) { Piece.Empty } }
    attackers.forEach { cells[it.row][it.col] = Piece.Attacker }
    defenders.forEach { cells[it.row][it.col] = Piece.Defender }
    cells[king.row][king.col] = Piece.King
    val board = Board(size, cells)
    assertNotNull(board[king])
    return board
}
