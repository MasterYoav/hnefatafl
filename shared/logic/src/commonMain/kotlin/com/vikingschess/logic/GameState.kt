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
    companion object {
        val Empty = Piece(PieceType.EMPTY, null)
    }
}

data class Position(val row: Int, val col: Int)

data class Board(
    val size: Int = 11,
    val cells: List<List<Piece>>,
) {
    operator fun get(position: Position): Piece = cells[position.row][position.col]

    fun isInside(position: Position): Boolean =
        position.row in 0 until size && position.col in 0 until size

    fun move(from: Position, to: Position): Board {
        val mutable = cells.map { it.toMutableList() }.toMutableList()
        mutable[to.row][to.col] = mutable[from.row][from.col]
        mutable[from.row][from.col] = Piece.Empty
        return copy(cells = mutable)
    }

    companion object {
        fun initial(size: Int = 11): Board {
            val cells = MutableList(size) { MutableList(size) { Piece.Empty } }

            val center = size / 2
            cells[center][center] = Piece(PieceType.KING, Player.DEFENDER)
            cells[center - 1][center] = Piece(PieceType.DEFENDER, Player.DEFENDER)
            cells[center + 1][center] = Piece(PieceType.DEFENDER, Player.DEFENDER)
            cells[center][center - 1] = Piece(PieceType.DEFENDER, Player.DEFENDER)
            cells[center][center + 1] = Piece(PieceType.DEFENDER, Player.DEFENDER)

            cells[0][center] = Piece(PieceType.ATTACKER, Player.ATTACKER)
            cells[size - 1][center] = Piece(PieceType.ATTACKER, Player.ATTACKER)
            cells[center][0] = Piece(PieceType.ATTACKER, Player.ATTACKER)
            cells[center][size - 1] = Piece(PieceType.ATTACKER, Player.ATTACKER)

            return Board(size, cells)
        }
    }
}

data class GameState(
    val board: Board = Board.initial(),
    val currentTurn: Player = Player.ATTACKER,
    val selected: Position? = null,
)
