# BALATRI

Version simplifiée de Balatro développée en Java selon une architecture MVC.

## Fonctionnalités implémentées

* Jeu de cartes de 52 cartes avec pioche, défausse et remélange automatique.
* Évaluation des combinaisons de poker :

  * Carte haute
  * Paire
  * Double paire
  * Brelan
  * Suite
  * Couleur
  * Full
  * Carré
  * Quinte flush
    
* Gestion des blinds :
  * Little Blind (300 points)
  * Big Blind (800 points)
  * Boss Blind (2000 points)
 
* Limitation du nombre de mains et de défausses par blind.
* Extension A : seules les cartes actives de la combinaison contribuent au score.
* Extension B : défausse active avant de jouer une main.
* Système de planètes améliorant durablement les combinaisons.
* Interface console.
* Interface graphique avec Zen6.
* Tests unitaires JUnit.

## Architecture du projet

```text
src/
├── app
├── controller
├── domain
├── model
└── view

test/
├── domain
└── model

resources/
├── cards
├── background.png
└── background-2.png

lib/
└── zen-6.0.jar
```

## Importer le projet dans Eclipse

### Prérequis

* JDK 25
* Eclipse IDE
  

### Import

1. Ouvrir Eclipse.
2. Aller dans **File > Import**.
3. Choisir **General > Existing Projects into Workspace**.
4. Sélectionner le dossier du projet.
5. Cliquer sur **Finish**.

## Lancer le programme

1. Ouvrir `src/app/Main.java`.
2. Faire un clic droit sur `Main.java`.
3. Choisir **Run As > Java Application**.

Un menu s'affiche :

* `1` : mode console
* `2` : interface graphique Zen6
* `3` : quitter

## Lancer les tests

1. Faire un clic droit sur le dossier `test`.
2. Choisir **Run As > JUnit (JUnit5) Test**.
