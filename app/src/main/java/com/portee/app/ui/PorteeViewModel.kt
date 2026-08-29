package com.portee.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portee.app.data.AddForm
import com.portee.app.data.ImportKind
import com.portee.app.data.MockData
import com.portee.app.data.Piece
import com.portee.app.data.Recording
import com.portee.app.data.Suggestion
import com.portee.app.update.UpdateChecker
import com.portee.app.update.UpdateInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

sealed class Screen {
    data object Library : Screen()
    data object Add : Screen()
    data object Suggestions : Screen()
    data class Detail(val pieceId: String) : Screen()
    data class Practice(val pieceId: String) : Screen()
    data class Record(val pieceId: String) : Screen()
}

class PorteeViewModel : ViewModel() {

    var pieces by mutableStateOf(emptyList<Piece>())
        private set

    var screen by mutableStateOf<Screen>(Screen.Library)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var addForm by mutableStateOf(AddForm())
        private set

    var addedSuggestions by mutableStateOf(setOf<String>())
        private set

    var practicePage by mutableStateOf(0)
        private set

    var recordActive by mutableStateOf(false)
        private set
    var recordElapsed by mutableStateOf(0)
        private set

    var updateInfo by mutableStateOf<UpdateInfo?>(null)
        private set

    private var recordJob: Job? = null

    val suggestions: List<Suggestion> get() = MockData.suggestions

    fun checkForUpdate(currentVersionCode: Int) {
        viewModelScope.launch {
            val latest = withContext(Dispatchers.IO) { UpdateChecker.fetchLatest() }
            if (latest != null && latest.versionCode > currentVersionCode) {
                updateInfo = latest
            }
        }
    }

    fun dismissUpdate() {
        updateInfo = null
    }

    val filteredPieces: List<Piece>
        get() {
            val q = searchQuery.trim().lowercase()
            if (q.isEmpty()) return pieces
            return pieces.filter { it.title.lowercase().contains(q) || it.composer.lowercase().contains(q) }
        }

    fun pieceById(id: String?): Piece? = pieces.find { it.id == id }

    private fun clearRecordTimer() {
        recordJob?.cancel(); recordJob = null
    }

    fun openPiece(id: String) {
        clearRecordTimer()
        screen = Screen.Detail(id)
    }

    fun goBack() {
        clearRecordTimer()
        screen = when (val s = screen) {
            is Screen.Practice -> Screen.Detail(s.pieceId)
            is Screen.Record -> Screen.Detail(s.pieceId)
            else -> Screen.Library
        }
    }

    fun setTab(tab: Screen) {
        clearRecordTimer()
        screen = tab
    }

    fun onSearchChange(value: String) {
        searchQuery = value
    }

    // --- Add form ---

    fun updateTitle(value: String) { addForm = addForm.copy(title = value) }
    fun updateComposer(value: String) { addForm = addForm.copy(composer = value) }
    fun pickLevel(level: Int) { addForm = addForm.copy(level = level) }

    fun pickPdfImport() {
        addForm = addForm.copy(importKind = ImportKind.PDF, importName = "partition.pdf", photoUris = emptyList())
    }

    fun addPhoto(uri: String) {
        addForm = addForm.copy(
            importKind = ImportKind.PHOTO,
            importName = null,
            photoUris = addForm.photoUris + uri,
        )
    }

    fun removePhoto(uri: String) {
        val remaining = addForm.photoUris - uri
        addForm = addForm.copy(
            photoUris = remaining,
            importKind = if (remaining.isEmpty()) null else ImportKind.PHOTO,
        )
    }

    fun submitAdd() {
        val f = addForm
        if (f.title.isBlank() || f.composer.isBlank() || f.importKind == null) return
        val pages = when (f.importKind) {
            ImportKind.PDF -> 3
            ImportKind.PHOTO -> f.photoUris.size.coerceAtLeast(1)
        }
        val piece = Piece(
            id = "p${System.currentTimeMillis()}",
            title = f.title.trim(),
            composer = f.composer.trim(),
            level = f.level,
            added = "Aujourd'hui",
            pages = pages,
            recordings = emptyList(),
            scoreImageUris = if (f.importKind == ImportKind.PHOTO) f.photoUris else emptyList(),
        )
        pieces = listOf(piece) + pieces
        addForm = AddForm()
        screen = Screen.Library
    }

    // --- Suggestions ---

    fun addSuggestion(suggestion: Suggestion) {
        if (suggestion.id in addedSuggestions) return
        val piece = Piece(
            id = "p${System.currentTimeMillis()}",
            title = suggestion.title,
            composer = suggestion.composer,
            level = suggestion.level,
            added = "Aujourd'hui",
            pages = MockData.pagesForLevel(suggestion.level),
            recordings = emptyList(),
        )
        pieces = listOf(piece) + pieces
        addedSuggestions = addedSuggestions + suggestion.id
    }

    // --- Practice (Jouer) ---

    fun startPractice(id: String) {
        clearRecordTimer()
        screen = Screen.Practice(id)
        practicePage = 0
    }

    fun jumpToPracticePage(page: Int) {
        val pieceId = (screen as? Screen.Practice)?.pieceId
        val total = pieceById(pieceId)?.pages ?: 1
        practicePage = page.coerceIn(0, total - 1)
    }

    // --- Record (Enregistrement) ---

    fun startRecord(id: String) {
        clearRecordTimer()
        screen = Screen.Record(id)
        recordActive = false
        recordElapsed = 0
    }

    fun toggleRecord() {
        if (recordActive) {
            clearRecordTimer()
            val pieceId = (screen as? Screen.Record)?.pieceId
            val duration = MockData.fmtTime(recordElapsed)
            val quality = 68 + Random.nextInt(28)
            pieces = pieces.map { p ->
                if (p.id == pieceId) {
                    val recording = Recording(id = "r${System.currentTimeMillis()}", date = "Aujourd'hui", duration = duration, quality = quality)
                    p.copy(recordings = listOf(recording) + p.recordings)
                } else p
            }
            recordActive = false
            return
        }
        recordActive = true
        recordElapsed = 0
        recordJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                recordElapsed += 1
            }
        }
    }

    override fun onCleared() {
        clearRecordTimer()
    }
}
