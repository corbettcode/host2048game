package com.corbettcode.host2024game.game

import kotlin.random.Random

/** 2048 is played on a plain 4x4 grid. */
const val BOARD_SIZE = 4

/** The game is won when a tile with this value appears on the board. */
const val WINNING_VALUE = 2048

/** A spawned tile is a 2 this often, and a 4 the rest of the time. */
private const val SPAWN_TWO_CHANCE = 0.9

/** The number of tiles the board starts with. */
private const val STARTING_TILES = 2

enum class Direction { UP, DOWN, LEFT, RIGHT }

enum class GameStatus {
    /** Waiting for the player's next move. */
    PLAYING,

    /** A 2048 tile just appeared; the player can stop here or keep playing. */
    WON,

    /** No legal moves remain. */
    OVER,
}

/**
 * A single tile on the board.
 *
 * Tiles keep a stable [id] across moves so the UI can animate them sliding from cell to cell. When two tiles merge,
 * the *moving* tile is the one that survives (carrying its id to the destination), which means the tile it landed on
 * disappears underneath it and the merge reads as a single smooth slide.
 */
data class Tile(
    val id: Int,
    val value: Int,
    val row: Int,
    val col: Int,
    /** True if this tile was spawned by the move that produced the current state. */
    val isNew: Boolean = false,
    /** True if this tile was formed by a merge during the move that produced the current state. */
    val justMerged: Boolean = false,
)

data class GameState(
    val tiles: List<Tile>,
    val score: Int,
    val status: GameStatus,
    /** True once a 2048 tile has been reached, so the "you win" message is only shown once. */
    val hasWon: Boolean,
    val nextTileId: Int,
) {
    val highestTile: Int get() = tiles.maxOfOrNull { it.value } ?: 0

    internal fun withTile(value: Int, row: Int, col: Int) = copy(
        tiles = tiles + Tile(nextTileId, value, row, col, isNew = true),
        nextTileId = nextTileId + 1,
    )
}

/** Starts a fresh game: an empty board plus [STARTING_TILES] randomly placed tiles. */
fun newGame(random: Random = Random.Default): GameState {
    var state = GameState(emptyList(), score = 0, status = GameStatus.PLAYING, hasWon = false, nextTileId = 0)
    repeat(STARTING_TILES) { state = state.spawnTile(random) }
    return state
}

/**
 * Slides every tile as far as it can go in [direction], merging equal pairs along the way, then spawns a new tile.
 *
 * Returns the same state (unchanged) if nothing could move, which is not a turn.
 */
fun GameState.move(direction: Direction, random: Random = Random.Default): GameState {
    if (status != GameStatus.PLAYING) return this

    val grid = toGrid()
    val movedTiles = mutableListOf<Tile>()
    var gainedScore = 0
    var anythingMoved = false

    for (lineIndex in 0 until BOARD_SIZE) {
        // Cells of this row/column ordered from the edge the tiles are sliding towards, inwards.
        val cells = lineCells(direction, lineIndex)
        val tilesInLine = cells.mapNotNull { (row, col) -> grid[row][col] }

        var writeIndex = 0
        // The tile sitting at `writeIndex - 1`, if it is still allowed to merge. A tile formed by a merge cannot
        // merge again in the same move.
        var mergeCandidate: Tile? = null

        for (tile in tilesInLine) {
            val candidate = mergeCandidate
            if (candidate != null && candidate.value == tile.value) {
                val (row, col) = cells[writeIndex - 1]
                val mergedValue = tile.value * 2
                gainedScore += mergedValue
                anythingMoved = true
                // The tile that was already parked here is replaced by the merged tile, which inherits the id of the
                // tile that slid in so the slide stays animated.
                movedTiles.removeAll { it.id == candidate.id }
                movedTiles += tile.copy(value = mergedValue, row = row, col = col, isNew = false, justMerged = true)
                mergeCandidate = null
            } else {
                val (row, col) = cells[writeIndex]
                if (row != tile.row || col != tile.col) anythingMoved = true
                val placed = tile.copy(row = row, col = col, isNew = false, justMerged = false)
                movedTiles += placed
                mergeCandidate = placed
                writeIndex++
            }
        }
    }

    if (!anythingMoved) return this

    val afterMove = copy(tiles = movedTiles, score = score + gainedScore).spawnTile(random)
    val justWon = !hasWon && afterMove.highestTile >= WINNING_VALUE
    return afterMove.copy(
        status = when {
            justWon -> GameStatus.WON
            !afterMove.canMove() -> GameStatus.OVER
            else -> GameStatus.PLAYING
        },
        hasWon = hasWon || justWon,
    )
}

/** Dismisses the "you win" message so the player can keep going for a higher score. */
fun GameState.keepPlaying(): GameState = if (status == GameStatus.WON) copy(status = GameStatus.PLAYING) else this

/** True while an empty cell or a pair of equal neighbours remains. */
fun GameState.canMove(): Boolean {
    if (tiles.size < BOARD_SIZE * BOARD_SIZE) return true

    val grid = toGrid()
    for (row in 0 until BOARD_SIZE) {
        for (col in 0 until BOARD_SIZE) {
            val value = grid[row][col]?.value ?: return true
            if (col + 1 < BOARD_SIZE && grid[row][col + 1]?.value == value) return true
            if (row + 1 < BOARD_SIZE && grid[row + 1][col]?.value == value) return true
        }
    }
    return false
}

private fun GameState.spawnTile(random: Random): GameState {
    val occupied = tiles.mapTo(HashSet()) { it.row to it.col }
    val free = buildList {
        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                if ((row to col) !in occupied) add(row to col)
            }
        }
    }
    if (free.isEmpty()) return this

    val (row, col) = free.random(random)
    return withTile(if (random.nextDouble() < SPAWN_TWO_CHANCE) 2 else 4, row, col)
}

private fun GameState.toGrid(): Array<Array<Tile?>> {
    val grid = Array(BOARD_SIZE) { arrayOfNulls<Tile>(BOARD_SIZE) }
    tiles.forEach { grid[it.row][it.col] = it }
    return grid
}

private fun lineCells(direction: Direction, lineIndex: Int): List<Pair<Int, Int>> {
    val indices = 0 until BOARD_SIZE
    return when (direction) {
        Direction.LEFT -> indices.map { col -> lineIndex to col }
        Direction.RIGHT -> indices.reversed().map { col -> lineIndex to col }
        Direction.UP -> indices.map { row -> row to lineIndex }
        Direction.DOWN -> indices.reversed().map { row -> row to lineIndex }
    }
}
