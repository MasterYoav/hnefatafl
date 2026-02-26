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
            val cells = MutableList(size) { MutableList(size) { Piece.Empty } }

            fun place(position: Position, piece: Piece) {
                cells[position.row][position.col] = piece
            }

            // Defenders + king (13 total = 12 defenders + king)
            place(Position(5, 5), Piece.King)
            val defenders = listOf(
                Position(5, 4), Position(5, 3), Position(5, 6), Position(5, 7),
                Position(4, 5), Position(3, 5), Position(6, 5), Position(7, 5),
                Position(4, 4), Position(4, 6), Position(6, 4), Position(6, 6),
            )
            defenders.forEach { place(it, Piece.Defender) }

            // Attackers (24 total)
            val attackers = listOf(
                Position(0, 3), Position(0, 4), Position(0, 5), Position(0, 6), Position(0, 7),
                Position(1, 5),

                Position(10, 3), Position(10, 4), Position(10, 5), Position(10, 6), Position(10, 7),
                Position(9, 5),

                Position(3, 0), Position(4, 0), Position(5, 0), Position(6, 0), Position(7, 0),
                Position(5, 1),

                Position(3, 10), Position(4, 10), Position(5, 10), Position(6, 10), Position(7, 10),
                Position(5, 9),
            )
            attackers.forEach { place(it, Piece.Attacker) }

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