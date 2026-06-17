package com.truebubble.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truebubble.ui.strings.LocalAppStrings
import com.truebubble.ui.theme.AccentOk
import com.truebubble.ui.theme.AccentOkDark
import com.truebubble.ui.theme.LocalAppColors

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onCalibration: () -> Unit,
    vm: SettingsViewModel = viewModel(),
) {
    val settings by vm.settings.collectAsStateWithLifecycle()
    val c = LocalAppColors.current
    val s = LocalAppStrings.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(c.bg)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 22.dp, top = 20.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Wstecz", tint = c.text)
            }
            Text(s.settingsTitle, fontSize = 21.sp, fontWeight = FontWeight.SemiBold, color = c.text)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // WYGLĄD
            SectionHeader(s.appearance)

            // Theme segmented control
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(s.theme, fontSize = 16.sp, color = c.text)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(c.surface)
                        .border(1.dp, c.line, RoundedCornerShape(100.dp))
                        .padding(3.dp),
                ) {
                    ThemeSegment(s.dark, settings.darkTheme) { vm.setDarkTheme(true) }
                    ThemeSegment(s.light, !settings.darkTheme) { vm.setDarkTheme(false) }
                }
            }

            SwitchRow(
                title = s.highContrastLabel,
                subtitle = s.highContrastSub,
                checked = settings.highContrastBubble,
                onCheckedChange = { vm.setHighContrast(it) },
            )

            // Bubble color picker
            val bubbleColors = listOf(
                Color.White, Color(0xFFFF7A00), Color(0xFFFFD600), Color(0xFF00CCFF), Color(0xFFFF4444),
                Color(0xFF1C1C1E), Color(0xFF9B30FF),
            )
            val accent = if (c.isDark) AccentOk else AccentOkDark
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(s.bubbleColorLabel, fontSize = 16.sp, color = c.text)
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    bubbleColors.forEachIndexed { idx, color ->
                        val selected = settings.bubbleColorIndex == idx
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(color)
                                .border(
                                    width = if (selected) 2.5.dp else 1.dp,
                                    color = if (selected) accent else c.line,
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                )
                                .clickable { vm.setBubbleColor(idx) },
                        )
                    }
                }
            }

            SectionDivider()

            // SYGNAŁ POZIOMU
            SectionHeader(s.levelSignal)

            SwitchRow(
                title = s.soundOnLevel,
                subtitle = s.soundOnLevelSub,
                checked = settings.soundOnLevel,
                onCheckedChange = { vm.setSound(it) },
            )

            SwitchRow(
                title = s.vibrateOnLevel,
                subtitle = s.vibrateOnLevelSub,
                checked = settings.vibrateOnLevel,
                onCheckedChange = { vm.setVibrate(it) },
            )

            SectionDivider()

            // CZUJNIKI
            SectionHeader(s.sensors)

            val accentBtn = if (c.isDark) AccentOk else AccentOkDark
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accentBtn.copy(alpha = 0.12f))
                    .border(1.dp, accentBtn.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
                    .clickable { onCalibration() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = s.calibrateSensors,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accentBtn,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    val c = LocalAppColors.current
    Text(
        text = title,
        modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 6.dp),
        fontSize = 12.sp,
        letterSpacing = 1.5.sp,
        fontWeight = FontWeight.SemiBold,
        color = c.text3,
    )
}

@Composable
private fun SectionDivider() {
    val c = LocalAppColors.current
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        color = c.line,
        thickness = 1.dp,
    )
}

@Composable
private fun ThemeSegment(label: String, active: Boolean, onClick: () -> Unit) {
    val c = LocalAppColors.current
    val accent = if (c.isDark) AccentOk else AccentOkDark
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (active) accent else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
            color = if (active) Color(0xFF06140C) else c.text2,
        )
    }
}

@Composable
private fun SwitchRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val c = LocalAppColors.current
    val accent = if (c.isDark) AccentOk else AccentOkDark
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, color = c.text)
            Text(subtitle, fontSize = 13.sp, color = c.text2, modifier = Modifier.padding(top = 2.dp))
        }
        // Custom toggle
        Box(
            modifier = Modifier
                .size(50.dp, 30.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(if (checked) accent else c.line)
                .clickable { onCheckedChange(!checked) },
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .padding(3.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (checked) Color(0xFF06140C) else c.bg),
            )
        }
    }
}

@Composable
private fun ChevronRow(title: String, subtitle: String, onClick: () -> Unit) {
    val c = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, color = c.text)
            Text(subtitle, fontSize = 13.sp, color = c.text2, modifier = Modifier.padding(top = 2.dp))
        }
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = c.text3, modifier = Modifier.size(22.dp))
    }
}
