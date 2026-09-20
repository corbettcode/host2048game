package com.corbettcode.host2024game.components.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.corbettcode.host2024game.components.GameColors
import com.corbettcode.host2024game.game.BOARD_SIZE
import com.corbettcode.host2024game.game.GameState
import com.corbettcode.host2024game.game.GameStatus
import com.corbettcode.host2024game.game.Tile
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.UserSelect
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.animation
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.left
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.top
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.compose.ui.modifiers.userSelect
import com.varabyte.kobweb.compose.ui.modifiers.zIndex
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.animation.Keyframes
import com.varabyte.kobweb.silk.style.animation.toAnimation
import org.jetbrains.compose.web.css.AnimationFillMode
import org.jetbrains.compose.web.css.AnimationTimingFunction
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

/** How long a tile takes to slide from one cell to the next. */
private val SLIDE_DURATION = 110.ms

/** All board measurements derive from the board's pixel size, so the whole thing scales to the viewport. */
class BoardMetrics(val boardSize: Double) {
    val gap = boardSize * 0.025
    val cellSize = (boardSize - gap * (BOARD_SIZE + 1)) / BOARD_SIZE

    /** Distance from the first cell to the cell at [index]. */
    fun offsetOf(index: Int) = index * (cellSize + gap)
}

val TileAppearKeyframes = Keyframes {
    from { Modifier.scaleTransform(0.0) }
    to { Modifier.scaleTransform(1.0) }
}

val TileMergeKeyframes = Keyframes {
    each(0.percent, 100.percent) { Modifier.scaleTransform(1.0) }
    each(60.percent) { Modifier.scaleTransform(1.18) }
}

val OverlayFadeInKeyframes = Keyframes {
    from { Modifier.opacityOf(0.0) }
    to { Modifier.opacityOf(1.0) }
}

@Composable
fun GameBoard(
    state: GameState,
    metrics: BoardMetrics,
    onRestart: () -> Unit,
    onKeepPlaying: () -> Unit,
) {
    Box(
        Modifier
            .size(metrics.boardSize.px)
            .backgroundColor(GameColors.Board)
            .borderRadius(metrics.gap.px)
            .position(Position.Relative)
            .userSelect(UserSelect.None)
            // Let swipes drive the game instead of scrolling the page.
            .styleModifier { property("touch-action", "none") }
    ) {
        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                Box(cellModifier(metrics, row, col).backgroundColor(GameColors.EmptyCell))
            }
        }

        state.tiles.forEach { tile ->
            key(tile.id) { TileView(tile, metrics) }
        }

        when (state.status) {
            GameStatus.WON -> GameOverlay("You win!", "Keep going", metrics, onKeepPlaying)
            GameStatus.OVER -> GameOverlay("Game over!", "Try again", metrics, onRestart)

            GameStatus.PLAYING -> Unit
        }
    }
}

@Composable
private fun TileView(tile: Tile, metrics: BoardMetrics) {
    Box(
        cellModifier(metrics, tile.row, tile.col)
            .transition(Transition.of("transform", SLIDE_DURATION, AnimationTimingFunction.EaseInOut))
            .zIndex(if (tile.justMerged) 2 else 1)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .backgroundColor(GameColors.tile(tile.value))
                .borderRadius((metrics.gap * 0.5).px)
                .thenIf(tile.isNew) {
                    Modifier.animation(
                        TileAppearKeyframes.toAnimation(
                            duration = SLIDE_DURATION,
                            timingFunction = AnimationTimingFunction.EaseInOut,
                        )
                    )
                }
                .thenIf(tile.justMerged) {
                    Modifier.animation(
                        TileMergeKeyframes.toAnimation(
                            duration = 180.ms,
                            timingFunction = AnimationTimingFunction.EaseInOut,
                        )
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            SpanText(
                tile.value.toString(),
                Modifier
                    .color(GameColors.tileText(tile.value))
                    .fontSize(fontSizeFor(tile.value, metrics.cellSize).px)
                    .fontWeight(FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun GameOverlay(
    message: String,
    buttonLabel: String,
    metrics: BoardMetrics,
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .position(Position.Absolute)
            .left(0.px)
            .top(0.px)
            .fillMaxSize()
            .backgroundColor(GameColors.Overlay)
            .borderRadius(metrics.gap.px)
            .zIndex(3)
            .animation(
                OverlayFadeInKeyframes.toAnimation(
                    duration = 400.ms,
                    timingFunction = AnimationTimingFunction.EaseInOut,
                    fillMode = AnimationFillMode.Both,
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SpanText(
                message,
                Modifier
                    .color(GameColors.DarkText)
                    .fontSize((metrics.cellSize * 0.55).px)
                    .fontWeight(FontWeight.Bold)
            )
            Row(Modifier.margin(top = (metrics.gap * 2).px)) {
                GameButton(buttonLabel, onClick = onClick)
            }
        }
    }
}

/** Places a box on the board's grid, sliding it into place via a transform. */
private fun cellModifier(metrics: BoardMetrics, row: Int, col: Int) = Modifier
    .position(Position.Absolute)
    .left(metrics.gap.px)
    .top(metrics.gap.px)
    .size(metrics.cellSize.px)
    .borderRadius((metrics.gap * 0.5).px)
    .styleModifier {
        property("transform", "translate(${metrics.offsetOf(col)}px, ${metrics.offsetOf(row)}px)")
    }

/** Longer numbers have to shrink to keep fitting on a tile. */
private fun fontSizeFor(value: Int, cellSize: Double) = when (value.toString().length) {
    1, 2 -> cellSize * 0.46
    3 -> cellSize * 0.38
    4 -> cellSize * 0.30
    5 -> cellSize * 0.24
    else -> cellSize * 0.20
}

private fun Modifier.scaleTransform(scale: Double) = styleModifier { property("transform", "scale($scale)") }

private fun Modifier.opacityOf(opacity: Double) = styleModifier { property("opacity", opacity.toString()) }
