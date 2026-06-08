# Balatro-lite / Balatri

Version simplifiee de Balatro en Java, structuree en MVC, avec une interface console et une interface graphique Zen6.

Le jeu affiche le nom **BALATRI** dans l'application.

## Presentation

Balatro-lite est un jeu solo inspire de Balatro. Le joueur pioche 8 cartes, peut effectuer des defausses actives, puis choisit 5 cartes pour former une combinaison de poker. Le score obtenu sert a battre une suite de blinds avec un nombre limite de mains.

La partie contient 3 blinds par defaut :

- Little Blind : 300 points
- Big Blind : 800 points
- Boss Blind : 2000 points

Chaque blind donne 4 mains et 3 defausses actives. Quand un blind est battu, un bonus de Planete aleatoire augmente definitivement les chips et le multiplicateur d'une combinaison.

## Fonctionnalites

- Architecture MVC : `domain`, `model`, `controller`, `view`, `app`.
- Jeu de 52 cartes avec pioche, defausse et remelange automatique de la defausse.
- Evaluation des 9 combinaisons de poker standards, dont la quinte basse A-2-3-4-5.
- Score calculé avec les cartes actives : `(chips de la combinaison + bonus des cartes actives) x multiplicateur`.
- Defausse active avant de jouer : remplacement des cartes choisies, limite par blind.
- Progression par blinds avec victoire ou defaite.
- Planetes permanentes qui ameliorent les niveaux des combinaisons.
- Interface console en ASCII.
- Interface graphique Zen6 avec menu, images associés aux cartes, panneaux de score, boutons Jouer/Defausser et affichage des planetes.
- Tests unitaires JUnit pour le deck, les mains, l'evaluateur et l'etat de jeu.

## Modifications integrees

- Ajout de l'extension A : seules les cartes actives d'une combinaison ajoutent leur valeur au score.
- Ajout de l'extension B : le joueur peut defausser des cartes avant de choisir sa main finale.
- Ajout de `HandLevel` pour stocker les chips et multiplicateurs courants de maniere immuable.
- Ajout et application des `Planet` pour faire progresser les niveaux de mains.
- Separation de l'interface graphique en `Zen6View`, `Zen6Renderer` et `Zen6Assets`.
- Ajout du suivi des defausses dans `GameState`, dans l'affichage console et dans l'affichage Zen6.
- Correction de `GameState.isGameOver()` pour eviter un acces hors limites apres la victoire.
- Validation plus stricte des entrees : tailles de mains, indices distincts, null safety, scores et compteurs invalides.
- Documentation Javadoc ajoutee ou completee sur les classes principales.
- README reecrit avec encodage lisible et inventaire complet du projet.

## Regles de score

Le score d'une main est calculé en deux etapes :

1. `HandEvaluator.evaluate()` determine la meilleure combinaison parmi les 5 cartes jouees.
2. `HandEvaluator.activeCards()` determine quelles cartes ajoutent leur valeur aux chips.

Formule utilisee par `GameController` :

```text
score = (chips de la combinaison + valeur des cartes actives) x multiplicateur
```

Les combinaisons sont classees de la plus faible a la plus forte :

| Rang | Combinaison | Condition | Cartes actives |
| ---: | --- | --- | --- |
| 1 | Carte haute | Aucune autre combinaison | La carte la plus haute |
| 2 | Paire | 2 cartes de même rang | Les 2 cartes de la paire |
| 3 | Double paire | 2 paires différentes | Les 4 cartes des deux paires |
| 4 | Brelan | 3 cartes de même rang | Les 3 cartes du brelan |
| 5 | Suite | 5 rangs consécutifs | Les 5 cartes |
| 6 | Couleur | 5 cartes de la même enseigne | Les 5 cartes |
| 7 | Full | Un brelan + une paire | Les 5 cartes |
| 8 | Carré | 4 cartes de même rang | Les 4 cartes du carré |
| 9 | Quinte flush | Suite + même enseigne | Les 5 cartes |

Valeur des cartes :

| Carte | Valeur |
| --- | ---: |
| 2 a 10 | Valeur indiquee sur la carte |
| Valet, Dame, Roi | 10 |
| As | 11 |

Les bonus de Planetes augmentent les chips et/ou le multiplicateur de la combinaison ciblee pour le reste de la partie.

## Architecture

```text
Balatro-lite/
|-- src/
|   |-- app/
|   |-- controller/
|   |-- domain/
|   |-- model/
|   `-- view/
|-- test/
|   |-- domain/
|   `-- model/
|-- resources/
|   |-- background.png
|   |-- background-2.png
|   `-- cards/
|-- lib/
|   `-- zen-6.0.jar
|-- bin/
|-- .classpath
|-- .project
|-- .gitignore
`-- README.md
```

## Contenu des fichiers

### Configuration et racine

| Fichier | Contenu / role |
| --- | --- |
| `README.md` | Documentation du projet, lancement, architecture, inventaire et modifications. |
| `.classpath` | Configuration Eclipse : sources `src` et `resources`, dependance `lib/zen-6.0.jar`, sortie `bin`. |
| `.project` | Projet Eclipse Java nomme `Balatro-lite`. |
| `.gitignore` | Ignore `bin/`, `.settings/`, `.project`, `.classpath` et fichiers systeme. |

### Application

| Fichier | Contenu / role |
| --- | --- |
| `src/app/Main.java` | Point d'entree. Cree les blinds, affiche le menu console, lance la vue console ou Zen6. |

### Controleur

| Fichier | Contenu / role |
| --- | --- |
| `src/controller/GameController.java` | Orchestre la boucle de jeu. Gere les modes console et graphique, la pioche de 8 cartes, les defausses actives, la selection de 5 cartes, le calcul du score, les recompenses Planete et le passage au blind suivant. |

### Domaine

| Fichier | Contenu / role |
| --- | --- |
| `src/domain/Blind.java` | Interface scellee representant un objectif de score. |
| `src/domain/StandardBlind.java` | Implementation standard d'un blind avec nom et score cible. |
| `src/domain/Card.java` | Record immuable d'une carte : `Rank` + `Suit`. |
| `src/domain/Rank.java` | Enum des 13 rangs, labels d'affichage et valeurs en chips des cartes. |
| `src/domain/Suit.java` | Enum des 4 enseignes : trefle, carreau, coeur, pique. |
| `src/domain/Deck.java` | Paquet de 52 cartes, pioche, defausse, remelange automatique, tailles de piles. |
| `src/domain/Hand.java` | Main immuable de 5 cartes, avec evaluation automatique de sa combinaison. |
| `src/domain/HandEvaluator.java` | Detection des combinaisons et calcul des cartes actives pour le bonus de score. |
| `src/domain/HandRank.java` | Enum des 9 combinaisons avec label, chips de base et multiplicateur de base. |
| `src/domain/HandLevel.java` | Record immuable des chips/multiplicateurs courants apres Planetes. |
| `src/domain/Planet.java` | Enum des 9 Planetes, chacune cible une combinaison et ajoute un bonus permanent. |

### Modele

| Fichier | Contenu / role |
| --- | --- |
| `src/model/GameState.java` | Etat complet de la partie : blinds, score courant, mains restantes, defausses restantes, niveaux de combinaisons et deck partage. |

### Vues

| Fichier | Contenu / role |
| --- | --- |
| `src/view/View.java` | Contrat commun des vues : affichage, selection, defausse, cartes actives, resultats, etats, victoire/defaite. |
| `src/view/ConsoleView.java` | Vue terminal : affichage ASCII, saisie des indices, defausse active, affichage des niveaux et recompenses. |
| `src/view/Zen6View.java` | Vue graphique : boucle d'evenements Zen6, clics sur cartes, boutons Jouer/Defausser, menu et synchronisation avec le controleur. |
| `src/view/Zen6Renderer.java` | Rendu graphique : panneaux, cartes, score, blinds, planetes, boutons, barre basse, messages. |
| `src/view/Zen6Assets.java` | Chargement des images de fond et des images de cartes depuis `resources`. |

### Tests

| Fichier | Contenu / role |
| --- | --- |
| `test/domain/DeckTest.java` | Verifie l'initialisation du deck, la pioche, la defausse, le remelange et les erreurs. |
| `test/domain/HandEvaluatorTest.java` | Verifie toutes les combinaisons, la quinte basse, les erreurs null et les tailles invalides. |
| `test/domain/HandTest.java` | Verifie la creation d'une main, l'evaluation automatique et l'immuabilite. |
| `test/model/GameStateTest.java` | Verifie le score, les mains, les blinds, les Planetes, la victoire/defaite et les defausses actives. |

### Ressources

| Chemin | Contenu / role |
| --- | --- |
| `resources/background.png` | Image de fond principale de l'interface graphique. |
| `resources/background-2.png` | Image de fond du menu graphique. |
| `resources/cards/*.png` | Images des 52 cartes, nommees `RANK_SUIT.png` comme `ACE_SPADES.png` ou `TEN_HEARTS.png`. |
| `lib/zen-6.0.jar` | Bibliotheque graphique Zen6 utilisee par la vue graphique. |
| `bin/` | Dossier de sortie Eclipse : classes compilees et ressources copiees. Il est ignore par Git. |

## Lancer le projet dans Eclipse

Prerequis :

- JDK 21 ou plus recent.
- Eclipse IDE pour developpeurs Java.

Import :

1. Ouvrir Eclipse.
2. Aller dans **File > Import > General > Existing Projects into Workspace**.
3. Selectionner le dossier du projet.
4. Valider avec **Finish**.

Lancement :

1. Ouvrir `src/app/Main.java`.
2. Clic droit sur `Main.java`.
3. Choisir **Run As > Java Application**.
4. Dans le menu :
   - `1` : mode console.
   - `2` : interface graphique Zen6.
   - `3` : quitter.

## Lancer les tests

Dans Eclipse :

1. Clic droit sur le dossier `test/`.
2. Choisir **Run As > JUnit Test**.

Le projet ne contient pas de fichier Maven ou Gradle. Les tests JUnit dependent donc de la configuration Eclipse.

## Notes techniques

- Le code utilise des collections immuables ou des copies defensives aux endroits critiques (`Hand`, `GameState`, listes exposees aux vues).
- Le controleur reste independant du type de vue grace a l'interface `View`.
- La vue Zen6 ne bloque pas pour demander une selection : les actions passent par les callbacks publics du controleur.
- Les fichiers image sont charges via le classpath, donc `resources` doit rester declare comme source dans Eclipse.
