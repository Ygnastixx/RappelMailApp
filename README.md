-----

# Application de Rappel par E-mail

## Description du Projet

Cette application web est un système simple de **planification et d'envoi de rappels par e-mail**. Conçue pour démontrer l'utilisation de **JavaMail** dans un environnement web, elle permet aux utilisateurs de créer des rappels personnalisés (sujet, contenu HTML, pièces jointes) qui seront envoyés à une date et une heure spécifiques.

Le projet met en œuvre une interface web conviviale pour la configuration des rappels et utilise une approche de **modèle de mail réutilisable** : la page de prévisualisation du rappel sur l'application a la même structure et la même apparence que l'e-mail final envoyé, garantissant une cohérence visuelle.

## Fonctionnalités

  * **Planification de Rappels :** Définissez la date et l'heure exactes pour l'envoi de vos e-mails.
  * **Contenu HTML :** Créez des rappels visuellement riches et formatés grâce au support du HTML dans le corps de l'e-mail.
  * **Pièces Jointes :** Joignez des fichiers pertinents à vos rappels (documents, images, etc.).
  * **Interface Web :** Une application accessible via un navigateur, rendant la création de rappels intuitive.
  * **Configuration Sécurisée de l'Expéditeur :** Les informations sensibles du compte expéditeur (Gmail) sont stockées côté serveur dans un fichier de propriétés pour une sécurité et une facilité d'utilisation accrues.
  * **Cohérence Visuelle :** La page de prévisualisation du rappel utilise le même modèle HTML/CSS que l'e-mail final.

## Technologies Utilisées

  * **Java 11+**
  * **Maven** : Système de gestion de projet et de dépendances.
  * **Jakarta Mail (anciennement JavaMail)** : API pour l'envoi d'e-mails.
  * **Jakarta Servlet API** : Pour la création des composants côté serveur (Servlets).
  * **Jakarta JSP API** : Pour la génération de pages web dynamiques.
  * **Apache Tomcat** : Serveur d'applications web.
  * **HTML, CSS** : Pour la structure et le style de l'interface utilisateur et des e-mails.

## Prérequis

Avant de lancer le projet, assurez-vous d'avoir :

  * **Java Development Kit (JDK) 11 ou supérieur** installé.

  * **Maven** installé.

  * **IntelliJ IDEA Community Edition** (ou un autre IDE compatible Java/Maven).

  * Un **compte Gmail** avec la **validation en deux étapes activée** et un **mot de passe d'application généré**. Ce mot de passe de 16 caractères sera utilisé par l'application pour se connecter au serveur SMTP de Gmail.

      * **Comment obtenir un mot de passe d'application Gmail :**
        1.  Accédez à [myaccount.google.com](https://myaccount.google.com/).
        2.  Allez dans la section **Sécurité**.
        3.  Sous "Comment vous connecter à Google", cliquez sur **Mots de passe des applications**. Si l'option n'est pas visible, activez d'abord la validation en deux étapes.
        4.  Suivez les instructions pour générer un nouveau mot de passe d'application (sélectionnez "Autre (Nom personnalisé)" et donnez-lui un nom comme "RappelMailApp"). Copiez ce mot de passe de 16 caractères.

## Installation et Exécution

Suivez ces étapes pour configurer et exécuter le projet :

1.  **Cloner le dépôt (si applicable) ou télécharger le projet.**

2.  **Configuration du Projet dans IntelliJ IDEA :**

      * Ouvrez IntelliJ IDEA.
      * Sélectionnez **"Open"** et naviguez vers le dossier racine du projet. IntelliJ devrait le reconnaître automatiquement comme un projet Maven.
      * Assurez-vous que le **JDK 11+** est configuré pour le projet ( `File` \> `Project Structure` \> `Project` ).

3.  **Configuration du Fichier de Propriétés SMTP :**

      * Créez un fichier nommé `email.properties` dans le dossier `src/main/resources/` de votre projet.
      * Ajoutez les lignes suivantes, en remplaçant les valeurs par vos informations Gmail (utilisez le **mot de passe d'application** pour `sender.password`) :
        ```properties
        sender.email=votre_email_expediteur@gmail.com
        sender.password=votre_mot_de_passe_application_gmail
        smtp.host=smtp.gmail.com
        smtp.port=587
        ```

4.  **Configuration d'Apache Tomcat dans IntelliJ :**

      * Allez dans `Run` \> `Edit Configurations...`.
      * Cliquez sur le `+` et sélectionnez **`Tomcat Server` \> `Local`**.
      * Sous l'onglet **`Server`**, assurez-vous que votre installation de Tomcat est bien configurée (si ce n'est pas le cas, cliquez sur `Configure...` et naviguez vers votre dossier d'installation de Tomcat).
      * Sous l'onglet **`Deployment`**, cliquez sur le `+` et sélectionnez `Artifact...` \> `RappelMailWebApp:war exploded` (ou le nom de votre `artifactId`). Assurez-vous que le "Application context" est `/RappelMailApp` (ou le nom de votre `artifactId`).
      * Cliquez sur `Apply` puis `OK`.

5.  **Exécution de l'Application :**

      * Dans IntelliJ, cliquez sur le bouton `Run` (la flèche verte) à côté de la configuration Tomcat que vous venez de créer.
      * Tomcat démarrera et l'application sera déployée.
      * Ouvrez votre navigateur web et accédez à l'URL : `http://localhost:8080/RappelMailApp/` (remplacez `RappelMailApp` par votre `artifactId` si différent).

## Structure du Projet

```
RappelMailApp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── rappelmail/
│   │   │               ├── EmailSender.java       <-- Logique d'envoi d'e-mail
│   │   │               └── ReminderServlet.java   <-- Gère les requêtes web et la planification
│   │   ├── resources/
│   │   │   └── email.properties                   <-- Configuration SMTP de l'expéditeur
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── web.xml                        <-- Descripteur de déploiement web
│   │       ├── index.jsp                          <-- Page du formulaire de rappel
│   │       └── previewReminder.jsp                <-- Page de prévisualisation du rappel
│   └── test/
└── pom.xml                                      <-- Fichier de configuration Maven
```

-----
