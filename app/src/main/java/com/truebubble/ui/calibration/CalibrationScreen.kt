package com.truebubble.ui.calibration

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truebubble.ui.common.MiniHorizontalVial
import com.truebubble.ui.common.MiniVerticalVial
import com.truebubble.ui.level.AngleStatus
import com.truebubble.ui.level.angleStatus
import com.truebubble.ui.strings.LocalAppStrings
import com.truebubble.ui.theme.AccentError
import com.truebubble.ui.theme.AccentOk
import com.truebubble.ui.theme.AccentOkDark
import com.truebubble.ui.theme.AccentWarn
import com.truebubble.ui.theme.LocalAppColors
import kotlin.math.abs

@Composable
fun CalibrationScreen(
    onBack: () -> Unit,
    vm: CalibrationViewModel = viewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val c = LocalAppColors.current
    val s = LocalAppStrings.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(c.bg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 22.dp, top = 20.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = s.back, tint = c.text)
            }
            Text(s.calibrationTitle, fontSize = 21.sp, fontWeight = FontWeight.SemiBold, color = c.text)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {

            // ── INSTRUKCJA Z ANIMACJĄ ──────────────────────────────────────────
            SectionLabel(s.howToCalibrate)

            CalibrationInstructionAnimation()

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                InstructionStep("1", s.instrStep1)
                InstructionStep("2", s.instrStep2)
                InstructionStep("3", s.instrStep3)
                InstructionStep("4", s.instrStep4)
            }

            // ── AUTO KALIBRACJA ───────────────────────────────────────────────
            SectionLabel(s.autoCalibration)

            Button(
                onClick = { vm.startCalibration() },
                enabled = !state.isCalibrating,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentOk,
                    contentColor = Color(0xFF06140C),
                    disabledContainerColor = AccentOk.copy(alpha = 0.5f),
                    disabledContentColor = Color(0xFF06140C).copy(alpha = 0.5f),
                ),
            ) {
                if (state.isCalibrating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF06140C), strokeWidth = 2.dp)
                    Spacer(Modifier.width(12.dp))
                    Text(s.calibrating, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                } else {
                    Text(s.startCalibration, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            if (state.showSuccess) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentOk.copy(alpha = 0.15f))
                        .border(1.dp, AccentOk.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                ) {
                    Text(s.calibratedSuccess, fontSize = 14.sp, color = AccentOk, fontWeight = FontWeight.SemiBold)
                }
            }

            // ── RĘCZNA KOREKTA OFFSETU ────────────────────────────────────────
            SectionLabel(s.manualCorrection)

            Text(
                text = s.manualCorrectionSub,
                fontSize = 13.sp,
                color = c.text2,
                lineHeight = 19.sp,
            )

            OffsetAdjustRow(
                label = s.pitchAxisLabel,
                value = state.pitchOffset,
                inputText = state.pitchInputText,
                currentLiveAngle = state.livePitch,
                isVertical = true,
                bubbleColor = state.bubbleColor,
                isMetallic = state.isMetallicBubble,
                onDecrement = { vm.decrementPitch() },
                onIncrement = { vm.incrementPitch() },
                onTextChange = { vm.setPitchText(it) },
                onCommit = { vm.commitPitchText(); focusManager.clearFocus() },
            )

            OffsetAdjustRow(
                label = s.rollAxisLabel,
                value = state.rollOffset,
                inputText = state.rollInputText,
                currentLiveAngle = state.liveRoll,
                isVertical = false,
                bubbleColor = state.bubbleColor,
                isMetallic = state.isMetallicBubble,
                onDecrement = { vm.decrementRoll() },
                onIncrement = { vm.incrementRoll() },
                onTextChange = { vm.setRollText(it) },
                onCommit = { vm.commitRollText(); focusManager.clearFocus() },
            )

            // ── KARTA DANYCH ──────────────────────────────────────────────────
            SectionLabel(s.calibrationData)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(c.surface)
                    .border(1.dp, c.line, RoundedCornerShape(16.dp)),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Pitch offset", fontSize = 15.sp, color = c.text2)
                    Text("%+.2f°".format(state.pitchOffset), fontFamily = FontFamily.Monospace, fontSize = 16.sp, color = c.text)
                }
                HorizontalDivider(color = c.line, thickness = 1.dp)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Roll offset", fontSize = 15.sp, color = c.text2)
                    Text("%+.2f°".format(state.rollOffset), fontFamily = FontFamily.Monospace, fontSize = 16.sp, color = c.text)
                }
                HorizontalDivider(color = c.line, thickness = 1.dp)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(s.lastCalibration, fontSize = 15.sp, color = c.text2)
                    Text(
                        text = state.lastCalibrationDate.ifEmpty { "—" },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        color = Color(0xFFC3CCD0),
                    )
                }
            }

            // ── RESET ─────────────────────────────────────────────────────────
            OutlinedButton(
                onClick = { vm.resetCalibration() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3a2a2a)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentError),
            ) {
                Text(s.resetCalibration, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    val c = LocalAppColors.current
    Text(
        text = text,
        fontSize = 13.sp,
        letterSpacing = 1.5.sp,
        fontWeight = FontWeight.SemiBold,
        color = c.text3,
        modifier = Modifier.padding(top = 4.dp),
    )
}

@Composable
private fun InstructionStep(number: String, text: String) {
    val c = LocalAppColors.current
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(AccentOk.copy(alpha = 0.15f))
                .border(1.dp, AccentOk.copy(alpha = 0.4f), RoundedCornerShape(100.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(number, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentOk)
        }
        Text(text, fontSize = 14.sp, color = c.text2, lineHeight = 20.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun OffsetAdjustRow(
    label: String,
    value: Float,
    inputText: String,
    currentLiveAngle: Float,
    isVertical: Boolean,
    bubbleColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.White,
    isMetallic: Boolean = false,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onTextChange: (String) -> Unit,
    onCommit: () -> Unit,
) {
    val c = LocalAppColors.current
    val isDark = c.isDark
    val status = angleStatus(currentLiveAngle)
    val liveColor = when (status) {
        AngleStatus.OK -> if (isDark) AccentOk else AccentOkDark
        AngleStatus.WARN -> AccentWarn
        AngleStatus.ERROR -> AccentError
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(c.surface)
            .border(1.dp, c.line, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Header: label + mini vial + current angle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, fontSize = 13.sp, color = c.text2, fontWeight = FontWeight.Medium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (isVertical) {
                    MiniVerticalVial(pitch = currentLiveAngle, width = 24.dp, height = 54.dp, highContrast = true, bubbleColor = bubbleColor, isMetallic = isMetallic)
                } else {
                    MiniHorizontalVial(roll = currentLiveAngle, width = 62.dp, height = 24.dp, highContrast = true, bubbleColor = bubbleColor, isMetallic = isMetallic)
                }
                Box(modifier = Modifier.width(68.dp)) {
                    Text(
                        text = "%.1f°".format(abs(currentLiveAngle)),
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = liveColor,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
            }
        }

        // Controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedButton(
                onClick = onDecrement,
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentOk),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentOk.copy(alpha = 0.5f)),
            ) {
                Text("−", fontSize = 20.sp, fontWeight = FontWeight.Light)
            }

            OutlinedTextField(
                value = inputText,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                suffix = { Text("°", fontSize = 14.sp, color = c.text2) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.None, autoCorrectEnabled = false, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onCommit() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentOk,
                    unfocusedBorderColor = c.line,
                    focusedTextColor = c.text,
                    unfocusedTextColor = c.text,
                    cursorColor = AccentOk,
                ),
                shape = RoundedCornerShape(10.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = c.text,
                ),
            )

            OutlinedButton(
                onClick = onIncrement,
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentOk),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentOk.copy(alpha = 0.5f)),
            ) {
                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// ── ANIMACJA INSTRUKCJI ────────────────────────────────────────────────────────

@Composable
private fun CalibrationInstructionAnimation() {
    val c = LocalAppColors.current

    val transition = rememberInfiniteTransition(label = "cal_anim")
    val phoneProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3800
                0f at 0 using LinearEasing
                0f at 600 using FastOutSlowInEasing
                1f at 2200 using FastOutSlowInEasing      // phone slides in from right
                1f at 3100 using LinearEasing              // hold next to level
                0f at 3800 using FastOutSlowInEasing      // slides back out
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "phone_progress",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(c.surface)
            .border(1.dp, c.line, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val canvasH = size.height

            // Level centered
            val levelH = 28.dp.toPx()
            val levelTop = canvasH * 0.62f
            drawWoodenLevel(cx, levelTop)

            // Phone edge-on (longer side visible = thin horizontal strip descending from above)
            val edgeW = size.width * 0.72f
            val edgeH = 10.dp.toPx()
            val edgeLeft = cx - edgeW / 2f
            val edgeRestBottom = levelTop  // bottom of phone edge meets top of level
            val edgeStartBottom = -edgeH   // off-screen above
            val edgeBottomY = edgeStartBottom + (edgeRestBottom - edgeStartBottom) * phoneProgress
            val edgeTopY = edgeBottomY - edgeH

            if (edgeBottomY > -edgeH) {
                drawPhoneEdgeOn(edgeLeft, edgeTopY, edgeW, edgeH)
            }

            // Down arrow while phone descending
            if (phoneProgress in 0.08f..0.85f) {
                val arrowAlpha = (1f - kotlin.math.abs(phoneProgress - 0.48f) * 2.2f).coerceIn(0f, 1f)
                val midY = (edgeBottomY + levelTop) / 2f - 4.dp.toPx()
                val headLen = 9.dp.toPx()
                drawLine(
                    color = AccentOk.copy(alpha = arrowAlpha * 0.8f),
                    start = Offset(cx, midY - 10.dp.toPx()),
                    end = Offset(cx, midY + 10.dp.toPx()),
                    strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round,
                )
                drawLine(
                    color = AccentOk.copy(alpha = arrowAlpha * 0.8f),
                    start = Offset(cx, midY + 10.dp.toPx()),
                    end = Offset(cx - headLen * 0.6f, midY + 10.dp.toPx() - headLen * 0.6f),
                    strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round,
                )
                drawLine(
                    color = AccentOk.copy(alpha = arrowAlpha * 0.8f),
                    start = Offset(cx, midY + 10.dp.toPx()),
                    end = Offset(cx + headLen * 0.6f, midY + 10.dp.toPx() - headLen * 0.6f),
                    strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round,
                )
            }
        }
    }
}

private fun DrawScope.drawPhoneEdgeOn(left: Float, top: Float, w: Float, h: Float) {
    val body = Color(0xFF1A2026)
    val border = Color(0xFF2E3C46)
    val screen = Color(0xFF8EA8BA).copy(alpha = 0.3f)  // subtle glass reflection
    val cr = CornerRadius(h / 2f)

    drawRoundRect(body, Offset(left, top), Size(w, h), cr)
    drawRoundRect(border, Offset(left, top), Size(w, h), cr, style = Stroke(1.2.dp.toPx()))

    // Thin glass strip in center — suggests screen edge
    val glassH = h * 0.32f
    val glassTop = top + (h - glassH) / 2f
    drawRoundRect(screen, Offset(left + h, glassTop), Size(w - h * 2f, glassH), CornerRadius(glassH / 2f))

    // Volume buttons (left bump)
    val bH = h * 0.55f
    val bW = 3.5.dp.toPx()
    drawRoundRect(border, Offset(left + w * 0.18f, top - bW + 1.2.dp.toPx()), Size(bH, bW), CornerRadius(1.5.dp.toPx()))
    drawRoundRect(border, Offset(left + w * 0.26f, top - bW + 1.2.dp.toPx()), Size(bH, bW), CornerRadius(1.5.dp.toPx()))

    // Power button (right bump)
    drawRoundRect(border, Offset(left + w * 0.72f, top - bW + 1.2.dp.toPx()), Size(bH * 0.8f, bW), CornerRadius(1.5.dp.toPx()))
}

private fun DrawScope.drawWoodenLevel(cx: Float, levelTop: Float, levelW: Float = size.width * 0.80f) {
    val levelH = 28.dp.toPx()
    val levelLeft = cx - levelW / 2f

    // Wood body
    drawRoundRect(
        color = Color(0xFF8B6914),
        topLeft = Offset(levelLeft, levelTop),
        size = Size(levelW, levelH),
        cornerRadius = CornerRadius(5.dp.toPx()),
    )
    // Wood grain highlight
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.22f), Color.Transparent),
            startY = levelTop,
            endY = levelTop + levelH * 0.4f,
        ),
        topLeft = Offset(levelLeft, levelTop),
        size = Size(levelW, levelH),
        cornerRadius = CornerRadius(5.dp.toPx()),
    )

    // Vial housing (center)
    val vialW = 60.dp.toPx()
    val vialH = 18.dp.toPx()
    val vialLeft = cx - vialW / 2f
    val vialTop = levelTop + (levelH - vialH) / 2f
    drawRoundRect(color = Color(0xFF222222), topLeft = Offset(vialLeft, vialTop), size = Size(vialW, vialH), cornerRadius = CornerRadius(9.dp.toPx()))
    drawRoundRect(
        brush = Brush.horizontalGradient(listOf(Color(0xFFD3EA63), Color(0xFF9CBE38)), startX = vialLeft + 3.dp.toPx(), endX = vialLeft + vialW - 3.dp.toPx()),
        topLeft = Offset(vialLeft + 3.dp.toPx(), vialTop + 3.dp.toPx()),
        size = Size(vialW - 6.dp.toPx(), vialH - 6.dp.toPx()),
        cornerRadius = CornerRadius(6.dp.toPx()),
    )
    // Bubble
    drawCircle(color = Color.White.copy(alpha = 0.9f), radius = 5.dp.toPx(), center = Offset(cx, levelTop + levelH / 2f))
    // Center marks
    val markY1 = vialTop + 3.dp.toPx()
    val markY2 = vialTop + vialH - 3.dp.toPx()
    for (x in listOf(cx - 8.dp.toPx(), cx + 8.dp.toPx())) {
        drawLine(Color(0xFF28370A).copy(alpha = 0.6f), Offset(x, markY1), Offset(x, markY2), 1.5.dp.toPx())
    }
}

private fun DrawScope.drawFlatPhone(phoneLeft: Float, phoneTop: Float, phoneW: Float, phoneH: Float, progress: Float) {
    val bgColor = Color(0xFF181E22)
    val borderColor = Color(0xFF2A343A)
    val screenColor = Color(0xFF0F1316)

    drawRoundRect(bgColor, Offset(phoneLeft, phoneTop), Size(phoneW, phoneH), CornerRadius(7.dp.toPx()))
    drawRoundRect(borderColor, Offset(phoneLeft, phoneTop), Size(phoneW, phoneH), CornerRadius(7.dp.toPx()), style = Stroke(1.5.dp.toPx()))

    val spad = 3.5.dp.toPx()
    val sLeft = phoneLeft + spad + 10.dp.toPx()
    val sRight = phoneLeft + phoneW - spad
    val sTop = phoneTop + spad
    val sBottom = phoneTop + phoneH - spad
    drawRoundRect(screenColor, Offset(sLeft, sTop), Size(sRight - sLeft, sBottom - sTop), CornerRadius(3.dp.toPx()))

    if (progress > 0.4f) {
        val alpha = ((progress - 0.4f) / 0.6f).coerceIn(0f, 1f)
        val scrCx = (sLeft + sRight) / 2f
        val scrCy = (sTop + sBottom) / 2f
        val scrW = sRight - sLeft
        val scrH = sBottom - sTop
        val vialW = scrW * 0.82f
        val vialH = scrH * 0.50f
        val vialLeft = scrCx - vialW / 2f
        val vialTop2 = scrCy - vialH / 2f
        val vr = CornerRadius(vialH / 2f)
        val inset2 = 2.dp.toPx()
        drawRoundRect(Color(0xFF141b1f).copy(alpha = alpha), Offset(vialLeft, vialTop2), Size(vialW, vialH), vr)
        drawRoundRect(Color(0xFF2A343A).copy(alpha = alpha), Offset(vialLeft, vialTop2), Size(vialW, vialH), vr, style = Stroke(1.dp.toPx()))
        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFFD3EA63).copy(alpha = alpha), Color(0xFF9CBE38).copy(alpha = alpha)),
                startY = vialTop2 + inset2, endY = vialTop2 + vialH - inset2,
            ),
            topLeft = Offset(vialLeft + inset2, vialTop2 + inset2),
            size = Size(vialW - inset2 * 2, vialH - inset2 * 2),
            cornerRadius = CornerRadius((vialH / 2f - inset2).coerceAtLeast(1f)),
        )
        val mc = Color(0xFF28370A).copy(alpha = alpha * 0.7f)
        val mg = vialW * 0.12f
        drawLine(mc, Offset(scrCx - mg, vialTop2 + inset2), Offset(scrCx - mg, vialTop2 + vialH - inset2), 1.5.dp.toPx())
        drawLine(mc, Offset(scrCx + mg, vialTop2 + inset2), Offset(scrCx + mg, vialTop2 + vialH - inset2), 1.5.dp.toPx())
        val bubR = (vialH / 2f - inset2 - 1.dp.toPx()).coerceAtLeast(2.dp.toPx())
        drawCircle(Color.White.copy(alpha = alpha * 0.92f), bubR, Offset(scrCx, scrCy))
    }
    // Camera notch bump
    val bumpW = 5.dp.toPx()
    val bumpH = phoneH * 0.25f
    drawRoundRect(borderColor, Offset(phoneLeft - bumpW + 1.dp.toPx(), phoneTop + phoneH * 0.37f), Size(bumpW, bumpH), CornerRadius(3.dp.toPx()))
}

private fun DrawScope.drawLandscapePhone(cx: Float, bottomY: Float, phoneW: Float, phoneH: Float, progress: Float) {
    val phoneLeft = cx - phoneW / 2f
    val phoneTop = bottomY - phoneH

    val bgColor = Color(0xFF181E22)
    val borderColor = Color(0xFF2A343A)
    val screenColor = Color(0xFF0F1316)

    // Phone body
    drawRoundRect(bgColor, Offset(phoneLeft, phoneTop), Size(phoneW, phoneH), CornerRadius(7.dp.toPx()))
    drawRoundRect(borderColor, Offset(phoneLeft, phoneTop), Size(phoneW, phoneH), CornerRadius(7.dp.toPx()), style = Stroke(1.5.dp.toPx()))

    // Screen
    val spad = 4.dp.toPx()
    val sLeft = phoneLeft + spad + 12.dp.toPx()   // extra left pad for landscape camera notch
    val sRight = phoneLeft + phoneW - spad
    val sTop = phoneTop + spad
    val sBottom = bottomY - spad
    drawRoundRect(screenColor, Offset(sLeft, sTop), Size(sRight - sLeft, sBottom - sTop), CornerRadius(4.dp.toPx()))

    // Mini horizontal vial on screen (when phone near level)
    if (progress > 0.35f) {
        val alpha = ((progress - 0.35f) / 0.65f).coerceIn(0f, 1f)
        val scrCx = (sLeft + sRight) / 2f
        val scrCy = (sTop + sBottom) / 2f
        val scrW = sRight - sLeft
        val scrH = sBottom - sTop
        val vialW = scrW * 0.82f
        val vialH = scrH * 0.48f
        val vialLeft = scrCx - vialW / 2f
        val vialTop2 = scrCy - vialH / 2f
        val vr = CornerRadius(vialH / 2f)
        val inset2 = 2.dp.toPx()
        // Vial outer
        drawRoundRect(Color(0xFF141b1f).copy(alpha = alpha), Offset(vialLeft, vialTop2), Size(vialW, vialH), vr)
        drawRoundRect(Color(0xFF2A343A).copy(alpha = alpha), Offset(vialLeft, vialTop2), Size(vialW, vialH), vr, style = Stroke(1.dp.toPx()))
        // Green liquid
        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFFD3EA63).copy(alpha = alpha), Color(0xFF9CBE38).copy(alpha = alpha)),
                startY = vialTop2 + inset2, endY = vialTop2 + vialH - inset2,
            ),
            topLeft = Offset(vialLeft + inset2, vialTop2 + inset2),
            size = Size(vialW - inset2 * 2, vialH - inset2 * 2),
            cornerRadius = CornerRadius((vialH / 2f - inset2).coerceAtLeast(1f)),
        )
        // Center marks
        val mc = Color(0xFF28370A).copy(alpha = alpha * 0.7f)
        val mg = vialW * 0.11f
        drawLine(mc, Offset(scrCx - mg, vialTop2 + inset2), Offset(scrCx - mg, vialTop2 + vialH - inset2), 1.5.dp.toPx())
        drawLine(mc, Offset(scrCx + mg, vialTop2 + inset2), Offset(scrCx + mg, vialTop2 + vialH - inset2), 1.5.dp.toPx())
        // Bubble
        val bubR = (vialH / 2f - inset2 - 1.dp.toPx()).coerceAtLeast(2.dp.toPx())
        drawCircle(Color.White.copy(alpha = alpha * 0.92f), bubR, Offset(scrCx, scrCy))
    }

    // Camera button bump (right side)
    val bumpW = 6.dp.toPx()
    val bumpH = phoneH * 0.22f
    val bumpLeft = phoneLeft + phoneW - 1.dp.toPx()
    val bumpTop = phoneTop + phoneH * 0.6f
    drawRoundRect(borderColor, Offset(bumpLeft, bumpTop), Size(bumpW, bumpH), CornerRadius(3.dp.toPx()))
}
