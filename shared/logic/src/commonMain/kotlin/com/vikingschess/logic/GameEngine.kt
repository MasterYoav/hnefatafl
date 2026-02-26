package com.vikingschess.logic

class GameEngine(
    private var gameState: GameState = GameState(),
) {
    private val history = ArrayDeque<GameState>()

    fun state(): GameState = gameState

    fun newGame() {
        history.clear()
        gameState = GameState()
    }

    fun canUndo(): Boolean = history.isNotEmpty() && gameState.winner == null

    fun undo(): Boolean {
        if (!canUndo()) return false
        gameState = history.removeLast()
        return true
    }

    fun legalMoves(from: Position): List<Position> {
        if (!gameState.board.isInside(from)) return emptyList()
        val piece = gameState.board[from]
        if (piece.owner != gameState.currentTurn) return emptyList()
        if (gameState.winner != null) return emptyList()

        return buildList {
            val directions = listOf(
                Position(1, 0), Position(-1, 0), Position(0, 1), Position(0, -1),
            )
            for (direction in directions) {
                var row = from.row + direction.row
                var col = from.col + direction.col
                while (gameState.board.isInside(Position(row, col))) {
                    val to = Position(row, col)
                    if (!gameState.board[to].isEmpty) break
                    if (gameState.board.isCorner(to) && piece.type != PieceType.KING) break
                    add(to)
                    row += direction.row
                    col += direction.col
                }
            }
        }
    }

    fun select(position: Position) {
        if (gameState.winner != null) {
            gameState = gameState.copy(selected = null)
            return
        }

        val piece = gameState.board[position]
        gameState = if (piece.owner == gameState.currentTurn) {
            gameState.copy(selected = position)
        } else {
            gameState.copy(selected = null)
        }
    }

    fun moveSelected(to: Position): Boolean {
        val from = gameState.selected ?: return false
        if (gameState.winner != null) return false
        if (to !in legalMoves(from)) return false

        history.addLast(gameState)

        var nextBoard = gameState.board.move(from, to)
        nextBoard = applyCaptures(nextBoard, to)
        val winner = determineWinner(nextBoard)

        gameState = gameState.copy(
            board = nextBoard,
            currentTurn = if (winner == null) gameState.currentTurn.opposite() else gameState.currentTurn,
            selected = null,
            winner = winner,
        )

        return true
    }

    private fun applyCaptures(board: Board, movedTo: Position): Board {
        val movingPiece = board[movedTo]
        val enemy = movingPiece.owner?.opposite() ?: return board
        var updated = board

        val directions = listOf(
            Position(1, 0), Position(-1, 0), Position(0, 1), Position(0, -1),
        )

        for (direction in directions) {
            val adjacent = Position(movedTo.row + direction.row, movedTo.col + direction.col)
            val beyond = Position(movedTo.row + (2 * direction.row), movedTo.col + (2 * direction.col))

            if (!updated.isInside(adjacent) || !updated.isInside(beyond)) continue

            val adjacentPiece = updated[adjacent]
            val beyondPiece = updated[beyond]

            if (adjacentPiece.type == PieceType.KING) continue
            if (adjacentPiece.owner != enemy) continue
            if (beyondPiece.owner == movingPiece.owner) {
                updated = updated.set(adjacent, Piece.Empty)
            }
        }

        return updated
    }

    private fun determineWinner(board: Board): Winner? {
        val king = findKing(board) ?: return Winner.ATTACKERS

        if (board.isCorner(king)) {
            return Winner.DEFENDERS
        }

        val kingCaptured = isKingSurroundedByAttackers(board, king)
        if (kingCaptured) return Winner.ATTACKERS

        return null
    }

    private fun findKing(board: Board): Position? {
        for (row in 0 until board.size) {
            for (col in 0 until board.size) {
                val pos = Position(row, col)
                if (board[pos].type == PieceType.KING) return pos
            }
        }
        return null
    }

    private fun isKingSurroundedByAttackers(board: Board, king: Position): Boolean {
        val neighbors = listOf(
            Position(king.row + 1, king.col),
            Position(king.row - 1, king.col),
            Position(king.row, king.col + 1),
            Position(king.row, king.col - 1),
        )
        return neighbors.all { pos ->
            board.isInside(pos) && board[pos].type == PieceType.ATTACKER
        }
    }
}
