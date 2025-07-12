# EcoDeli Desktop

Application de bureau développée en Java avec JavaFX pour la gestion du back office d'EcoDeli. Cette application permet aux administrateurs et employés d'EcoDeli de gérer efficacement les statistiques générales et les opérations administratives.

Application proposée par : 

- Quentin DELNEUF
- Damien VAURETTE
- Rémy THIBAUT

Classe 2A3 - Année 2024-2025

## Description

EcoDeli Desktop est une application de gestion complète qui reprend toutes les fonctionnalités du back office web dans une interface desktop native. Elle offre aux équipes administratives d'EcoDeli un accès direct et optimisé aux outils de gestion, statistiques et reporting.

## Prérequis

- Java 21 ou version supérieure
- Maven 3.6 ou version supérieure
- Système d'exploitation compatible avec JavaFX (Windows, macOS, Linux)

## Installation et lancement

### Lancer l'application en mode développement

```bash
mvn clean javafx:run
```

### Compiler et créer le JAR exécutable

```bash
mvn clean package
```

Le JAR exécutable sera généré dans le dossier `target/` avec toutes les dépendances incluses.

### Lancer le JAR compilé

```bash
java -jar target/EcoDeli-desktop-1.0-SNAPSHOT.jar
```

## Librairies utilisées

### Interface utilisateur
- **JavaFX Controls** - Composants d'interface utilisateur
- **JavaFX FXML** - Chargement des interfaces depuis fichiers FXML
- **JavaFX Web** - Intégration de contenu web
- **JavaFX Swing** - Intégration avec Swing
- **ControlsFX** - Composants UI avancés pour JavaFX
- **FormsFX** - Création de formulaires avancés
- **ValidatorFX** - Validation des données de formulaires
- **TilesFX** - Composants de dashboard et visualisation

### Cartes et géolocalisation
- **GMapsFX** - Intégration Google Maps dans JavaFX

### Icônes
- **Ikonli JavaFX** - Système d'icônes pour JavaFX
- **Ikonli FontAwesome** - Pack d'icônes FontAwesome

### Génération de documents
- **Apache PDFBox** - Création et manipulation de documents PDF
- **Apache PDFBox Tools** - Outils supplémentaires pour PDFBox
- **Apache PDFBox IO** - Support d'entrée/sortie pour PDFBox
- **iText PDF** - Génération avancée de documents PDF
- **Graphics2D** - Rendu graphique dans les PDFs

### Graphiques et visualisation
- **JFreeChart** - Création de graphiques et diagrammes

### Communication réseau
- **OkHttp** - Client HTTP moderne et performant
- **Gson** - Sérialisation/désérialisation JSON

### Utilitaires
- **JavaFaker** - Génération de données de test
- **JUnit Jupiter** - Framework de tests unitaires

## Structure du projet

```
EcoDeli-desktop/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── fr/ecodeli/ecodelidesktop/
│   │   └── resources/
│   └── test/
├── target/
├── pom.xml
└── README.md
```

## Fonctionnalités

- Gestion des statistiques générales
- Interface administrateur complète
- Génération de rapports PDF
- Visualisation de données avec graphiques
- Intégration cartographique
- Interface utilisateur moderne et responsive

## Développement

Le projet utilise Maven comme gestionnaire de dépendances et Java 21 comme version cible. L'application est configurée pour être empaquetée avec toutes ses dépendances dans un JAR exécutable unique.

## Support

Pour toute question ou problème technique, veuillez contacter l'équipe de développement EcoDeli.
