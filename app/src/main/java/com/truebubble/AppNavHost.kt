package com.truebubble

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truebubble.ui.calibration.CalibrationScreen
import com.truebubble.ui.circlelevel.CircleLevelScreen
import com.truebubble.ui.level.LevelScreen
import com.truebubble.ui.menu.MenuScreen
import com.truebubble.ui.settings.SettingsScreen
import com.truebubble.ui.settings.SettingsViewModel
import com.truebubble.ui.strings.LocalAppStrings
import com.truebubble.ui.strings.stringsFor
import com.truebubble.ui.theme.AccentOk
import com.truebubble.ui.theme.AccentOkDark
import com.truebubble.ui.theme.LocalAppColors

sealed class Screen(val route: String) {
    object Level : Screen("level")
    object CircleLevel : Screen("circle_level")
    object Settings : Screen("settings")
    object Calibration : Screen("calibration")
    object Menu : Screen("menu")
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val c = LocalAppColors.current
    val settingsVm: SettingsViewModel = viewModel()
    val settings by settingsVm.settings.collectAsStateWithLifecycle()

    CompositionLocalProvider(LocalAppStrings provides stringsFor(settings.languageCode)) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            NavHost(navController = navController, startDestination = Screen.Level.route) {
                composable(Screen.Level.route) {
                    LevelScreen(
                        onSettingsClick = { navController.navigate(Screen.Settings.route) },
                        onMenuClick = { navController.navigate(Screen.Menu.route) },
                        onLanguageChange = { settingsVm.setLanguage(it) },
                    )
                }
                composable(Screen.CircleLevel.route) {
                    CircleLevelScreen(
                        onSettingsClick = { navController.navigate(Screen.Settings.route) },
                        onMenuClick = { navController.navigate(Screen.Menu.route) },
                        onLanguageChange = { settingsVm.setLanguage(it) },
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        onBack = { navController.popBackStack() },
                        onCalibration = { navController.navigate(Screen.Calibration.route) },
                    )
                }
                composable(Screen.Calibration.route) {
                    CalibrationScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.Menu.route) {
                    MenuScreen(onBack = { navController.popBackStack() })
                }
            }
        }

        // Bottom navigation — tylko na głównych ekranach
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        if (currentRoute == Screen.Level.route || currentRoute == Screen.CircleLevel.route) {
            BottomNav(
                currentRoute = currentRoute,
                onLevel = {
                    if (currentRoute != Screen.Level.route)
                        navController.navigate(Screen.Level.route) {
                            popUpTo(Screen.Level.route) { inclusive = true }
                        }
                },
                onCircleLevel = {
                    if (currentRoute != Screen.CircleLevel.route)
                        navController.navigate(Screen.CircleLevel.route) {
                            popUpTo(Screen.Level.route)
                        }
                },
            )
        }
    }
    } // CompositionLocalProvider
}

@Composable
private fun BottomNav(
    currentRoute: String?,
    onLevel: () -> Unit,
    onCircleLevel: () -> Unit,
) {
    val c = LocalAppColors.current
    val s = LocalAppStrings.current
    val accent = if (c.isDark) AccentOk else AccentOkDark

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(c.navBg)
            .border(width = 1.dp, color = c.line, shape = RoundedCornerShape(0.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // ── Poziomica (flat vial icon) ────────────────────────────────────
        BottomNavItem(
            label = s.poziomica,
            active = currentRoute == Screen.Level.route,
            onClick = onLevel,
            modifier = Modifier.weight(1f),
        ) {
            Canvas(modifier = Modifier.size(32.dp)) {
                val active = currentRoute == Screen.Level.route
                val color = if (active) accent else c.text3
                val vialW = size.width * 0.88f
                val vialH = size.height * 0.38f
                val vialLeft = (size.width - vialW) / 2f
                val vialTop = (size.height - vialH) / 2f
                val r = vialH / 2f

                drawRoundRect(color = color.copy(alpha = 0.25f), topLeft = Offset(vialLeft, vialTop), size = Size(vialW, vialH), cornerRadius = CornerRadius(r))
                drawRoundRect(color = color, topLeft = Offset(vialLeft, vialTop), size = Size(vialW, vialH), cornerRadius = CornerRadius(r), style = Stroke(width = 1.8.dp.toPx()))
                val cx = size.width / 2f
                val markH = vialH - 4.dp.toPx()
                drawLine(color, Offset(cx - 6.dp.toPx(), vialTop + (vialH - markH) / 2f), Offset(cx - 6.dp.toPx(), vialTop + (vialH + markH) / 2f), 1.5.dp.toPx())
                drawLine(color, Offset(cx + 6.dp.toPx(), vialTop + (vialH - markH) / 2f), Offset(cx + 6.dp.toPx(), vialTop + (vialH + markH) / 2f), 1.5.dp.toPx())
                drawCircle(color = color, radius = 4.5.dp.toPx(), center = Offset(cx, size.height / 2f))
            }
        }

        // ── Libella (circle+dot icon) ─────────────────────────────────────
        BottomNavItem(
            label = s.libella,
            active = currentRoute == Screen.CircleLevel.route,
            onClick = onCircleLevel,
            modifier = Modifier.weight(1f),
        ) {
            Canvas(modifier = Modifier.size(32.dp)) {
                val active = currentRoute == Screen.CircleLevel.route
                val color = if (active) accent else c.text3
                val cx = size.width / 2f
                val cy = size.height / 2f
                drawCircle(color = color, radius = size.width * 0.33f, center = Offset(cx, cy), style = Stroke(width = 2.dp.toPx()))
                drawCircle(color = color, radius = 3.dp.toPx(), center = Offset(cx, cy))
            }
        }

    }
}

@Composable
private fun BottomNavItem(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    val c = LocalAppColors.current
    val accent = if (c.isDark) AccentOk else AccentOkDark

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (active) accent.copy(alpha = 0.12f) else c.surface)
            .border(1.dp, if (active) accent.copy(alpha = 0.55f) else c.line, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        icon()
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
            color = if (active) accent else c.text3,
        )
    }
}
