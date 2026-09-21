package com.corbettcode.host2048game.game

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

/** The rules being checked here are the ones described at https://en.wikipedia.org/wiki/2048_(video_game). */
class Game2048Test {
    @Test
    fun tilesSlideAsFarAsTheyCan() {
        val state = boardOf(
            listOf(0, 0, 0, 2),
            listOf(0, 4, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
        ).move(Direction.LEFT)

        assertEquals(
            listOf(
                listOf(2, 0, 0, 0),
                listOf(4, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
            ),
            state.settledValues(),
        )
    }

    @Test
    fun equalTilesMergeAndScoreTheCombinedValue() {
        val state = boardOf(
            listOf(2, 2, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
        ).move(Direction.LEFT)

        assertEquals(listOf(4, 0, 0, 0), state.settledValues()[0])
        assertEquals(4, state.score)
    }

    @Test
    fun threeInARowOnlyMergesThePairFurthestAlong() {
        val state = boardOf(
            listOf(2, 2, 2, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
        ).move(Direction.LEFT)

        assertEquals(listOf(4, 2, 0, 0), state.settledValues()[0])
        assertEquals(4, state.score)
    }

    @Test
    fun fourInARowMergesIntoTwoPairs() {
        val state = boardOf(
            listOf(2, 2, 2, 2),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
        ).move(Direction.LEFT)

        assertEquals(listOf(4, 4, 0, 0), state.settledValues()[0])
        assertEquals(8, state.score)
    }

    @Test
    fun aMergedTileCannotMergeAgainInTheSameMove() {
        val state = boardOf(
            listOf(2, 2, 4, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
        ).move(Direction.LEFT)

        assertEquals(listOf(4, 4, 0, 0), state.settledValues()[0])
        assertEquals(4, state.score)
    }

    @Test
    fun tilesMergeInEveryDirection() {
        val board = boardOf(
            listOf(2, 0, 0, 2),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(2, 0, 0, 2),
        )

        assertEquals(listOf(4, 0, 0, 0), board.move(Direction.LEFT).settledValues()[0])
        assertEquals(listOf(0, 0, 0, 4), board.move(Direction.RIGHT).settledValues()[0])
        assertEquals(listOf(4, 0, 0, 4), board.move(Direction.UP).settledValues()[0])
        assertEquals(listOf(4, 0, 0, 4), board.move(Direction.DOWN).settledValues()[3])
    }

    @Test
    fun aMoveThatChangesNothingIsNotATurn() {
        val board = boardOf(
            listOf(2, 4, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
        )

        assertSame(board, board.move(Direction.LEFT))
    }

    @Test
    fun everyTurnSpawnsASingleTileWorthTwoOrFour() {
        val board = boardOf(
            listOf(0, 0, 0, 2),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
        )

        repeat(50) { seed ->
            val spawned = board.move(Direction.LEFT, Random(seed)).tiles.filter { it.isNew }
            assertEquals(1, spawned.size)
            assertTrue(spawned.single().value == 2 || spawned.single().value == 4)
        }
    }

    @Test
    fun spawnedTilesAreMostlyTwos() {
        var state = newGame(Random(1234))
        var twos = 0
        var total = 0
        repeat(200) {
            val next = state.move(Direction.values().random(Random(it)), Random(it))
            next.tiles.filter { tile -> tile.isNew }.forEach { tile ->
                total++
                if (tile.value == 2) twos++
            }
            state = if (next.status == GameStatus.OVER) newGame(Random(it.toLong())) else next
        }

        assertTrue(total > 50, "expected plenty of spawns, got $total")
        assertTrue(twos.toDouble() / total > 0.75, "expected mostly 2s, got ${twos.toDouble() / total}")
    }

    @Test
    fun aNewGameStartsWithTwoTilesAndNoScore() {
        repeat(20) { seed ->
            val state = newGame(Random(seed))
            assertEquals(2, state.tiles.size)
            assertEquals(0, state.score)
            assertEquals(GameStatus.PLAYING, state.status)
            assertTrue(state.tiles.all { it.value == 2 || it.value == 4 })
        }
    }

    @Test
    fun reachingTwentyFortyEightWinsOnceAndThenPlayCanContinue() {
        val won = boardOf(
            listOf(1024, 1024, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
        ).move(Direction.LEFT)

        assertEquals(GameStatus.WON, won.status)
        assertTrue(won.hasWon)
        assertEquals(WINNING_VALUE, won.score)

        val continued = won.keepPlaying()
        assertEquals(GameStatus.PLAYING, continued.status)
        assertTrue(continued.hasWon)

        // Winning again should not interrupt the player a second time.
        val secondWin = boardOf(
            listOf(1024, 1024, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
        ).copy(hasWon = true).move(Direction.LEFT)
        assertEquals(GameStatus.PLAYING, secondWin.status)
    }

    @Test
    fun aFullBoardWithNoEqualNeighboursIsGameOver() {
        val stuck = boardOf(
            listOf(2, 4, 2, 4),
            listOf(4, 2, 4, 2),
            listOf(2, 4, 2, 4),
            listOf(4, 2, 4, 2),
        )

        assertFalse(stuck.canMove())
        assertTrue(
            stuck.copy(tiles = stuck.tiles.filterNot { it.row == 3 && it.col == 3 }).canMove(),
            "a board with a free cell can always move",
        )
    }

    @Test
    fun theLastLegalMoveEndsTheGame() {
        // Sliding left frees up (3, 3), and whichever tile spawns there leaves no equal neighbours.
        val state = boardOf(
            listOf(2, 4, 2, 4),
            listOf(4, 2, 4, 2),
            listOf(2, 4, 2, 16),
            listOf(0, 8, 16, 8),
        ).move(Direction.LEFT)

        assertEquals(GameStatus.OVER, state.status)
        assertSame(state, state.move(Direction.RIGHT), "a finished game ignores further moves")
    }
}

private fun boardOf(vararg rows: List<Int>): GameState {
    var nextId = 0
    val tiles = buildList {
        rows.forEachIndexed { row, values ->
            values.forEachIndexed { col, value ->
                if (value != 0) add(Tile(nextId++, value, row, col))
            }
        }
    }
    return GameState(tiles, score = 0, status = GameStatus.PLAYING, hasWon = false, nextTileId = nextId)
}

/** The board as plain numbers, ignoring the tile that spawned at the end of the move. */
private fun GameState.settledValues(): List<List<Int>> {
    val grid = MutableList(BOARD_SIZE) { MutableList(BOARD_SIZE) { 0 } }
    tiles.filterNot { it.isNew }.forEach { grid[it.row][it.col] = it.value }
    return grid
}
