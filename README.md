# Balatro-lite

Version simplifiée du jeu *Balatro* — Projet Java MVC avec interface console et graphique.

---

## Présentation

**Balatro-lite** est un jeu solo de poker de type rogue-like, inspiré du jeu *Balatro*. L'objectif est de former des mains de poker à partir de cartes piochées afin d'atteindre des objectifs de score progressifs, appelés **Blinds**, avec un nombre limité de mains.

Ce dépôt contient l'implémentation complète de la **Phase 1 (Soutenance β)**, comprenant une architecture **MVC** entièrement découplée, une interface en terminal (ASCII-art), une interface graphique via la bibliothèque **Zen6**, ainsi qu'une suite exhaustive de tests unitaires JUnit 5.

---

## Fonctionnalités implémentées

- **Modèle de domaine** : représentation orientée objet complète d'un jeu de 52 cartes (`Card`, `Suit`, `Rank`).
- **Évaluateur de mains** : détection des 9 combinaisons de poker standards (de la Carte Haute à la Quinte Flush), incluant la quinte basse A-2-3-4-5.
- **Boucle de jeu** : gestion complète de l'état (`GameState`) — blinds, scores, mains restantes et tas de cartes.
- **Pioche automatique** : remélange automatique depuis la défausse lorsque la pioche est épuisée.
- **Cartes Planètes** : modificateurs permanents de score (jetons et multiplicateurs) attribués après chaque Blind.
- **Vue Console** : interface terminal entièrement formatée en ASCII-art.
- **Vue Graphique (Zen6)** : interface visuelle utilisant la bibliothèque Zen6.
- **Tests unitaires** : JUnit 5 (`Deck`, `Hand`, `HandEvaluator`, `GameState`).

---

## Architecture du projet (MVC)

```
balatro-lite/
├── src/
│   ├── app/          # Point d'entrée (Main.java)
│   ├── domain/       # Logique métier pure (Card, Deck, HandEvaluator…)
│   ├── model/        # Gestion de l'état (GameState)
│   ├── controller/   # Orchestration du flux de jeu (GameController)
│   └── view/         # Couches de présentation (View, ConsoleView, Zen6View)
├── test/             # Tests unitaires JUnit 5
├── resources/        # Assets graphiques (.png) et sonores (.wav)
└── lib/
    └── zen-6.0.jar   # Bibliothèque graphique (incluse)
```

---

## Prérequis

- Java JDK 21 ou supérieur
- Eclipse IDE pour développeurs Java

---

## Importer le projet

### Option A — Depuis l'archive `.zip` (déposée sur Moodle)

1. Téléchargez et extrayez l'archive `.zip`.
2. Dans Eclipse : **File → Import → General → Existing Projects into Workspace**.
3. Cliquez sur **Browse...** et sélectionnez le dossier extrait (celui contenant directement `src/` et `.project`).
4. Assurez-vous que le projet est coché dans la liste, puis cliquez sur **Finish**.

### Option B — Depuis GitHub

```bash
git clone https://github.com/yasminebyb/balatro-lite.git
```

1. Dans Eclipse : **File → Import → General → Existing Projects into Workspace**.
2. Cliquez sur **Browse...** et sélectionnez le dossier `balatro-lite` cloné.
3. Cliquez sur **Finish**.

> La bibliothèque `zen-6.0.jar` est incluse dans le dossier `/lib` et est automatiquement associée au build path lors de l'importation.

---

## Lancer le programme

1. Dans l'Explorateur de projet Eclipse, localisez `src/app/Main.java`.
2. Clic droit sur `Main.java` → **Run As → Java Application**.
3. Choisissez votre mode d'affichage dans le menu terminal :
   - `1` — Mode Console
   - `2` — Mode Graphique (Zen6)
   - `3` — Quitter

### Lancer les tests unitaires

Clic droit sur le dossier `test/` → **Run As → JUnit Test**.
