package com.portee.app.data

import java.time.LocalDate

// In-memory demo data — mirrors the design prototype's mock catalog and generation rules.
// A real build needs persistence (local DB) instead of this in-memory data.
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

    private val catalog = listOf(
        Triple("Clair de lune", "Claude Debussy", 5),
        Triple("Nocturne op. 9 n°2", "Frédéric Chopin", 3),
        Triple("Gymnopédie n°1", "Erik Satie", 1),
        Triple("Arabesque n°1", "Claude Debussy", 3),
        Triple("Sonate au clair de lune, 1er mvt", "Ludwig van Beethoven", 4),
        Triple("Prélude en do majeur", "Johann Sebastian Bach", 1),
        Triple("Valse minute", "Frédéric Chopin", 5),
        Triple("Für Elise", "Ludwig van Beethoven", 2),
        Triple("Rêverie", "Claude Debussy", 3),
        Triple("Gnossienne n°1", "Erik Satie", 1),
        Triple("Ballade pour Adeline", "Richard Clayderman", 2),
        Triple("Étude révolutionnaire", "Frédéric Chopin", 5),
        Triple("La lettre à Élise", "Ludwig van Beethoven", 1),
        Triple("Fantaisie-Impromptu", "Frédéric Chopin", 5),
        Triple("Prélude op. 28 n°4", "Frédéric Chopin", 2),
        Triple("Turkish March", "Wolfgang Amadeus Mozart", 3),
        Triple("Sonate facile K.545, 1er mvt", "Wolfgang Amadeus Mozart", 1),
        Triple("Rhapsodie hongroise n°2", "Franz Liszt", 5),
        Triple("Liebestraum n°3", "Franz Liszt", 4),
        Triple("Consolation n°3", "Franz Liszt", 3),
        Triple("Prélude en mi mineur", "Frédéric Chopin", 1),
        Triple("Comptine d’un autre été", "Yann Tiersen", 2),
        Triple("La valse d’Amélie", "Yann Tiersen", 1),
        Triple("River Flows in You", "Yiruma", 2),
        Triple("Kiss the Rain", "Yiruma", 3),
        Triple("Gymnopédie n°3", "Erik Satie", 1),
        Triple("Sonate pathétique, 2e mvt", "Ludwig van Beethoven", 3),
        Triple("Prélude n°15 « La goutte d’eau »", "Frédéric Chopin", 4),
        Triple("Traumerei", "Robert Schumann", 2),
        Triple("Menuet en sol majeur", "Johann Sebastian Bach", 1),
    )

    fun buildPieces(): List<Piece> = catalog.mapIndexed { i, (title, composer, level) ->
        val recordings = when {
            i % 3 == 0 -> listOf(
                Recording("r${i}a", dateLabel(i * 4 - 3), "3:5${i % 6}", 70 + (i * 7) % 25),
            )
            i % 5 == 0 -> emptyList()
            else -> listOf(
                Recording("r${i}a", dateLabel(i * 4 - 6), "4:0${i % 6}", 65 + (i * 5) % 30),
                Recording("r${i}b", dateLabel(i * 4 - 1), "3:5${i % 6}", 75 + (i * 3) % 24),
            )
        }
        Piece(
            id = "p${i + 1}",
            title = title,
            composer = composer,
            level = level,
            added = dateLabel(i * 4),
            pages = pagesForLevel(level),
            recordings = recordings,
        )
    }

    val suggestions = listOf(
        Suggestion("s1", "Arabesque n°1", "Claude Debussy", 4, "Étape suivante après la Gymnopédie n°1"),
        Suggestion("s2", "Ballade pour Adeline", "Richard Clayderman", 3, "Même niveau que le Nocturne, style proche"),
        Suggestion("s3", "Sonate au clair de lune, 1er mvt", "Ludwig van Beethoven", 5, "Progression depuis Clair de lune"),
    )
}
