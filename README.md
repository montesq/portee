# Portée

Application Android native (Kotlin + Jetpack Compose) pour un pianiste : bibliothèque de
morceaux/partitions, ajout d'un morceau, mode "Jouer" plein écran en paysage qui tourne les
pages automatiquement, mode enregistrement avec historique des prises, et suggestions de
morceaux basées sur la progression.

Recréée à partir d'un handoff de design (Claude Design, système "Nocturne") — voir
`docs/design-handoff/` pour les specs et captures d'écran d'origine.

## Stack

- Kotlin, Jetpack Compose (Material 3), un seul module `app`
- minSdk 26, compileSdk/targetSdk 35
- Pas de backend : les données sont en mémoire (`MockData`), remises à zéro à chaque lancement

## Lancer le projet

1. Ouvrir le dossier dans Android Studio (Ladybug ou plus récent) — Android Studio régénère
   automatiquement le Gradle wrapper au premier sync si besoin.
2. Lancer sur un émulateur ou un appareil API 26+.

En ligne de commande, une fois le wrapper généré (`gradle wrapper` si vous avez Gradle en local) :

```bash
./gradlew assembleDebug
```

## Limitations connues (prototype → produit réel)

- **Police** : Inter n'est pas embarquée, la police système (Roboto) est utilisée à la place.
- **Persistance** : aucune base de données locale — à ajouter (Room, DataStore…) pour conserver
  la bibliothèque entre les sessions.
- **Écoute / reconnaissance de jeu** : le mode "Jouer" tourne les pages sur un minuteur fixe ; il
  n'y a pas encore d'analyse audio réelle du jeu au piano.
- **Enregistrement** : le bouton d'enregistrement simule une prise (chronomètre + score de
  qualité aléatoire) ; l'enregistrement audio réel (MediaRecorder) reste à implémenter.
- **Import PDF/Photo** : les boutons ne font que marquer un type d'import choisi ; l'intégration
  avec un sélecteur de fichiers/l'appareil photo et le rendu réel de la partition sont à faire.
- Ce projet a été généré sans environnement Java/Gradle local pour compiler — à valider dans
  Android Studio avant la première exécution.
