package com.portee.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.portee.app.camera.rememberDecodedBitmap
import com.portee.app.ui.components.ScorePlaceholder
import com.portee.app.ui.components.SecondaryButton
import com.portee.app.ui.theme.PorteeColors
import com.portee.app.ui.theme.PorteeType
import com.portee.app.ui.theme.Spacing

@Composable
fun PracticeScreen(
    imageUris: List<String>,
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var zoomed by remember { mutableStateOf(false) }
    val currentImageUri = imageUris.getOrNull(currentPage)

    val pagerState = rememberPagerState(initialPage = currentPage) { totalPages }

    LaunchedEffect(currentPage) {
        if (pagerState.currentPage != currentPage) {
            pagerState.scrollToPage(currentPage)
        }
    }
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != currentPage) {
            onPageChange(pagerState.currentPage)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(PorteeColors.background)) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f).fillMaxWidth().padding(Spacing.space3),
            ) { page ->
                val pageImageUri = imageUris.getOrNull(page)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, PorteeColors.divider, RoundedCornerShape(8.dp))
                        .clickable(
                            enabled = pageImageUri != null,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { zoomed = true },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    ScoreStage(imageUri = pageImageUri, pages = totalPages, contentScale = ContentScale.Fit)
                }
            }

            Text(
                "${currentPage + 1} / $totalPages",
                style = PorteeType.mono,
                color = PorteeColors.text.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = Spacing.space2),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.space4, vertical = Spacing.space4),
                horizontalArrangement = Arrangement.spacedBy(Spacing.space3),
            ) {
                SecondaryButton(
                    text = "◀ Précédent",
                    onClick = { onPageChange(currentPage - 1) },
                    enabled = currentPage > 0,
                    modifier = Modifier.weight(1f),
                )
                SecondaryButton(
                    text = "Fermer",
                    onClick = onClose,
                    modifier = Modifier.weight(1f),
                )
                SecondaryButton(
                    text = "Suivant ▶",
                    onClick = { onPageChange(currentPage + 1) },
                    enabled = currentPage < totalPages - 1,
                    modifier = Modifier.weight(1f),
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
                    pages = totalPages,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(Spacing.space4),
                )
            }
        }
    }
}

@Composable
private fun ScoreStage(
    imageUri: String?,
    pages: Int,
    contentScale: ContentScale,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    if (imageUri == null) {
        ScorePlaceholder(pages = pages, modifier = modifier)
        return
    }
    val bitmap = rememberDecodedBitmap(imageUri, 1600)
    if (bitmap != null) {
        Image(bitmap = bitmap, contentDescription = null, contentScale = contentScale, modifier = modifier)
    } else {
        ScorePlaceholder(pages = pages, modifier = modifier)
    }
}
