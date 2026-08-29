package com.portee.app.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.portee.app.data.Recording
import com.portee.app.ui.components.RecordingRow
import com.portee.app.ui.theme.PorteeColors
import com.portee.app.ui.theme.PorteeType
import com.portee.app.ui.theme.Spacing

@Composable
fun RecordScreen(
    active: Boolean,
    elapsedLabel: String,
    recordings: List<Recording>,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.space6),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.space6),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.space4),
        ) {
            RecordButton(active = active, onClick = onToggle)
            Text(
                elapsedLabel,
                style = PorteeType.mono.copy(fontFamily = FontFamily.Monospace, fontSize = 20.sp),
                color = PorteeColors.text,
            )
            Text(
                if (active) "Enregistrement en cours…" else "Appuie pour enregistrer ta prise",
                style = PorteeType.bodySmall.copy(fontSize = 12.5.sp),
                color = PorteeColors.text.copy(alpha = 0.55f),
            )
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "REPRISES",
                style = PorteeType.meta,
                color = PorteeColors.text.copy(alpha = 0.55f),
                modifier = Modifier.padding(bottom = 10.dp),
            )
            if (recordings.isEmpty()) {
                Text(
                    "Aucune prise pour l'instant — lance ton premier enregistrement.",
                    style = PorteeType.bodySmall,
                    color = PorteeColors.text.copy(alpha = 0.5f),
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.space3)) {
                    recordings.forEach { recording -> RecordingRow(recording = recording) }
                }
            }
        }
    }
}

@Composable
private fun RecordButton(active: Boolean, onClick: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "recordPulse")
    val ringScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (active) 1.45f else 1f,
        animationSpec = infiniteRepeatable(tween(1300, easing = LinearEasing), RepeatMode.Restart),
        label = "ringScale",
    )
    Box(contentAlignment = Alignment.Center) {
        if (active) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .scale(ringScale)
                    .clip(CircleShape)
                    .background(PorteeColors.recordRed.copy(alpha = (1f - (ringScale - 1f) / 0.45f).coerceIn(0f, 0.45f))),
            )
        }
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(PorteeColors.surface)
                .border(1.dp, PorteeColors.divider, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (active) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(PorteeColors.recordRed),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(PorteeColors.recordRed),
                )
            }
        }
    }
}
