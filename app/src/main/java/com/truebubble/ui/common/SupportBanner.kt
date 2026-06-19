package com.truebubble.ui.common

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truebubble.ui.strings.LocalAppStrings
import com.truebubble.ui.theme.AccentOk
import com.truebubble.ui.theme.AccentOkDark
import com.truebubble.ui.theme.LocalAppColors

@Composable
fun SupportTopBarButton(
    expanded: Boolean,
    onClick: () -> Unit,
) {
    val c = LocalAppColors.current
    val s = LocalAppStrings.current
    val accent = if (c.isDark) AccentOk else AccentOkDark

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (expanded) accent.copy(alpha = 0.12f) else accent.copy(alpha = 0.07f))
            .border(
                width = 1.dp,
                color = if (expanded) accent.copy(alpha = 0.50f) else accent.copy(alpha = 0.28f),
                shape = RoundedCornerShape(10.dp),
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Favorite,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(15.dp),
        )
        Text(
            text = s.supportTitle,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = accent,
            maxLines = 1,
        )
    }
}

@Composable
fun SupportTileContent() {
    val c = LocalAppColors.current
    val s = LocalAppStrings.current
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val accent = if (c.isDark) AccentOk else AccentOkDark
    val website = "spotrobotics.app"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(c.surface)
            .border(1.dp, accent.copy(alpha = 0.22f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = s.supportText,
            fontSize = 12.sp,
            color = c.text2,
            lineHeight = 18.sp,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(c.bg)
                .border(1.dp, c.line, RoundedCornerShape(8.dp))
                .clickable { uriHandler.openUri("https://spotrobotics.app") }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = website,
                fontSize = 14.sp,
                color = accent,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("website", website))
                    Toast.makeText(context, s.copiedToClipboard, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    Icons.Outlined.ContentCopy,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
fun SupportOverlay(supportState: MutableTransitionState<Boolean>) {
    AnimatedVisibility(
        visibleState = supportState,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        SupportTileContent()
    }
}
