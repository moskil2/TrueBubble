package com.truebubble.ui.common

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.truebubble.ui.theme.LocalAppColors

@Composable
fun MiniHorizontalVial(
    roll: Float,
    width: Dp = 72.dp,
    height: Dp = 24.dp,
    highContrast: Boolean = false,
    bubbleColor: Color = Color.White,
) {
    val c = LocalAppColors.current
    val animRoll by animateFloatAsState(
        targetValue = roll.coerceIn(-30f, 30f) / 30f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "mini_roll",
    )
    Canvas(modifier = Modifier.size(width, height)) {
        drawMiniVialBody(horizontal = true, isDark = c.isDark, vialTop = c.vialTop, vialBottom = c.vialBottom, line = c.line)
        val inset = 4.dp.toPx()
        val bubR = (size.height / 2f - inset).coerceAtLeast(4.dp.toPx())
        val maxTravel = size.width / 2f - inset - bubR
        val bx = size.width / 2f + animRoll * maxTravel
        val by = size.height / 2f
        drawMiniBubble(bx, by, bubR, bubbleColor, highContrast)
    }
}

@Composable
fun MiniVerticalVial(
    pitch: Float,
    width: Dp = 24.dp,
    height: Dp = 72.dp,
    highContrast: Boolean = false,
    bubbleColor: Color = Color.White,
) {
    val c = LocalAppColors.current
    val animPitch by animateFloatAsState(
        targetValue = pitch.coerceIn(-30f, 30f) / 30f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "mini_pitch",
    )
    Canvas(modifier = Modifier.size(width, height)) {
        drawMiniVialBody(horizontal = false, isDark = c.isDark, vialTop = c.vialTop, vialBottom = c.vialBottom, line = c.line)
        val inset = 4.dp.toPx()
        val bubR = (size.width / 2f - inset).coerceAtLeast(4.dp.toPx())
        val maxTravel = size.height / 2f - inset - bubR
        val bx = size.width / 2f
        val by = size.height / 2f - animPitch * maxTravel
        drawMiniBubble(bx, by, bubR, bubbleColor, highContrast)
    }
}

private fun DrawScope.drawMiniVialBody(
    horizontal: Boolean,
    isDark: Boolean,
    vialTop: Color,
    vialBottom: Color,
    line: Color,
) {
    val r = CornerRadius(8.dp.toPx())
    val inset = 3.dp.toPx()
    val innerR = CornerRadius(5.dp.toPx())

    // Outer shell
    val outerBrush = if (isDark) {
        if (horizontal) Brush.verticalGradient(listOf(Color(0xFF0a0d0f), Color(0xFF141b1f)))
        else Brush.horizontalGradient(listOf(Color(0xFF0a0d0f), Color(0xFF141b1f)))
    } else {
        if (horizontal) Brush.verticalGradient(listOf(Color(0xFFe8ebe6), Color(0xFFdfe3dd)))
        else Brush.horizontalGradient(listOf(Color(0xFFe8ebe6), Color(0xFFdfe3dd)))
    }
    drawRoundRect(brush = outerBrush, cornerRadius = r)
    drawRoundRect(color = line, cornerRadius = r, style = Stroke(1.dp.toPx()))

    // Liquid
    val liquidBrush = if (horizontal)
        Brush.verticalGradient(listOf(vialTop, vialBottom))
    else
        Brush.horizontalGradient(listOf(vialTop, vialBottom))
    drawRoundRect(
        brush = liquidBrush,
        topLeft = Offset(inset, inset),
        size = Size(size.width - inset * 2, size.height - inset * 2),
        cornerRadius = innerR,
    )

    val bubInset = 4.dp.toPx()
    val markGap = if (horizontal)
        (size.height / 2f - bubInset + 3.dp.toPx()).coerceAtLeast(4.dp.toPx())
    else
        (size.width / 2f - bubInset + 3.dp.toPx()).coerceAtLeast(4.dp.toPx())
    val markColor = Color(0x8C28370A)
    if (horizontal) {
        val cx = size.width / 2f
        drawLine(markColor, Offset(cx - markGap, inset), Offset(cx - markGap, size.height - inset), 2.5.dp.toPx())
        drawLine(markColor, Offset(cx + markGap, inset), Offset(cx + markGap, size.height - inset), 2.5.dp.toPx())
    } else {
        val cy = size.height / 2f
        drawLine(markColor, Offset(inset, cy - markGap), Offset(size.width - inset, cy - markGap), 2.5.dp.toPx())
        drawLine(markColor, Offset(inset, cy + markGap), Offset(size.width - inset, cy + markGap), 2.5.dp.toPx())
    }
}

private fun DrawScope.drawMiniBubble(cx: Float, cy: Float, radius: Float, bubbleColor: Color = Color.White, highContrast: Boolean = false) {
    val base = bubbleColor
    val a1 = if (highContrast) 0.95f else 0.85f
    val a2 = if (highContrast) 0.75f else 0.40f
    val a3 = if (highContrast) 0.50f else 0.00f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(base.copy(alpha = a1), base.copy(alpha = a2), base.copy(alpha = a3)),
            center = Offset(cx - radius * 0.25f, cy - radius * 0.3f),
            radius = radius,
        ),
        radius = radius,
        center = Offset(cx, cy),
    )
    drawCircle(
        color = base.copy(alpha = if (highContrast) 0.85f else 0.65f),
        radius = radius,
        center = Offset(cx, cy),
        style = Stroke(1.dp.toPx()),
    )
}
