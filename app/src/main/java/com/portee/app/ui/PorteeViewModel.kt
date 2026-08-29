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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

const val AUTO_TURN_SPEED_MS = 2600L

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

    var practiceListening by mutableStateOf(false)
        private set
    var practicePage by mutableStateOf(0)
        private set
    var practiceDone by mutableStateOf(false)
        private set

    var recordActive by mutableStateOf(false)
        private set
    var recordElapsed by mutableStateOf(0)
        private set

    private var practiceJob: Job? = null
    private var recordJob: Job? = null

    val suggestions: List<Suggestion> get() = MockData.suggestions

    val filteredPieces: List<Piece>
        get() {
            val q = searchQuery.trim().lowercase()
            if (q.isEmpty()) return pieces
            return pieces.filter { it.title.lowercase().contains(q) || it.composer.lowercase().contains(q) }
        }

    fun pieceById(id: String?): Piece? = pieces.find { it.id == id }

    private fun clearTimers() {
        practiceJob?.cancel(); practiceJob = null
        recordJob?.cancel(); recordJob = null
    }

    fun openPiece(id: String) {
        clearTimers()
        screen = Screen.Detail(id)
    }

    fun goBack() {
        clearTimers()
        screen = when (val s = screen) {
            is Screen.Practice -> Screen.Detail(s.pieceId)
            is Screen.Record -> Screen.Detail(s.pieceId)
            else -> Screen.Library
        }
    }

    fun setTab(tab: Screen) {
        clearTimers()
        screen = tab
    }

    fun onSearchChange(value: String) {
        searchQuery = value
    }

    // --- Add form ---

    fun updateTitle(value: String) { addForm = addForm.copy(title = value) }
    fun updateComposer(value: String) { addForm = addForm.copy(composer = value) }
    fun pickLevel(level: Int) { addForm = addForm.copy(level = level) }
    fun pickImport(kind: ImportKind) {
        val name = if (kind == ImportKind.PDF) "partition.pdf" else "photo_partition.jpg"
        addForm = addForm.copy(importKind = kind, importName = name)
    }

    fun submitAdd() {
        val f = addForm
        if (f.title.isBlank() || f.composer.isBlank() || f.importKind == null) return
        val piece = Piece(
            id = "p${System.currentTimeMillis()}",
            title = f.title.trim(),
            composer = f.composer.trim(),
            level = f.level,
            added = "Aujourd'hui",
            pages = if (f.importKind == ImportKind.PDF) 3 else 1,
            recordings = emptyList(),
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
        clearTimers()
        screen = Screen.Practice(id)
        practicePage = 0
        practiceListening = false
        practiceDone = false
    }

    fun toggleListening() {
        if (practiceListening) {
            clearTimers()
            practiceListening = false
            return
        }
        val pieceId = (screen as? Screen.Practice)?.pieceId ?: return
        practiceListening = true
        practiceDone = false
        practiceJob = viewModelScope.launch {
            while (isActive) {
                delay(AUTO_TURN_SPEED_MS)
                val total = pieceById(pieceId)?.pages ?: 1
                val next = practicePage + 1
                if (next >= total) {
                    practicePage = total - 1
                    practiceListening = false
                    practiceDone = true
                    break
                }
                practicePage = next
            }
        }
    }

    fun resetPractice() {
        clearTimers()
        practicePage = 0
        practiceDone = false
        practiceListening = false
    }

    // --- Record (Enregistrement) ---

    fun startRecord(id: String) {
        clearTimers()
        screen = Screen.Record(id)
        recordActive = false
        recordElapsed = 0
    }

    fun toggleRecord() {
        if (recordActive) {
            clearTimers()
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
        clearTimers()
    }
}
