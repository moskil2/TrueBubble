package com.truebubble.ui.level

import android.app.Activity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truebubble.ui.common.MiniHorizontalVial
import com.truebubble.ui.common.MiniVerticalVial
import com.truebubble.ui.common.SupportOverlay
import com.truebubble.ui.common.SupportTopBarButton
import com.truebubble.ui.strings.LocalAppStrings
import com.truebubble.ui.strings.flagFor
import com.truebubble.ui.strings.supportedLanguages
import com.truebubble.ui.theme.AccentError
import com.truebubble.ui.theme.AccentOk
import com.truebubble.ui.theme.AccentOkDark
import com.truebubble.ui.theme.AccentWarn
import com.truebubble.ui.theme.LocalAppColors
import kotlin.math.abs

@Composable
fun LevelScreen(
    onSettingsClick: () -> Unit,
    onMenuClick: () -> Unit = {},
    onLanguageChange: (String) -> Unit = {},
    vm: LevelViewModel = viewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val c = LocalAppColors.current
    val s = LocalAppStrings.current
    val activity = LocalContext.current as Activity
    var showLangMenu by remember { mutableStateOf(false) }
    val supportState = remember { MutableTransitionState(false) }

    // Wygładzenie wyświetlanych cyfr — bąbelek i mini-oczka mają własną spring animation
    val animPitchDisplay by animateFloatAsState(
        targetValue = state.pitch,
        animationSpec = tween(durationMillis = 180),
        label = "pitch_display",
    )
    val animRollDisplay by animateFloatAsState(
        targetValue = state.roll,
        animationSpec = tween(durationMillis = 180),
        label = "roll_display",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(c.bg)
    ) {
        // Top bar z etykietami pod przyciskami
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp, top = 12.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TopBarButton(label = s.menuBtn, onClick = onMenuClick) {
                Icon(Icons.Outlined.Menu, contentDescription = s.menuBtn, tint = c.text2, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.weight(1f))
            SupportTopBarButton(expanded = supportState.targetState, onClick = { supportState.targetState = !supportState.targetState })
            Spacer(Modifier.weight(1f))
            Box {
                TopBarButton(label = "Lang", onClick = { showLangMenu = true }) {
                    Text(flagFor(s.langCode), fontSize = 20.sp)
                }
                DropdownMenu(expanded = showLangMenu, onDismissRequest = { showLangMenu = false }) {
                    supportedLanguages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text("${lang.flag}  ${lang.name}", fontSize = 14.sp) },
                            onClick = { onLanguageChange(lang.code); showLangMenu = false },
                        )
                    }
                }
            }
            Spacer(Modifier.width(10.dp))
            TopBarButton(label = s.settingsBtn, onClick = onSettingsClick) {
                Icon(Icons.Outlined.Tune, contentDescription = s.settingsBtn, tint = c.text2, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(10.dp))
            TopBarButton(label = s.closeBtn, onClick = { activity.finish() }) {
                Icon(Icons.Outlined.Close, contentDescription = s.closeBtn, tint = c.text2, modifier = Modifier.size(22.dp))
            }
        }

        // Hero — spacery zamiast Arrangement.Center, żeby layout nie "skakał" przy zmianie wartości
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
            Spacer(Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                HorizontalVial(roll = state.roll, isDark = c.isDark, highContrast = state.highContrastBubble, bubbleColor = state.bubbleColor, isMetallic = state.isMetallicBubble)
                VerticalVial(pitch = state.pitch, isDark = c.isDark, highContrast = state.highContrastBubble, bubbleColor = state.bubbleColor, isMetallic = state.isMetallicBubble)
            }

            Spacer(Modifier.height(22.dp))

            // Dual-axis readout — Alignment.Top żeby obie kolumny zaczynały od tej samej linii
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                AngleColumn(
                    label = s.longitudinal,
                    angle = animPitchDisplay,
                    status = state.pitchStatus,
                    isDark = c.isDark,
                    miniVial = { MiniVerticalVial(pitch = state.pitch, width = 26.dp, height = 60.dp, highContrast = state.highContrastBubble, bubbleColor = state.bubbleColor, isMetallic = state.isMetallicBubble) },
                )
                Box(
                    Modifier
                        .width(1.dp)
                        .height(110.dp)
                        .background(c.line)
                )
                AngleColumn(
                    label = s.transverse,
                    angle = animRollDisplay,
                    status = state.rollStatus,
                    isDark = c.isDark,
                    miniVial = { MiniHorizontalVial(roll = state.roll, width = 64.dp, height = 26.dp, highContrast = state.highContrastBubble, bubbleColor = state.bubbleColor, isMetallic = state.isMetallicBubble) },
                )
            }

            Spacer(Modifier.weight(1f))
            }
            SupportOverlay(supportState)
        }

        // Zapamiętana wartość — stała wysokość żeby ekran nie skakał
        val rPitch = state.rememberedPitch
        val rRoll = state.rememberedRoll
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (state.locked && rPitch != null && rRoll != null) {
                Text(
                    text = "${s.rememberedLine}:  ${"%.1f".format(abs(rPitch))}°  ·  ${"%.1f".format(abs(rRoll))}°",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        // Status row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Calibration status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = if (state.calibrated) AccentOk else c.text3,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = if (state.calibrated) s.calibrated else s.notCalibrated,
                    fontSize = 13.sp,
                    color = c.text2,
                )
            }

            // Zapamiętaj / Wyczyść button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, if (state.locked) AccentOk else c.line, RoundedCornerShape(12.dp))
                    .background(if (state.locked) AccentOk.copy(alpha = 0.15f) else c.surface)
                    .clickable { vm.toggleLock() }
                    .padding(horizontal = 16.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = if (state.locked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = null,
                    tint = if (state.locked) AccentOk else c.text,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = if (state.locked) s.clear else s.remember,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (state.locked) AccentOk else c.text,
                )
            }
        }
    }
}

@Composable
private fun AngleColumn(
    label: String,
    angle: Float,
    status: AngleStatus,
    isDark: Boolean,
    miniVial: @Composable () -> Unit,
) {
    val color = when (status) {
        AngleStatus.OK -> if (isDark) AccentOk else AccentOkDark
        AngleStatus.WARN -> AccentWarn
        AngleStatus.ERROR -> AccentError
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontSize = 10.sp,
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = LocalAppColors.current.text3,
        )
        Box(modifier = Modifier.width(175.dp), contentAlignment = Alignment.Center) {
            Text(
                text = "%.1f°".format(abs(angle)),
                fontSize = 56.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = color,
                lineHeight = 60.sp,
                letterSpacing = (-1).sp,
                maxLines = 1,
                softWrap = false,
            )
        }
        miniVial()
    }
}

@Composable
fun HorizontalVial(roll: Float, isDark: Boolean, highContrast: Boolean = false, bubbleColor: Color = Color.White, isMetallic: Boolean = false) {
    val vialWidth = 200.dp
    val vialHeight = 72.dp
    val bubbleRadius = 26.dp

    val clampedRoll = roll.coerceIn(-45f, 45f)
    val bubbleOffsetFraction by animateFloatAsState(
        targetValue = clampedRoll / 45f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "roll_anim",
    )

    val c = LocalAppColors.current
    val outerBg = if (isDark)
        Brush.verticalGradient(listOf(Color(0xFF0a0d0f), Color(0xFF141b1f)))
    else
        Brush.verticalGradient(listOf(Color(0xFFe8ebe6), Color(0xFFdfe3dd)))

    Box(
        modifier = Modifier
            .size(vialWidth, vialHeight)
            .clip(RoundedCornerShape(16.dp))
            .background(outerBg)
            .border(1.dp, c.line, RoundedCornerShape(16.dp)),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val inset = 8.dp.toPx()
            val innerRadius = 10.dp.toPx()

            drawRoundRect(
                brush = Brush.verticalGradient(listOf(c.vialTop, c.vialBottom)),
                topLeft = Offset(inset, inset),
                size = Size(size.width - inset * 2, size.height - inset * 2),
                cornerRadius = CornerRadius(innerRadius),
            )
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0f))),
                topLeft = Offset(14.dp.toPx(), 9.dp.toPx()),
                size = Size(size.width - 28.dp.toPx(), 16.dp.toPx()),
                cornerRadius = CornerRadius(innerRadius),
            )

            val maxTravel = (size.width / 2f) - inset - bubbleRadius.toPx()
            val bx = size.width / 2f + bubbleOffsetFraction * maxTravel
            drawBubble(bx, size.height / 2f, bubbleRadius.toPx(), bubbleColor, highContrast, isMetallic)

            val markOffset = 29.dp.toPx()
            val markColor = Color(0x8C28370A)
            drawLine(markColor, Offset(size.width / 2f - markOffset, inset), Offset(size.width / 2f - markOffset, size.height - inset), 2.dp.toPx())
            drawLine(markColor, Offset(size.width / 2f + markOffset, inset), Offset(size.width / 2f + markOffset, size.height - inset), 2.dp.toPx())
        }
    }
}

@Composable
fun VerticalVial(pitch: Float, isDark: Boolean, highContrast: Boolean = false, bubbleColor: Color = Color.White, isMetallic: Boolean = false) {
    val vialWidth = 72.dp
    val vialHeight = 200.dp
    val bubbleRadius = 26.dp

    val clampedPitch = pitch.coerceIn(-45f, 45f)
    val bubbleOffsetFraction by animateFloatAsState(
        targetValue = clampedPitch / 45f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "pitch_anim",
    )

    val c = LocalAppColors.current
    val outerBg = if (isDark)
        Brush.horizontalGradient(listOf(Color(0xFF0a0d0f), Color(0xFF141b1f)))
    else
        Brush.horizontalGradient(listOf(Color(0xFFe8ebe6), Color(0xFFdfe3dd)))

    Box(
        modifier = Modifier
            .size(vialWidth, vialHeight)
            .clip(RoundedCornerShape(16.dp))
            .background(outerBg)
            .border(1.dp, c.line, RoundedCornerShape(16.dp)),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val inset = 8.dp.toPx()
            val innerRadius = 10.dp.toPx()

            drawRoundRect(
                brush = Brush.horizontalGradient(listOf(c.vialTop, c.vialBottom)),
                topLeft = Offset(inset, inset),
                size = Size(size.width - inset * 2, size.height - inset * 2),
                cornerRadius = CornerRadius(innerRadius),
            )
            drawRoundRect(
                brush = Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0f))),
                topLeft = Offset(9.dp.toPx(), 14.dp.toPx()),
                size = Size(16.dp.toPx(), size.height - 28.dp.toPx()),
                cornerRadius = CornerRadius(innerRadius),
            )

            val maxTravel = (size.height / 2f) - inset - bubbleRadius.toPx()
            val by = size.height / 2f - bubbleOffsetFraction * maxTravel
            drawBubble(size.width / 2f, by, bubbleRadius.toPx(), bubbleColor, highContrast, isMetallic)

            val markOffset = 29.dp.toPx()
            val markColor = Color(0x8C28370A)
            drawLine(markColor, Offset(inset, size.height / 2f - markOffset), Offset(size.width - inset, size.height / 2f - markOffset), 2.dp.toPx())
            drawLine(markColor, Offset(inset, size.height / 2f + markOffset), Offset(size.width - inset, size.height / 2f + markOffset), 2.dp.toPx())
        }
    }
}

@Composable
private fun TopBarButton(label: String, onClick: () -> Unit, icon: @Composable () -> Unit) {
    val c = LocalAppColors.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(c.text.copy(alpha = 0.10f))
            .border(1.dp, c.text3.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        icon()
    }
}

fun DrawScope.drawBubble(cx: Float, cy: Float, radius: Float, bubbleColor: Color = Color.White, highContrast: Boolean = false, isMetallic: Boolean = false) {
    if (isMetallic) {
        drawMetallicBubble(cx, cy, radius, bubbleColor)
        return
    }
    val base = bubbleColor
    val a1 = if (highContrast) 0.95f else 0.72f
    val a2 = if (highContrast) 0.75f else 0.14f
    val a3 = if (highContrast) 0.50f else 0.00f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(base.copy(alpha = a1), base.copy(alpha = a2), base.copy(alpha = a3)),
            center = Offset(cx - radius * 0.24f, cy - radius * 0.36f),
            radius = radius,
        ),
        radius = radius,
        center = Offset(cx, cy),
    )
    drawCircle(
        color = base.copy(alpha = if (highContrast) 0.85f else 0.55f),
        radius = radius,
        center = Offset(cx, cy),
        style = Stroke(width = 1.5.dp.toPx()),
    )
}

private fun DrawScope.drawMetallicBubble(cx: Float, cy: Float, radius: Float, color: Color) {
    val isGold = color.blue < 0.1f
    val shadow = if (isGold) Color(0xFF7A5200) else Color(0xFF3A3A48)
    val mid    = if (isGold) Color(0xFFFFCC00) else Color(0xFFBEC0CC)
    val bright = if (isGold) Color(0xFFFFF8D0) else Color(0xFFFFFFFF)
    val rim    = if (isGold) Color(0xFF9B7200) else Color(0xFF787888)

    drawCircle(
        color = shadow.copy(alpha = 0.28f),
        radius = radius + 2.dp.toPx(),
        center = Offset(cx + 1.5f.dp.toPx(), cy + 1.5f.dp.toPx()),
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(bright, mid, shadow),
            center = Offset(cx - radius * 0.30f, cy - radius * 0.30f),
            radius = radius * 1.4f,
        ),
        radius = radius,
        center = Offset(cx, cy),
    )
    drawCircle(
        color = rim,
        radius = radius,
        center = Offset(cx, cy),
        style = Stroke(width = 1.5.dp.toPx()),
    )
    val specR = radius * 0.20f
    val specX = cx - radius * 0.36f
    val specY = cy - radius * 0.36f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0f)),
            center = Offset(specX, specY),
            radius = specR * 1.5f,
        ),
        radius = specR,
        center = Offset(specX, specY),
    )
}
