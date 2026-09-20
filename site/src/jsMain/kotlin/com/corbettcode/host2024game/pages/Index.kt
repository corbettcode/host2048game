package com.corbettcode.host2024game.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.corbettcode.host2024game.components.widgets.BoardMetrics
import com.corbettcode.host2024game.components.widgets.GameBoard
import com.corbettcode.host2024game.components.widgets.GameHeader
import com.corbettcode.host2024game.game.Direction
import com.corbettcode.host2024game.game.GameController
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.lineHeight
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.opacity
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.window
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val MIN_BOARD_SIZE = 240.0
private const val MAX_BOARD_SIZE = 480.0

/** How far a finger has to travel before it counts as a swipe. */
private const val SWIPE_THRESHOLD = 24.0

@Page
@Composable
fun HomePage() {
    val controller = remember { GameController() }
    val metrics = rememberBoardMetrics()

    GameInput(onMove = controller::move)

    Box(Modifier.fillMaxSize().padding(1.cssRem), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.width(metrics.boardSize.px).margin(topBottom = 1.5.cssRem)) {
            GameHeader(
                score = controller.state.score,
                bestScore = controller.bestScore,
                scoreGain = controller.lastGain,
                gainCount = controller.gainCount,
                onRestart = controller::restart,
            )
            Box(Modifier.margin(top = 1.5.cssRem)) {
                GameBoard(
                    state = controller.state,
                    metrics = metrics,
                    onRestart = controller::restart,
                    onKeepPlaying = controller::keepPlaying,
                )
            }
            HowToPlay()
        }
    }
}

@Composable
private fun HowToPlay() {
    Column(Modifier.margin(top = 1.5.cssRem).opacity(0.8)) {
        SpanText(
            "HOW TO PLAY",
            Modifier.fontSize(0.8.cssRem).fontWeight(FontWeight.Bold)
        )
        SpanText(
            "Use the arrow keys (or WASD, or swipe) to slide the tiles. Tiles with the same number merge into " +
                "one when they touch. Get to the 2048 tile!",
            Modifier.fontSize(0.9.cssRem).lineHeight(1.5).margin(top = 0.25.cssRem)
        )
    }
}

/** Listens for arrow keys, WASD and swipes for as long as the page is showing. */
@Composable
private fun GameInput(onMove: (Direction) -> Unit) {
    DisposableEffect(Unit) {
        val keyListener: (Event) -> Unit = { event ->
            val direction = directionOf((event as KeyboardEvent).key)
            if (direction != null) {
                // Stop arrow keys from also scrolling the page.
                event.preventDefault()
                onMove(direction)
            }
        }

        var swipeStartX = 0.0
        var swipeStartY = 0.0
        val touchStartListener: (Event) -> Unit = { event ->
            touchPoint(event)?.let { (x, y) ->
                swipeStartX = x
                swipeStartY = y
            }
        }
        val touchEndListener: (Event) -> Unit = { event ->
            touchPoint(event)?.let { (x, y) ->
                val deltaX = x - swipeStartX
                val deltaY = y - swipeStartY
                val direction = when {
                    max(abs(deltaX), abs(deltaY)) < SWIPE_THRESHOLD -> null
                    abs(deltaX) > abs(deltaY) -> if (deltaX > 0) Direction.RIGHT else Direction.LEFT
                    else -> if (deltaY > 0) Direction.DOWN else Direction.UP
                }
                if (direction != null) onMove(direction)
            }
        }

        window.addEventListener("keydown", keyListener)
        window.addEventListener("touchstart", touchStartListener)
        window.addEventListener("touchend", touchEndListener)
        onDispose {
            window.removeEventListener("keydown", keyListener)
            window.removeEventListener("touchstart", touchStartListener)
            window.removeEventListener("touchend", touchEndListener)
        }
    }
}

private fun directionOf(key: String): Direction? = when (key) {
    "ArrowUp", "w", "W" -> Direction.UP
    "ArrowDown", "s", "S" -> Direction.DOWN
    "ArrowLeft", "a", "A" -> Direction.LEFT
    "ArrowRight", "d", "D" -> Direction.RIGHT
    else -> null
}

/** Reads the first changed touch of a touch event; the typed DOM API for these is patchy, so go through JS. */
private fun touchPoint(event: Event): Pair<Double, Double>? {
    val touches = event.asDynamic().changedTouches ?: return null
    if (touches.length == 0) return null
    val touch = touches[0]
    return (touch.clientX as Number).toDouble() to (touch.clientY as Number).toDouble()
}

/** The board is sized to fit the viewport, and re-measured whenever the window changes. */
@Composable
private fun rememberBoardMetrics(): BoardMetrics {
    var boardSize by remember { mutableStateOf(calculateBoardSize()) }
    DisposableEffect(Unit) {
        val resizeListener: (Event) -> Unit = { boardSize = calculateBoardSize() }
        window.addEventListener("resize", resizeListener)
        onDispose { window.removeEventListener("resize", resizeListener) }
    }
    return remember(boardSize) { BoardMetrics(boardSize) }
}

private fun calculateBoardSize(): Double {
    val availableWidth = window.innerWidth - 32.0
    // Leave room for the header and the instructions below the board.
    val availableHeight = window.innerHeight - 340.0
    return max(MIN_BOARD_SIZE, min(MAX_BOARD_SIZE, min(availableWidth, availableHeight)))
}
