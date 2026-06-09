# Balatro-lite / Balatri

Version simplifiee de Balatro en Java, structuree en MVC, avec une interface console et une interface graphique Zen6.

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
- Score calculé avec les cartes actives.
- Defausse active avant de jouer : remplacement des cartes choisies, limite par blind.
- Progression par blinds avec victoire ou defaite.
- Planetes permanentes qui ameliorent les niveaux des combinaisons.
- Interface console en ASCII.
- Interface graphique Zen6 avec menu, images associés aux cartes, panneaux de score, boutons Jouer/Defausser et affichage des planetes.
- Tests unitaires JUnit pour le deck, les mains, l'evaluateur et l'etat de jeu.

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

### Configuration des fichiers racine

| Package | Rôle |
|----------|------|
| `.classpath` | Contient la configuration des bibliothèques et du chemin de compilation du projet Eclipse. |
| `.project` | Contient les informations permettant à Eclipse de reconnaître le projet. |
| `.gitignore` | Liste les fichiers et dossiers que Git ne doit pas prendre en compte. |
| `/ressource` | Images aux formats PNG nécessaires pour le design du jeu. |

### Description des packages

| Package | Rôle |
|----------|------|
| `src/app` | Point d'entrée de l'application et lancement du jeu. |
| `src/controller` | Gestion de la logique de jeu et coordination entre le modèle et les vues. |
| `src/domain` | Entités métier du jeu : cartes, deck, mains, blinds, planètes et règles associées. |
| `src/model` | État de la partie et données persistantes du jeu. |
| `src/view` | Interfaces utilisateur console et graphique (Zen6). |
| `test/domain` | Tests des règles métier et des composants du domaine. |
| `test/model` | Tests de l'état du jeu et de sa gestion. |


## Lancer le projet dans Eclipse

Prerequis :

- JDK 25.
- Eclipse IDE.
- Avoir cloné le dépôt Git et importé le projet dans l'espace de travail Eclipse.

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

Note: Le projet ne contient pas de fichier Maven ou Gradle. Les tests JUnit (JUnit 5) dependent donc de la configuration Eclipse.
