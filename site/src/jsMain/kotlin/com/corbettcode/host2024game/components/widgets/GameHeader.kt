package com.corbettcode.host2024game.components.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.setValue
import com.corbettcode.host2024game.components.GameColors
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.animation
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.lineHeight
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.top
import com.varabyte.kobweb.compose.ui.modifiers.whiteSpace
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.animation.Keyframes
import com.varabyte.kobweb.silk.style.animation.toAnimation
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.AnimationFillMode
import org.jetbrains.compose.web.css.AnimationTimingFunction
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.px

val ScoreGainKeyframes = Keyframes {
    from { Modifier.floatUp(0.0, 1.0) }
    to { Modifier.floatUp(-60.0, 0.0) }
}

/** Title, tagline, score boxes and the controls that sit above the board. */
@Composable
fun GameHeader(
    score: Int,
    bestScore: Int,
    scoreGain: Int,
    gainCount: Int,
    onRestart: () -> Unit,
) {
    var colorMode by ColorMode.currentState

    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            SpanText(
                "2048",
                Modifier
                    .fontSize(3.5.cssRem)
                    .fontWeight(FontWeight.Bold)
                    .lineHeight(1.1)
            )
            Spacer()
            Row(Modifier.margin(top = 0.4.cssRem)) {
                ScoreBox("SCORE", score, scoreGain, gainCount)
                Box(Modifier.margin(left = 0.5.cssRem)) {
                    ScoreBox("BEST", bestScore)
                }
            }
        }

        Row(Modifier.fillMaxWidth().margin(top = 0.75.cssRem), verticalAlignment = Alignment.Bottom) {
            SpanText(
                "Join the numbers and get to the 2048 tile!",
                Modifier.fontSize(1.cssRem).lineHeight(1.4)
            )
            Spacer()
            GameButton(
                if (colorMode.isLight) "Dark" else "Light",
                Modifier.margin(right = 0.5.cssRem),
            ) { colorMode = colorMode.opposite }
            GameButton("New Game", onClick = onRestart)
        }
    }
}

@Composable
private fun ScoreBox(label: String, value: Int, gain: Int = 0, gainCount: Int = 0) {
    Column(
        Modifier
            .backgroundColor(GameColors.Board)
            .borderRadius(3.px)
            .padding(topBottom = 0.4.cssRem, leftRight = 1.cssRem)
            .minWidth(5.cssRem)
            .position(Position.Relative),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SpanText(
            label,
            Modifier
                .color(GameColors.ScoreLabel)
                .fontSize(0.7.cssRem)
                .fontWeight(FontWeight.Bold)
                .textAlign(TextAlign.Center)
        )
        SpanText(
            value.toString(),
            Modifier
                .color(Colors.White)
                .fontSize(1.5.cssRem)
                .fontWeight(FontWeight.Bold)
                .lineHeight(1.2)
        )
        if (gain > 0) key(gainCount) { ScoreGain(gain) }
    }
}

/** The "+4" that floats up out of the score box whenever tiles merge. */
@Composable
private fun ScoreGain(gain: Int) {
    SpanText(
        "+$gain",
        Modifier
            .position(Position.Absolute)
            .top(0.6.cssRem)
            .fontSize(1.5.cssRem)
            .fontWeight(FontWeight.Bold)
            .color(GameColors.DarkText)
            .whiteSpace(WhiteSpace.NoWrap)
            .animation(
                ScoreGainKeyframes.toAnimation(
                    duration = 600.ms,
                    timingFunction = AnimationTimingFunction.EaseInOut,
                    fillMode = AnimationFillMode.Forwards,
                )
            )
    )
}

private fun Modifier.floatUp(offsetY: Double, opacity: Double) = styleModifier {
    property("transform", "translateY(${offsetY}px)")
    property("opacity", opacity.toString())
}
