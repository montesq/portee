package com.portee.app.data

import java.time.LocalDate

// A real build needs persistence (local DB) instead of in-memory state.
object MockData {

    private val monthsFr = listOf(
        "janv.", "févr.", "mars", "avr.", "mai", "juin",
        "juil.", "août", "sept.", "oct.", "nov.", "déc.",
    )

    fun dateLabel(daysAgo: Int): String {
        val d = LocalDate.now().minusDays(daysAgo.toLong())
        return "${d.dayOfMonth} ${monthsFr[d.monthValue - 1]} ${d.year}"
    }

    fun pagesForLevel(level: Int): Int = if (level <= 1) 1 else if (level <= 3) 2 else 3

    fun fmtTime(totalSeconds: Int): String {
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        return "$m:${s.toString().padStart(2, '0')}"
    }

    val suggestions = listOf(
        Suggestion("s1", "Arabesque n°1", "Claude Debussy", 4, "Étape suivante après la Gymnopédie n°1"),
        Suggestion("s2", "Ballade pour Adeline", "Richard Clayderman", 3, "Même niveau que le Nocturne, style proche"),
        Suggestion("s3", "Sonate au clair de lune, 1er mvt", "Ludwig van Beethoven", 5, "Progression depuis Clair de lune"),
    )
}
