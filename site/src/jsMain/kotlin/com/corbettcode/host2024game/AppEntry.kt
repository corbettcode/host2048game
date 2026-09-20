package com.corbettcode.host2024game

import androidx.compose.runtime.Composable
import com.corbettcode.host2024game.components.GameColors
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color

@InitSilk
fun initStyles(ctx: InitSilkContext) {
    ctx.config.initialColorMode = ColorMode.LIGHT

    ctx.theme.palettes.light.background = GameColors.pageBackground(ColorMode.LIGHT)
    ctx.theme.palettes.light.color = GameColors.pageText(ColorMode.LIGHT)
    ctx.theme.palettes.dark.background = GameColors.pageBackground(ColorMode.DARK)
    ctx.theme.palettes.dark.color = GameColors.pageText(ColorMode.DARK)

    ctx.stylesheet.registerStyleBase("html, body") {
        Modifier
            .fillMaxHeight()
            .fontFamily("Clear Sans", "Helvetica Neue", "Arial", "sans-serif")
    }
}

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SilkApp {
        Surface(SmoothColorStyle.toModifier().fillMaxHeight()) {
            content()
        }
    }
}
