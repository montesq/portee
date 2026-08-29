package com.portee.app.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.portee.app.camera.rememberDecodedBitmap
import com.portee.app.ui.components.DiagonalStripes
import com.portee.app.ui.components.IconOnlyButton
import com.portee.app.ui.components.PrimaryButton
import com.portee.app.ui.components.SecondaryButton
import com.portee.app.ui.icons.PorteeIcons
import com.portee.app.ui.theme.PorteeColors
import com.portee.app.ui.theme.PorteeType
import com.portee.app.ui.theme.Spacing

@Composable
fun PracticeScreen(
    imageUris: List<String>,
    currentPage: Int,
    totalPages: Int,
    listening: Boolean,
    done: Boolean,
    onBack: () -> Unit,
    onToggleListening: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var zoomed by remember { mutableStateOf(false) }
    val currentImageUri = imageUris.getOrNull(currentPage)

    Box(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize().background(PorteeColors.background)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(Spacing.space3)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, if (listening) PorteeColors.accent else PorteeColors.divider, RoundedCornerShape(8.dp))
                    .clickable(
                        enabled = currentImageUri != null,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { zoomed = true },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                ScoreStage(imageUri = currentImageUri, contentScale = ContentScale.Fit)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "PAGE",
                        style = PorteeType.mono.copy(fontSize = 11.sp, letterSpacing = 0.6.sp),
                        color = PorteeColors.text.copy(alpha = 0.6f),
                    )
                    Text(
                        "${currentPage + 1} / $totalPages",
                        style = PorteeType.mono.copy(fontFamily = FontFamily.Monospace, fontSize = 44.sp),
                        color = PorteeColors.text,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                    Row(
                        modifier = Modifier.width(180.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        repeat(totalPages) { i ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (i <= currentPage) PorteeColors.accent else PorteeColors.neutral800),
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .width(150.dp)
                    .fillMaxHeight()
                    .padding(top = Spacing.space3, bottom = Spacing.space3, end = Spacing.space3),
                verticalArrangement = Arrangement.Center,
            ) {
                IconOnlyButton(icon = PorteeIcons.ArrowRight, contentDescription = "Retour", onClick = onBack)

                Box(modifier = Modifier.height(Spacing.space3))

                if (listening) {
                    ListeningIndicator()
                    Box(modifier = Modifier.height(Spacing.space3))
                } else if (done) {
                    Text("Terminé.", style = PorteeType.label, color = PorteeColors.text.copy(alpha = 0.6f))
                    Box(modifier = Modifier.height(Spacing.space3))
                }

                PrimaryButton(
                    text = if (listening) "Mettre en pause" else if (done) "Terminé" else "Démarrer",
                    onClick = onToggleListening,
                    enabled = !done,
                    fullWidth = true,
                )
                Box(modifier = Modifier.height(Spacing.space3))
                SecondaryButton(text = "Recommencer", onClick = onReset, modifier = Modifier.fillMaxWidth())
                Box(modifier = Modifier.height(Spacing.space3))
                Text(
                    "L'app écoute ton jeu et tourne la page au bon moment.",
                    style = PorteeType.meta.copy(fontSize = 10.5.sp),
                    color = PorteeColors.text.copy(alpha = 0.5f),
                )
            }
        }

        if (zoomed && currentImageUri != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PorteeColors.background)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { zoomed = false },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                ScoreStage(
                    imageUri = currentImageUri,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(Spacing.space4),
                )
            }
        }
    }
}

@Composable
private fun ScoreStage(imageUri: String?, contentScale: ContentScale, modifier: Modifier = Modifier.fillMaxSize()) {
    if (imageUri == null) {
        DiagonalStripes(modifier = modifier)
        return
    }
    val bitmap = rememberDecodedBitmap(imageUri, 1600)
    if (bitmap != null) {
        Image(bitmap = bitmap, contentDescription = null, contentScale = contentScale, modifier = modifier)
    } else {
        DiagonalStripes(modifier = modifier)
    }
}

@Composable
private fun ListeningIndicator() {
    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(550, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulseScale",
    )
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(PorteeColors.accent),
        )
        Text("Écoute en cours", style = PorteeType.label, color = PorteeColors.accent300)
    }
}
