package com.portee.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.portee.app.BuildConfig
import com.portee.app.data.MockData
import com.portee.app.ui.components.BackHeader
import com.portee.app.ui.components.PorteeTabBar
import com.portee.app.ui.components.RootTab
import com.portee.app.ui.components.UpdateDialog
import com.portee.app.ui.screens.AddScreen
import com.portee.app.ui.screens.DetailScreen
import com.portee.app.ui.screens.LibraryScreen
import com.portee.app.ui.screens.PracticeScreen
import com.portee.app.ui.screens.RecordScreen
import com.portee.app.ui.screens.SuggestionsScreen
import com.portee.app.ui.theme.PorteeColors
import com.portee.app.ui.theme.PorteeType
import com.portee.app.ui.theme.Spacing

@Composable
fun PorteeApp(viewModel: PorteeViewModel = viewModel()) {
    val screen = viewModel.screen
    val context = LocalContext.current
    val activity = context.findActivity()

    DisposableEffect(screen is Screen.Practice) {
        val window = activity?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, it.decorView) }
        if (screen is Screen.Practice) {
            controller?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller?.hide(WindowInsetsCompat.Type.navigationBars())
        } else {
            controller?.show(WindowInsetsCompat.Type.navigationBars())
        }
        onDispose {
            controller?.show(WindowInsetsCompat.Type.navigationBars())
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkForUpdate(BuildConfig.VERSION_CODE)
    }

    viewModel.updateInfo?.let { info ->
        UpdateDialog(info = info, onDismiss = viewModel::dismissUpdate)
    }

    Surface(color = PorteeColors.background, modifier = Modifier.fillMaxSize()) {
        when (val current = screen) {
            is Screen.Practice -> {
                val piece = viewModel.pieceById(current.pieceId)
                PracticeScreen(
                    imageUris = piece?.scoreImageUris ?: emptyList(),
                    currentPage = viewModel.practicePage,
                    totalPages = piece?.pages ?: 1,
                    onPageChange = viewModel::jumpToPracticePage,
                    onClose = viewModel::goBack,
                )
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                    when (current) {
                        Screen.Library, Screen.Add, Screen.Suggestions -> RootHeader(
                            current = current,
                            pieceCount = viewModel.pieces.size,
                            modifier = Modifier.padding(horizontal = Spacing.space4),
                        )
                        is Screen.Detail -> {
                            val piece = viewModel.pieceById(current.pieceId)
                            BackHeader(
                                title = piece?.title ?: "",
                                onBack = viewModel::goBack,
                                modifier = Modifier.padding(horizontal = Spacing.space4),
                            )
                        }
                        is Screen.Record -> {
                            val piece = viewModel.pieceById(current.pieceId)
                            BackHeader(
                                title = "${piece?.title.orEmpty()} · Enregistrement",
                                onBack = viewModel::goBack,
                                modifier = Modifier.padding(horizontal = Spacing.space4),
                            )
                        }
                        is Screen.Practice -> {}
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.space4, vertical = Spacing.space3),
                    ) {
                        when (current) {
                            Screen.Library -> LibraryScreen(
                                pieces = viewModel.filteredPieces,
                                searchQuery = viewModel.searchQuery,
                                onSearchChange = viewModel::onSearchChange,
                                onOpenPiece = viewModel::openPiece,
                            )
                            Screen.Add -> AddScreen(
                                form = viewModel.addForm,
                                onTitleChange = viewModel::updateTitle,
                                onComposerChange = viewModel::updateComposer,
                                onPickPdf = viewModel::pickPdfImport,
                                onPhotoTaken = viewModel::addPhoto,
                                onRemovePhoto = viewModel::removePhoto,
                                onPickLevel = viewModel::pickLevel,
                                onSubmit = { viewModel.submitAdd(context.applicationContext) },
                                isSubmitting = viewModel.isSubmittingPiece,
                            )
                            Screen.Suggestions -> SuggestionsScreen(
                                suggestions = viewModel.suggestions,
                                addedIds = viewModel.addedSuggestions,
                                onAdd = viewModel::addSuggestion,
                            )
                            is Screen.Detail -> {
                                val piece = viewModel.pieceById(current.pieceId)
                                if (piece != null) {
                                    DetailScreen(
                                        piece = piece,
                                        onPlay = { viewModel.startPractice(piece.id) },
                                        onRecord = { viewModel.startRecord(piece.id) },
                                        onSeeSuggestions = { viewModel.setTab(Screen.Suggestions) },
                                    )
                                }
                            }
                            is Screen.Record -> {
                                val piece = viewModel.pieceById(current.pieceId)
                                RecordScreen(
                                    active = viewModel.recordActive,
                                    elapsedLabel = MockData.fmtTime(viewModel.recordElapsed),
                                    recordings = piece?.recordings ?: emptyList(),
                                    onToggle = viewModel::toggleRecord,
                                )
                            }
                            is Screen.Practice -> {}
                        }
                    }

                    if (current == Screen.Library || current == Screen.Add || current == Screen.Suggestions) {
                        PorteeTabBar(
                            selected = when (current) {
                                Screen.Add -> RootTab.Add
                                Screen.Suggestions -> RootTab.Suggestions
                                else -> RootTab.Library
                            },
                            onSelect = { tab ->
                                viewModel.setTab(
                                    when (tab) {
                                        RootTab.Library -> Screen.Library
                                        RootTab.Add -> Screen.Add
                                        RootTab.Suggestions -> Screen.Suggestions
                                    },
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RootHeader(current: Screen, pieceCount: Int, modifier: Modifier = Modifier) {
    val kicker = when (current) {
        Screen.Library -> if (pieceCount == 1) "1 MORCEAU" else "$pieceCount MORCEAUX"
        Screen.Add -> "NOUVEAU MORCEAU"
        Screen.Suggestions -> "SUGGESTIONS"
        else -> ""
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Spacing.space6, bottom = Spacing.space3),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text("Portée", style = PorteeType.appTitle, color = PorteeColors.text)
        Text(kicker, style = PorteeType.label, color = PorteeColors.text.copy(alpha = 0.5f))
    }
}
