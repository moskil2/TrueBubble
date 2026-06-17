package com.truebubble.ui.menu

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.truebubble.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truebubble.BuildConfig
import com.truebubble.ui.strings.LocalAppStrings
import com.truebubble.ui.theme.AccentOk
import com.truebubble.ui.theme.AccentOkDark
import com.truebubble.ui.theme.LocalAppColors

@Composable
fun MenuScreen(onBack: () -> Unit) {
    val c = LocalAppColors.current
    val s = LocalAppStrings.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) { scrollState.scrollTo(0) }

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
            Text(s.menuTitle, fontSize = 21.sp, fontWeight = FontWeight.SemiBold, color = c.text)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            // ── App header ───────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = "TrueBubble",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(104.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .border(1.dp, c.line, RoundedCornerShape(22.dp)),
                )

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "TrueBubble",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.text,
                    )
                    Text(
                        text = "V${BuildConfig.VERSION_NAME}",
                        fontSize = 13.sp,
                        color = c.text2,
                    )
                    Text(
                        text = "Build ${BuildConfig.BUILD_TIME}",
                        fontSize = 11.sp,
                        color = c.text3,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(c.surface)
                        .border(1.dp, c.line, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = "Created by Tomasz Pieczara",
                        fontSize = 12.sp,
                        color = c.text2,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            HorizontalDivider(color = c.line, thickness = 1.dp, modifier = Modifier.padding(bottom = 12.dp))

            // ── Accordion tiles ──────────────────────────────────────────────

            AccordionTile(icon = Icons.Outlined.Favorite, title = s.supportTitle) {
                Text(
                    text = s.supportText,
                    fontSize = 13.sp,
                    color = c.text2,
                    lineHeight = 20.sp,
                )
            }

            AccordionTile(icon = Icons.Outlined.Security, title = s.securityTitle) {
                Text(
                    text = s.securityText,
                    fontSize = 13.sp,
                    color = c.text2,
                    lineHeight = 20.sp,
                )
            }

            AccordionTile(icon = Icons.Outlined.Policy, title = s.gdprTitle) {
                Text(
                    text = s.gdprText,
                    fontSize = 13.sp,
                    color = c.text2,
                    lineHeight = 20.sp,
                )
            }

            AccordionTile(icon = Icons.Outlined.Email, title = s.contactTitle) {
                val email = "tomasz.pieczara@gazeta.pl"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(c.bg)
                        .border(1.dp, c.line, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(email, fontSize = 13.sp, color = c.text, modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("email", email))
                            Toast.makeText(context, s.copiedToClipboard, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(36.dp),
                    ) {
                        Icon(Icons.Outlined.ContentCopy, contentDescription = null, tint = AccentOk, modifier = Modifier.size(18.dp))
                    }
                }
            }

            AccordionTile(icon = Icons.Outlined.PhoneAndroid, title = s.compatTitle) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CompatRow("Android 8.0 – 8.1", "API 26–27", true)
                    CompatRow("Android 9.0 Pie", "API 28", true)
                    CompatRow("Android 10", "API 29", true)
                    CompatRow("Android 11", "API 30", true)
                    CompatRow("Android 12 – 12L", "API 31–32", true)
                    CompatRow("Android 13", "API 33", true)
                    CompatRow("Android 14", "API 34", true)
                    CompatRow("Android 15", "API 35", true)
                    CompatRow("Android 16", "API 36", true)
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AccordionTile(
    icon: ImageVector,
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    val c = LocalAppColors.current
    var expanded by remember { mutableStateOf(false) }
    val accent = if (c.isDark) AccentOk else AccentOkDark

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(c.surface)
                .border(1.dp, if (expanded) accent.copy(alpha = 0.4f) else c.line, RoundedCornerShape(12.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.text, modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                contentDescription = null,
                tint = c.text3,
                modifier = Modifier.size(20.dp),
            )
        }
        AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                    .background(c.surface.copy(alpha = 0.6f))
                    .border(1.dp, accent.copy(alpha = 0.25f), RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = content,
            )
        }
    }
}

@Composable
private fun CompatRow(androidName: String, apiLevel: String, compatible: Boolean) {
    val c = LocalAppColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(androidName, fontSize = 13.sp, color = c.text2)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(apiLevel, fontSize = 12.sp, color = c.text3)
            Icon(
                Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = if (compatible) AccentOk else c.text3,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
