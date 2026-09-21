package com.corbettcode.host2048game.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.browser.localStorage

private const val BEST_SCORE_KEY = "host2048game.bestScore"

/** Holds the live game and the best score, which survives reloads via local storage. */
class GameController {
    var state by mutableStateOf(newGame())
        private set

    var bestScore by mutableStateOf(localStorage.getItem(BEST_SCORE_KEY)?.toIntOrNull() ?: 0)
        private set

    /** Points won by the most recent move, for the "+4" that floats out of the score box. */
    var lastGain by mutableStateOf(0)
        private set

    /** Bumped on every scoring move, so that repeated gains of the same size still replay the animation. */
    var gainCount by mutableStateOf(0)
        private set

    fun move(direction: Direction) {
        val next = state.move(direction)
        if (next === state) return

        val gained = next.score - state.score
        if (gained > 0) {
            lastGain = gained
            gainCount++
        }
        state = next
        if (next.score > bestScore) {
            bestScore = next.score
            runCatching { localStorage.setItem(BEST_SCORE_KEY, next.score.toString()) }
        }
    }

    fun restart() {
        state = newGame()
        lastGain = 0
    }

    fun keepPlaying() {
        state = state.keepPlaying()
    }
}
