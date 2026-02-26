package com.vikingschess.logic

class GameEngine(
    private var gameState: GameState = GameState(),
) {
    fun state(): GameState = gameState

    fun select(position: Position) {
        val piece = gameState.board[position]
        gameState = if (piece.owner == gameState.currentTurn) {
            gameState.copy(selected = position)
        } else {
            gameState.copy(selected = null)
        }
    }

    fun moveSelected(to: Position): Boolean {
        val from = gameState.selected ?: return false
        if (!gameState.board.isInside(to)) return false
        if (!isStraightLine(from, to)) return false
        if (gameState.board[to].type != PieceType.EMPTY) return false

        gameState = gameState.copy(
            board = gameState.board.move(from, to),
            currentTurn = gameState.currentTurn.opposite(),
            selected = null,
        )
        return true
    }

    private fun isStraightLine(from: Position, to: Position): Boolean =
        from.row == to.row || from.col == to.col
}
