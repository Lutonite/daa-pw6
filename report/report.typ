#import "template.typ": *
#show: project.with(
  title: "DAA - Laboratoire 6",
  authors: (
    "Émilie Bressoud",
    "Sacha Butty",
    "Loïc Herman"
  ),
  date: "January 13, 2024",
)

= Détails d'implémentation

#import "template.typ": *

= Détails d'implémentation

== Gestion des états
L'application utilise un système d'états pour gérer la synchronisation des contacts :
- *SYNCED* : Contact synchronisé avec le serveur
- *CREATED* : Nouveau contact en attente de synchronisation
- *UPDATED* : Contact modifié localement
- *DELETED* : Contact marqué pour la suppression avant la synchronisation


== Opérations réseau
=== Création d'un contact
Lors de la création d'un contact, l'application privilégie l'expérience utilisateur en appliquant immédiatement les modifications en local. Le contact est d'abord sauvegardé dans la base de données locale avec l'état CREATED. Une tentative de synchronisation avec le serveur est ensuite effectuée. En cas de succès, l'application met à jour l'identifiant serveur et change l'état en SYNCED. Si la synchronisation échoue, le contact conserve son état CREATED pour permettre une synchronisation ultérieure.

=== Modification d'un contact
La modification suit une approche similaire à la création. Les changements sont immédiatement appliqués localement avec l'état UPDATED. L'application tente ensuite de synchroniser ces modifications avec le serveur. La réussite de cette opération entraîne le passage à l'état SYNCED, tandis qu'un échec maintient l'état UPDATED pour une synchronisation future.

=== Suppression d'un contact
Pour la suppression, le contact est d'abord marqué comme DELETED dans la base de données locale. L'application tente ensuite de le supprimer sur le serveur. En cas de succès, le contact est définitivement supprimé de la base locale (hard delete). Si la suppression côté serveur échoue, le contact reste marqué comme DELETED pour une tentative ultérieure.

Le marquage `DELETED`permet aussi d'identifier les contacts a ne plus afficher dans l'interface utilisateur.

== Stockage local
La persistence des données est gérée via la bibliothèque Room. Nous utilisons une entité Contact qui comprend tous les champs nécessaires ainsi qu'un champ d'état pour la synchronisation. Room facilite la gestion des opérations CRUD et permet une observation réactive des changements via LiveData.

== Stratégie de synchronisation
Notre application implémente l'approche "local-first" qui priorise la réactivité et l'expérience utilisateur. Les modifications sont appliquées immédiatement en local puis synchronisées avec le serveur de manière asynchrone en utilisant l'action refresh. Les conflits sont gérés en conservant les états de synchronisation appropriés, permettant des tentatives ultérieures de synchronisation. Cette approche garantit que l'application reste utilisable même en cas de problèmes de connectivité.

=== Gestion des appels réseau
Retrofit2 transforme automatiquement nos appels HTTP en méthodes Kotlin suspendues, permettant une utilisation naturelle avec les coroutines. Les annotations comme `@GET`, `@POST`, `@PUT` et `@DELETE` définissent le type de requête, tandis que `@Header` et `@Path` gèrent les paramètres dynamiques.

=== Conversion des données
La conversion entre JSON et objets Kotlin est gérée par le convertisseur Gson de Retrofit. Le DTOs assure une séparation claire entre les données réseau et le modèle local :
- *ContactDTO* : Représentation réseau d'un contact
- Mappers de conversion vers/depuis les entités locales

=== Gestion des erreurs
Les erreurs permettent de gérer les cas où la synchronisation échoue. Nous utilisons un simple try catch pour intercepter les exceptions et afficher des logs détaillés. Si la synchronisation échoue, l'application conserve le contact en local selon ce que l'utilisateur a effectué.

== Interface utilisateur avec Jetpack Compose
Notre application utilise Jetpack Compose, le toolkit moderne de Google pour la construction d'interfaces utilisateur natives Android. Cette approche déclarative simplifie considérablement le développement UI en comparaison avec les vues XML traditionnelles.

=== Architecture UI
Nous avons structuré l'interface en utilisant des composants réutilisables afin de facilité la maintenance et l'évolutivité de l'application.

- *ContactTextField* : Champ de saisie personnalisé pour les différentes informations de contact sous forme de TextField
- *ContactDateField* : Sélecteur de date pour la date de naissance
- *ContactPhoneTypeField* : Sélecteur du type de numéro de téléphone
- *EditButtons* : Groupe de boutons pour la gestion des contacts, qui change selon le type d'opération (création ou modification)

=== State Management
Nous utilisons le state management de Jetpack Compose pour gérer l'état de l'interface utilisateur. Avec `remember`, nous conservons l'état local des composants et des écrans, permettant une mise à jour réactive de l'interface en fonction des actions de l'utilisateur.