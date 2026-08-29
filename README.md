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
- Pas de backend : les données sont en mémoire, remises à zéro à chaque lancement — la
  bibliothèque démarre vide, à remplir depuis l'onglet Ajouter

## Lancer le projet

1. Ouvrir le dossier dans Android Studio (Ladybug ou plus récent) — Android Studio régénère
   automatiquement le Gradle wrapper au premier sync si besoin.
2. Lancer sur un émulateur ou un appareil API 26+.

En ligne de commande, une fois le wrapper généré (`gradle wrapper` si vous avez Gradle en local) :

```bash
./gradlew assembleDebug
```

Sans Android Studio ni Gradle local : chaque push sur `main` déclenche le workflow
[`.github/workflows/android-build.yml`](.github/workflows/android-build.yml), qui compile un
APK debug, le publie comme artefact du run (onglet *Actions*) **et** crée une
[GitHub Release](https://github.com/montesq/portee/releases) taguée `vN` (N = numéro de run)
avec l'APK attaché — à télécharger et installer directement sur un téléphone Android 8.0+.

## Mise à jour automatique

L'app vérifie au démarrage si une release plus récente existe (`GET /repos/montesq/portee/releases/latest`)
et, si oui, propose de la télécharger et l'installer (`UpdateChecker` / `UpdateDialog`,
package `com.portee.app.update`). Ça suppose :
- le repo **public**, pour interroger l'API et télécharger l'APK sans identifiants embarqués ;
- `versionCode` = numéro de run CI (passé via `-PversionCode=…`), comparé au tag `vN` de la
  dernière release ;
- l'autorisation Android "installer des applications inconnues" accordée à l'app qui déclenche
  l'installation (demandée automatiquement au premier "Mettre à jour" si besoin).

## Limitations connues (prototype → produit réel)

- **Police** : Inter n'est pas embarquée, la police système (Roboto) est utilisée à la place.
- **Persistance** : aucune base de données locale — à ajouter (Room, DataStore…) pour conserver
  la bibliothèque entre les sessions.
- **Écoute / reconnaissance de jeu** : le mode "Jouer" tourne les pages sur un minuteur fixe ; il
  n'y a pas encore d'analyse audio réelle du jeu au piano.
- **Enregistrement** : le bouton d'enregistrement simule une prise (chronomètre + score de
  qualité aléatoire) ; l'enregistrement audio réel (MediaRecorder) reste à implémenter.
- **Import PDF** : le bouton ne fait que marquer un import PDF simulé ; un vrai sélecteur de
  fichiers et le rendu du PDF restent à faire. L'import **Photo**, lui, ouvre l'appareil photo
  réel (une ou plusieurs pages) et les photos sont affichées dans la fiche détail et en mode Jouer.
- Les 3 suggestions de l'onglet Suggestions restent des données fixes (issues du prototype de
  design) — un vrai moteur de recommandation basé sur la bibliothèque réelle reste à faire.
