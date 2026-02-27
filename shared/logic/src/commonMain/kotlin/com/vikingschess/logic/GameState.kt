package com.vikingschess.logic

enum class Player {
    ATTACKER,
    DEFENDER;

    fun opposite(): Player = if (this == ATTACKER) DEFENDER else ATTACKER
}

enum class PieceType {
    ATTACKER,
    DEFENDER,
    KING,
    EMPTY
}

data class Piece(
    val type: PieceType,
    val owner: Player? = null,
) {
    val isEmpty: Boolean get() = type == PieceType.EMPTY

    companion object {
        val Empty = Piece(PieceType.EMPTY, null)
        val Attacker = Piece(PieceType.ATTACKER, Player.ATTACKER)
        val Defender = Piece(PieceType.DEFENDER, Player.DEFENDER)
        val King = Piece(PieceType.KING, Player.DEFENDER)
    }
}

data class Position(val row: Int, val col: Int)

enum class Winner {
    ATTACKERS,
    DEFENDERS
}

data class Board(
    val size: Int = 11,
    val cells: List<List<Piece>>,
) {
    operator fun get(position: Position): Piece = cells[position.row][position.col]

    fun isInside(position: Position): Boolean =
        position.row in 0 until size && position.col in 0 until size

    fun isCorner(position: Position): Boolean {
        val max = size - 1
        return (position.row == 0 || position.row == max) &&
            (position.col == 0 || position.col == max)
    }

    fun set(position: Position, piece: Piece): Board {
        val mutable = cells.map { it.toMutableList() }.toMutableList()
        mutable[position.row][position.col] = piece
        return copy(cells = mutable)
    }

    fun move(from: Position, to: Position): Board {
        val mutable = cells.map { it.toMutableList() }.toMutableList()
        mutable[to.row][to.col] = mutable[from.row][from.col]
        mutable[from.row][from.col] = Piece.Empty
        return copy(cells = mutable)
    }

    companion object {
        fun initial(size: Int = 11): Board {
            require(size == 11) { "Current ruleset supports 11x11 board." }

            // Kept in lockstep with the original Java VikingsChess startboard matrix:
            // 0 = empty, 1 = attacker, 2 = defender, 3 = king
            val startboard = listOf(
                listOf(0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(1, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1),
                listOf(1, 0, 0, 0, 2, 2, 2, 0, 0, 0, 1),
                listOf(1, 1, 0, 2, 2, 3, 2, 2, 0, 1, 1),
                listOf(1, 0, 0, 0, 2, 2, 2, 0, 0, 0, 1),
                listOf(1, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1),
                listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0),
            )

            val cells = startboard.map { row ->
                row.map { value ->
                    when (value) {
                        1 -> Piece.Attacker
                        2 -> Piece.Defender
                        3 -> Piece.King
                        else -> Piece.Empty
                    }
                }
            }

            return Board(size, cells)
        }
    }
}

data class GameState(
    val board: Board = Board.initial(),
    val currentTurn: Player = Player.ATTACKER,
    val selected: Position? = null,
    val winner: Winner? = null,
)