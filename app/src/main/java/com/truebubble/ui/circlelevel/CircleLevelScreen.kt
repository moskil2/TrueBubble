package com.truebubble.ui.circlelevel

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truebubble.ui.common.MiniHorizontalVial
import com.truebubble.ui.common.MiniVerticalVial
import com.truebubble.ui.level.AngleStatus
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
fun CircleLevelScreen(
    onSettingsClick: () -> Unit,
    onMenuClick: () -> Unit = {},
    onLanguageChange: (String) -> Unit = {},
    vm: CircleLevelViewModel = viewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val c = LocalAppColors.current
    val s = LocalAppStrings.current
    val activity = LocalContext.current as Activity
    var showLangMenu by remember { mutableStateOf(false) }

    val animRoll by animateFloatAsState(
        targetValue = state.roll,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "roll_2d",
    )
    val animPitch by animateFloatAsState(
        targetValue = state.pitch,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "pitch_2d",
    )
    // Wygładzenie cyfr
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

    fun angleColor(status: AngleStatus): Color = when (status) {
        AngleStatus.OK -> if (c.isDark) AccentOk else AccentOkDark
        AngleStatus.WARN -> AccentWarn
        AngleStatus.ERROR -> AccentError
    }

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

        // Hero — spacery zamiast Arrangement.Center
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))

            Canvas(modifier = Modifier.size(280.dp)) {
                drawBullseye(
                    animRoll = animRoll,
                    animPitch = animPitch,
                    withinTolerance = state.withinTolerance,
                    isDark = c.isDark,
                    vialTop = c.vialTop,
                    vialBottom = c.vialBottom,
                    lineColor = c.line,
                    highContrast = state.highContrastBubble,
                    bubbleColor = state.bubbleColor,
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(s.longitudinal, fontSize = 10.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.SemiBold, color = c.text3)
                    Box(modifier = Modifier.width(175.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "%.1f°".format(abs(animPitchDisplay)),
                            fontSize = 56.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            color = angleColor(state.pitchStatus),
                            lineHeight = 60.sp,
                            letterSpacing = (-1).sp,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                    MiniVerticalVial(pitch = state.pitch, width = 26.dp, height = 60.dp, highContrast = state.highContrastBubble, bubbleColor = state.bubbleColor)
                }
                Box(
                    Modifier
                        .width(1.dp)
                        .height(100.dp)
                        .background(c.line)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(s.transverse, fontSize = 10.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.SemiBold, color = c.text3)
                    Box(modifier = Modifier.width(175.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "%.1f°".format(abs(animRollDisplay)),
                            fontSize = 56.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            color = angleColor(state.rollStatus),
                            lineHeight = 60.sp,
                            letterSpacing = (-1).sp,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                    MiniHorizontalVial(roll = state.roll, width = 64.dp, height = 26.dp, highContrast = state.highContrastBubble, bubbleColor = state.bubbleColor)
                }
            }

            Spacer(Modifier.weight(1f))
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
                    text = "Zapamiętano:  Wzdł. ${"%.1f".format(abs(rPitch))}°  ·  Poprz. ${"%.1f".format(abs(rRoll))}°",
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
                    text = if (state.calibrated) "Skalibrowano" else "Nieskalibrowano",
                    fontSize = 13.sp,
                    color = c.text2,
                )
            }
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
                    text = if (state.locked) "Wyczyść" else "Zapamiętaj",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (state.locked) AccentOk else c.text,
                )
            }
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

private fun DrawScope.drawBullseye(
    animRoll: Float,
    animPitch: Float,
    withinTolerance: Boolean,
    isDark: Boolean,
    vialTop: Color,
    vialBottom: Color,
    lineColor: Color,
    highContrast: Boolean = false,
    bubbleColor: Color = Color.White,
) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val outerRadius = size.width / 2f - 2.dp.toPx()

    drawCircle(
        color = lineColor,
        radius = outerRadius,
        center = center,
        style = Stroke(width = 2.dp.toPx()),
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                vialTop,
                vialTop.copy(alpha = 0.9f),
                vialBottom,
                vialBottom.copy(alpha = 0.85f),
            ),
            center = Offset(center.x - outerRadius * 0.2f, center.y - outerRadius * 0.2f),
            radius = outerRadius * 1.1f,
        ),
        radius = outerRadius - 1.dp.toPx(),
        center = center,
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0f)),
            center = Offset(center.x - outerRadius * 0.3f, center.y - outerRadius * 0.4f),
            radius = outerRadius * 0.6f,
        ),
        radius = outerRadius - 1.dp.toPx(),
        center = center,
    )

    val toleranceRadius = outerRadius * 0.22f
    drawCircle(
        color = Color(0xFF28370A).copy(alpha = 0.35f),
        radius = toleranceRadius,
        center = center,
        style = Stroke(width = 1.5.dp.toPx()),
    )

    val midRadius = outerRadius * 0.55f
    drawCircle(
        color = Color(0xFF28370A).copy(alpha = 0.20f),
        radius = midRadius,
        center = center,
        style = Stroke(width = 1.dp.toPx()),
    )

    val crossColor = Color(0xFF28370A).copy(alpha = 0.45f)
    val margin = outerRadius * 0.08f
    drawLine(crossColor, Offset(center.x, center.y - outerRadius + margin), Offset(center.x, center.y + outerRadius - margin), 1.5.dp.toPx())
    drawLine(crossColor, Offset(center.x - outerRadius + margin, center.y), Offset(center.x + outerRadius - margin, center.y), 1.5.dp.toPx())

    for (i in 0 until 36) {
        val angleDeg = i * 10f
        val angleRad = Math.toRadians(angleDeg.toDouble())
        val isMajor = i % 9 == 0
        val tickLen = if (isMajor) 10.dp.toPx() else 5.dp.toPx()
        val tickOuter = outerRadius - 3.dp.toPx()
        val sx = center.x + tickOuter * Math.sin(angleRad).toFloat()
        val sy = center.y - tickOuter * Math.cos(angleRad).toFloat()
        val ex = center.x + (tickOuter - tickLen) * Math.sin(angleRad).toFloat()
        val ey = center.y - (tickOuter - tickLen) * Math.cos(angleRad).toFloat()
        drawLine(
            color = Color(0xFF28370A).copy(alpha = if (isMajor) 0.5f else 0.3f),
            start = Offset(sx, sy),
            end = Offset(ex, ey),
            strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }

    val maxTravel = outerRadius - toleranceRadius - 30.dp.toPx()
    val maxAngle = 30f
    val bx = center.x + (animRoll.coerceIn(-maxAngle, maxAngle) / maxAngle) * maxTravel
    val by = center.y - (animPitch.coerceIn(-maxAngle, maxAngle) / maxAngle) * maxTravel
    val bubbleRadius = 28.dp.toPx()

    val bubBase = bubbleColor
    val ba1 = if (highContrast) 0.95f else 0.72f
    val ba2 = if (highContrast) 0.75f else 0.14f
    val ba3 = if (highContrast) 0.50f else 0.00f

    drawCircle(
        color = Color(0xFF28370A).copy(alpha = if (highContrast) 0.3f else 0.15f),
        radius = bubbleRadius + 3.dp.toPx(),
        center = Offset(bx + 2.dp.toPx(), by + 2.dp.toPx()),
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(bubBase.copy(alpha = ba1), bubBase.copy(alpha = ba2), bubBase.copy(alpha = ba3)),
            center = Offset(bx - bubbleRadius * 0.3f, by - bubbleRadius * 0.35f),
            radius = bubbleRadius,
        ),
        radius = bubbleRadius,
        center = Offset(bx, by),
    )

    drawCircle(
        color = bubBase.copy(alpha = if (highContrast) 0.70f else 0.45f),
        radius = bubbleRadius,
        center = Offset(bx, by),
        style = Stroke(width = 1.5.dp.toPx()),
    )
}
