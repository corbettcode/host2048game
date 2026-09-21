package com.corbettcode.host2048game.components.widgets

import androidx.compose.runtime.Composable
import com.corbettcode.host2048game.components.GameColors
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.UserSelect
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.compose.ui.modifiers.userSelect
import com.varabyte.kobweb.compose.ui.modifiers.whiteSpace
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.px

val GameButtonStyle = CssStyle {
    base {
        Modifier
            .backgroundColor(GameColors.Button)
            .color(GameColors.LightText)
            .borderRadius(3.px)
            .padding(topBottom = 0.6.cssRem, leftRight = 1.1.cssRem)
            .fontWeight(FontWeight.Bold)
            .whiteSpace(WhiteSpace.NoWrap)
            .cursor(Cursor.Pointer)
            .userSelect(UserSelect.None)
            .transition(Transition.of("background-color", 100.ms))
    }
    hover {
        Modifier.backgroundColor(GameColors.ButtonHover)
    }
}

/** A chunky 2048-style button. */
@Composable
fun GameButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        GameButtonStyle.toModifier().then(modifier).onClick { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        SpanText(label)
    }
}
