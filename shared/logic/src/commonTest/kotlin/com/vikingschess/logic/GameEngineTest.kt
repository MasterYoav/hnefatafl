package com.vikingschess.logic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameEngineTest {
    @Test
    fun `selecting own piece marks it as selected`() {
        val engine = GameEngine()
        val attackerPosition = Position(0, 5)

        engine.select(attackerPosition)

        assertEquals(attackerPosition, engine.state().selected)
    }

    @Test
    fun `moving selected piece switches turn`() {
        val engine = GameEngine()
        val from = Position(0, 5)
        val to = Position(1, 5)
        engine.select(from)

        val moved = engine.moveSelected(to)

        assertTrue(moved)
        assertEquals(Player.DEFENDER, engine.state().currentTurn)
        assertEquals(PieceType.ATTACKER, engine.state().board[to].type)
        assertEquals(PieceType.EMPTY, engine.state().board[from].type)
    }

    @Test
    fun `cannot move diagonally`() {
        val engine = GameEngine()
        engine.select(Position(0, 5))

        val moved = engine.moveSelected(Position(1, 6))

        assertFalse(moved)
    }
}

class GameServiceIntegrationTest {
    @Test
    fun `select then move performs boundary transition`() {
        val engine = GameEngine()

        engine.select(Position(0, 5))
        val result = engine.moveSelected(Position(2, 5))

        assertTrue(result)
        assertEquals(Player.DEFENDER, engine.state().currentTurn)
    }
}
