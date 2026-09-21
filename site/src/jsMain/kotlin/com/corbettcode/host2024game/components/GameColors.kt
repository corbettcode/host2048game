package com.corbettcode.host2024game.components

import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.silk.theme.colors.ColorMode

/**
 * A green take on the 2048 palette: tiles start as pale mint and deepen through leaf and forest greens, with the
 * winning tile breaking out as a bright emerald.
 */
object GameColors {
    val Board = Color.rgb(0xADC3A5)
    val EmptyCell = Color.rgba(238, 245, 232, 0.35f)
    val DarkText = Color.rgb(0x46543f)
    val LightText = Color.rgb(0xf4f9f0)
    val Overlay = Color.rgba(238, 245, 232, 0.73f)
    val ScoreLabel = Color.rgb(0xeef5e8)
    val Button = Color.rgb(0x5d7d55)
    val ButtonHover = Color.rgb(0x6f9065)

    private val tileColors = mapOf(
        2 to Color.rgb(0xeef5e8),
        4 to Color.rgb(0xddedd1),
        8 to Color.rgb(0x9ccd74),
        16 to Color.rgb(0x7cbd55),
        32 to Color.rgb(0x5faf3c),
        64 to Color.rgb(0x43a12c),
        128 to Color.rgb(0x2f9234),
        256 to Color.rgb(0x23843a),
        512 to Color.rgb(0x1a763f),
        1024 to Color.rgb(0x136843),
        2048 to Color.rgb(0x1fa85c),
    )

    /** Tiles past 2048 all share the same dark treatment. */
    private val SuperTile = Color.rgb(0x10331f)

    fun tile(value: Int): Color = tileColors[value] ?: SuperTile

    fun tileText(value: Int): Color = if (value <= 4) DarkText else LightText

    fun pageBackground(colorMode: ColorMode) =
        if (colorMode.isLight) Color.rgb(0xf3f7ef) else Color.rgb(0x151a14)

    fun pageText(colorMode: ColorMode) =
        if (colorMode.isLight) DarkText else Color.rgb(0xe3edda)
}
