package com.portee.app.data

data class Recording(
    val id: String,
    val date: String,
    val duration: String,
    val quality: Int, // 0-100
)

data class Piece(
    val id: String,
    val title: String,
    val composer: String,
    val level: Int, // 1-5
    val added: String,
    val pages: Int,
    val recordings: List<Recording> = emptyList(),
)

data class Suggestion(
    val id: String,
    val title: String,
    val composer: String,
    val level: Int,
    val reason: String,
)

enum class ImportKind { PDF, PHOTO }

data class AddForm(
    val title: String = "",
    val composer: String = "",
    val level: Int = 3,
    val importKind: ImportKind? = null,
    val importName: String? = null,
)
