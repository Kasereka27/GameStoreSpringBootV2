# Guide du Projet : Spring Framework (Module 01)

Ce document détaille les exigences techniques et structurelles pour le projet d'examen académique du module Spring Framework.

## Spécifications Fonctionnelles et Techniques

Le projet doit impérativement respecter les fonctionnalités et l'architecture suivantes :

### 1. Architecture Applicative

* 
**Type :** Développement d'une application Web et d'une API.


* 
**Structure :** Implémentation d'une architecture "3 Layers" utilisant les annotations Spring suivantes:


* `@Controller` : Couche de présentation / gestion des requêtes.
* `@Service` : Couche métier / logique applicative.
* `@Repository` : Couche d'accès aux données.



### 2. Gestion des Données

* 
**Persistance :** Utilisation d'une base de données relationnelle.


* 
**Complexité :** La base de données doit contenir un minimum de **3 tables** distinctes.


* 
**Jointures :** Les requêtes doivent impliquer l'utilisation de jointures entre ces tables.



### 3. Accès aux Données et API

* 
**Interface :** Utilisation de l'interface `JdbcTemplate` ou `JdbcClient`.


* 
**Librairie :** Intégration obligatoire de la librairie **simpleflatmapper** pour la gestion du mapping.



### 4. Opérations Fondamentales

* 
**CRUD :** Le projet doit couvrir l'intégralité des opérations de base:


* **C**reate (Création)
* **R**ead (Lecture)
* **U**pdate (Mise à jour)
* **D**elete (Suppression)



---