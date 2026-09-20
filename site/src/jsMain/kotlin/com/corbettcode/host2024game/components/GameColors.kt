package com.corbettcode.host2024game.components

import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.silk.theme.colors.ColorMode

/** The classic 2048 palette (https://en.wikipedia.org/wiki/2048_(video_game)). */
object GameColors {
    val Board = Color.rgb(0xbbada0)
    val EmptyCell = Color.rgba(238, 228, 218, 0.35f)
    val DarkText = Color.rgb(0x776e65)
    val LightText = Color.rgb(0xf9f6f2)
    val Overlay = Color.rgba(238, 228, 218, 0.73f)
    val ScoreLabel = Color.rgb(0xeee4da)
    val Button = Color.rgb(0x8f7a66)
    val ButtonHover = Color.rgb(0xa08b76)

    private val tileColors = mapOf(
        2 to Color.rgb(0xeee4da),
        4 to Color.rgb(0xede0c8),
        8 to Color.rgb(0xf2b179),
        16 to Color.rgb(0xf59563),
        32 to Color.rgb(0xf67c5f),
        64 to Color.rgb(0xf65e3b),
        128 to Color.rgb(0xedcf72),
        256 to Color.rgb(0xedcc61),
        512 to Color.rgb(0xedc850),
        1024 to Color.rgb(0xedc53f),
        2048 to Color.rgb(0xedc22e),
    )

    /** Tiles past 2048 all share the same dark treatment. */
    private val SuperTile = Color.rgb(0x3c3a32)

    fun tile(value: Int): Color = tileColors[value] ?: SuperTile

    fun tileText(value: Int): Color = if (value <= 4) DarkText else LightText

    fun pageBackground(colorMode: ColorMode) =
        if (colorMode.isLight) Color.rgb(0xfaf8ef) else Color.rgb(0x1a1815)

    fun pageText(colorMode: ColorMode) =
        if (colorMode.isLight) DarkText else Color.rgb(0xeee4da)
}
