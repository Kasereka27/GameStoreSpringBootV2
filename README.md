# Cahier des Charges — Application Web E-Commerce de Vente de Licences de Jeux Vidéo

**Projet :** GameStore Platform  
**Framework :** Spring Boot (Java)  
**Version du document :** 1.0  
**Date :** Juin 2026

---

## 1. Présentation du Projet

### 1.1 Contexte

Le marché des jeux vidéo numériques est en pleine expansion. De plus en plus de joueurs préfèrent acheter des licences numériques (clés de jeux) plutôt que des supports physiques. Ce projet vise à développer une plateforme e-commerce moderne permettant l'achat, la gestion et la distribution de licences de jeux vidéo sous forme de clés d'activation numériques.

### 1.2 Objectifs

- Offrir une expérience d'achat fluide et sécurisée pour les licences de jeux vidéo numériques
- Gérer un catalogue de jeux varié avec des informations détaillées (genre, éditeur, plateforme)
- Automatiser la distribution des clés d'activation après paiement confirmé
- Fournir un tableau de bord administrateur complet pour la gestion du catalogue, du stock et des commandes
- Assurer une sécurité robuste des données clients et des transactions

### 1.3 Périmètre

Le système couvrira les modules suivants :
- Gestion du catalogue de jeux et des licences
- Authentification et gestion des utilisateurs
- Panier d'achat et processus de commande
- Intégration de passerelle de paiement
- Distribution automatique des clés d'activation
- Interface d'administration
- Notifications par email

---

## 2. Parties Prenantes

| Rôle | Responsabilités |
|------|-----------------|
| **Chef de projet** | Pilotage global, validation des livrables |
| **Développeur Backend** | Architecture Spring Boot, APIs REST, base de données |
| **Développeur Frontend** | Interface utilisateur (Thymeleaf) |
| **Administrateur** | Gestion du catalogue, stock, commandes |
| **Client (utilisateur final)** | Navigation, achat, gestion de son compte |

---

## 3. Spécifications Fonctionnelles

### 3.1 Module Authentification & Gestion des Utilisateurs

#### 3.1.1 Inscription

- Formulaire d'inscription avec les champs : nom, prénom, email, mot de passe, confirmation mot de passe
- Validation des données (format email, complexité du mot de passe)
- Envoi d'un email de confirmation de compte
- Activation du compte via lien dans l'email
- Protection contre les inscriptions automatisées (CAPTCHA)

#### 3.1.2 Connexion

- Authentification par email/mot de passe
- Option "Se souvenir de moi" (Remember Me) via token persistant
- Connexion OAuth2 (Google, optionnel : Steam, Discord)
- Verrouillage du compte après N tentatives échouées (configurable)
- Récupération de mot de passe par email

#### 3.1.3 Profil Utilisateur

- Modification des informations personnelles
- Changement de mot de passe
- Historique des commandes
- Bibliothèque des jeux achetés (licences)
- Gestion des adresses de facturation

#### 3.1.4 Rôles et Permissions

| Rôle | Permissions |
|------|-------------|
| `ROLE_GUEST` | Consultation du catalogue uniquement |
| `ROLE_USER` | Achat, consultation de son compte, bibliothèque |
| `ROLE_ADMIN` | Toutes les fonctions + tableau de bord admin |
| `ROLE_SUPERADMIN` | Gestion des administrateurs, paramètres système |

---

### 3.2 Module Catalogue de Jeux

#### 3.2.1 Structure d'un Jeu

Chaque jeu devra contenir les informations suivantes :

- Titre du jeu
- Description courte et longue
- Images (miniature, galerie de captures d'écran)
- Vidéo de présentation (URL YouTube/Vimeo, optionnel)
- Éditeur / Développeur
- Date de sortie
- Genre(s) (Action, RPG, Stratégie, Sport, etc.)
- Plateforme(s) compatible(s) (PC, PlayStation, Xbox, Nintendo Switch)
- Classification d'âge (PEGI)
- Langue(s) supportée(s)
- Configuration minimale et recommandée (pour PC)
- Prix de base
- Prix promotionnel (avec date début/fin de promotion)
- Statut : Actif, Inactif, Bientôt disponible (Pre-order)
- Étiquettes/Tags (Open World, Multijoueur, Solo, etc.)

#### 3.2.2 Gestion du Stock de Licences

- Chaque jeu dispose d'un pool de clés d'activation
- Import de clés en masse (fichier CSV ou saisie directe)
- Indicateur de stock disponible
- Alertes de stock faible (seuil configurable)
- Statut des clés : Disponible, Vendu, Réservé, Invalide
- Traçabilité : chaque clé est liée à une commande et un utilisateur

#### 3.2.3 Navigation et Recherche

- Page d'accueil avec jeux en vedette, nouveautés, promotions
- Filtres avancés : genre, plateforme, prix (fourchette), éditeur, date de sortie, note
- Recherche textuelle avec autocomplétion
- Tri : pertinence, prix croissant/décroissant, meilleures ventes, nouveautés, notes
- Pagination ou chargement infini
- Vue en grille et vue en liste

#### 3.2.4 Fiche Produit

- Toutes les informations du jeu
- Galerie d'images avec zoom
- Bouton "Ajouter au panier" ou "Acheter maintenant"
- Système de notation et d'avis utilisateurs (note de 1 à 5 étoiles + commentaire)
- Jeux similaires (recommandations)
- Badge "Meilleure vente", "Promo", "Nouveau"

---

### 3.3 Module Panier et Commandes

#### 3.3.1 Panier

- Ajout/suppression d'articles
- Modification des quantités (si multi-licences autorisées)
- Affichage du sous-total, remises éventuelles, total
- Persistance du panier (en session pour invités, en base pour utilisateurs connectés)
- Sauvegarde du panier entre les sessions

#### 3.3.2 Application de Codes Promo

- Code promo à saisir manuellement
- Types de remises : pourcentage, montant fixe, livraison offerte
- Conditions d'utilisation : date d'expiration, nombre d'utilisations max, montant minimum

#### 3.3.3 Processus de Commande (Checkout)

1. **Récapitulatif du panier** — liste des articles, total
2. **Informations de facturation** — adresse de facturation
3. **Paiement** — choix du moyen de paiement et saisie des informations
4. **Confirmation** — récapitulatif final avant validation
5. **Traitement** — paiement, attribution des clés, envoi de l'email de confirmation

#### 3.3.4 États d'une Commande

| État | Description |
|------|-------------|
| `PENDING` | Commande créée, en attente de paiement |
| `PAID` | Paiement confirmé |
| `PROCESSING` | Attribution des clés en cours |
| `COMPLETED` | Clés envoyées au client |
| `CANCELLED` | Commande annulée |
| `REFUNDED` | Remboursement effectué |
| `FAILED` | Échec du paiement |

---

### 3.4 Module Paiement

#### 3.4.1 Moyens de Paiement Supportés

- Carte bancaire (Visa, MasterCard) via Stripe ou PayPal
- PayPal
- Paiement mobile money (Orange Money, M-Pesa — optionnel selon la région cible)

#### 3.4.2 Sécurité des Paiements

- Conformité PCI-DSS : aucune donnée de carte stockée sur les serveurs
- Utilisation d'un prestataire de paiement certifié (Stripe recommandé)
- Vérification 3D Secure
- Détection de fraude basique (montant anormal, adresse suspecte)
- Webhook pour confirmer les paiements de manière asynchrone

#### 3.4.3 Facturation

- Génération automatique d'une facture PDF après paiement
- Envoi de la facture par email
- Disponibilité de la facture dans l'espace client

---

### 3.5 Module Distribution des Licences

- Après confirmation du paiement, attribution automatique d'une clé disponible pour chaque jeu acheté
- Envoi immédiat des clés par email (format HTML soigné)
- Affichage des clés dans l'espace client (bibliothèque)
- Mécanisme de réessai en cas d'échec d'envoi d'email
- Journalisation de chaque attribution de clé (audit trail)
- Gestion des litiges : possibilité de renvoyer une clé (admin uniquement)

---

### 3.6 Module Avis et Notations

- Publication d'un avis uniquement si le jeu a été acheté (avis vérifiés)
- Note de 1 à 5 étoiles + titre + commentaire
- Modération des avis (approbation manuelle ou automatique)
- Signalement d'un avis inapproprié
- Affichage de la note moyenne et du nombre d'avis

---

### 3.7 Module Administration (Backoffice)

#### 3.7.1 Tableau de Bord

- KPIs clés : chiffre d'affaires du jour/mois, nombre de commandes, nouveaux inscrits
- Graphiques : évolution des ventes, jeux les plus vendus
- Alertes : stock faible, commandes en attente, avis en attente de modération

#### 3.7.2 Gestion du Catalogue

- CRUD complet des jeux (création, modification, suppression logique)
- Import/export du catalogue en CSV/Excel
- Gestion des images (upload, recadrage)
- Activation/désactivation d'un jeu

#### 3.7.3 Gestion des Licences (Clés)

- Import de clés en masse (CSV)
- Suivi du stock par jeu
- Visualisation du statut de chaque clé
- Invalidation d'une clé

#### 3.7.4 Gestion des Commandes

- Liste des commandes avec filtres
- Détail d'une commande
- Modification manuelle du statut
- Remboursement via interface (déclenche le remboursement Stripe)

#### 3.7.5 Gestion des Utilisateurs

- Liste des utilisateurs avec filtres
- Détail d'un utilisateur (profil, historique)
- Activation/désactivation d'un compte
- Attribution/révocation de rôle

#### 3.7.6 Gestion des Promotions

- Création de codes promo
- Définition des promotions par jeu ou catégorie
- Suivi de l'utilisation des codes promo

#### 3.7.7 Rapports et Export

- Rapport de ventes (par période, par jeu, par catégorie)
- Export CSV/Excel/PDF
- Rapport d'inventaire des clés

---

### 3.8 Module Notifications

| Événement | Canal | Destinataire |
|-----------|-------|--------------|
| Inscription | Email | Utilisateur |
| Confirmation de commande | Email | Utilisateur |
| Envoi des clés | Email | Utilisateur |
| Récupération de mot de passe | Email | Utilisateur |
| Stock faible | Email / Dashboard | Admin |
| Nouvelle commande | Dashboard | Admin |
| Avis en attente de modération | Dashboard | Admin |

---

## 4. Spécifications Techniques

### 4.1 Stack Technologique

> **Décision d'architecture — Accès aux données**  
> Pour ce projet, **nous n'utilisons pas d'ORM** (pas de Hibernate, pas de Spring Data JPA).  
> La communication avec la base de données repose sur **des requêtes SQL écrites manuellement**, exécutées via **Spring JDBC** (`JdbcClient`).  
> Les scripts de schéma et les évolutions de structure sont versionnés avec **Flyway** ; les classes du domaine sont de simples **POJOs** (sans annotations de mapping).

#### Backend

| Composant | Technologie | Version recommandée |
|-----------|-------------|---------------------|
| Framework | Spring Boot | 3.x |
| Langage | Java | 21 (LTS) |
| Sécurité | Spring Security + JWT / Spring Session | — |
| OAuth2 | Spring Security OAuth2 Client | — |
| Persistance | Spring JDBC — `JdbcClient` (SQL manuel, sans ORM) | — |
| Base de données | PostgreSQL (production) / H2 (tests) | PostgreSQL 16 |
| Migration BDD | Flyway | — |
| Email | Spring Mail + Thymeleaf Templates | — |
| Paiement | Stripe Java SDK | — |
| Génération PDF | iText / Apache PDFBox | — |
| Cache | Redis (Spring Cache) | — |
| Messaging (optionnel) | Spring Events / RabbitMQ | — |
| Tests | JUnit 5 + Mockito + Testcontainers | — |
| Build | Maven ou Gradle | — |
| Documentation API | SpringDoc OpenAPI (Swagger UI) | — |

#### Frontend

**Option A — Serveur-side (recommandé pour démarrer rapidement) :**
| Composant | Technologie |
|-----------|-------------|
| Template Engine | Thymeleaf |
| CSS Framework | Bootstrap 5 / Tailwind CSS |
| JavaScript | Vanilla JS + Alpine.js |

**Option B — SPA découplée (pour scalabilité) :**
| Composant | Technologie |
|-----------|-------------|
| Framework JS | React 18 / Angular 17 |
| UI Library | Material UI / Ant Design |
| HTTP Client | Axios |
| State Management | Redux / Zustand |

#### Infrastructure & DevOps

| Composant | Technologie |
|-----------|-------------|
| Containerisation | Docker + Docker Compose |
| Reverse Proxy | Nginx |
| CI/CD | GitHub Actions / GitLab CI |
| Stockage des images | AWS S3 / Cloudinary |
| Monitoring | Spring Boot Actuator + Prometheus + Grafana |
| Logs | SLF4J + Logback + ELK Stack (optionnel) |

---

### 4.2 Architecture Applicative

L'application suivra une architecture **MVC en couches** (Layered Architecture) avec les couches suivantes :

```
┌──────────────────────────────────────────┐
│           Couche Présentation            │
│    (Controllers REST / Thymeleaf Views)  │
├──────────────────────────────────────────┤
│           Couche Service                 │
│    (Business Logic / DTOs / Mappers)     │
├──────────────────────────────────────────┤
│           Couche Repository (DAO)          │
│    (JdbcClient — requêtes SQL manuelles)  │
├──────────────────────────────────────────┤
│           Couche Domaine                 │
│    (POJOs / Value Objects / Enums)       │
└──────────────────────────────────────────┘
```

Les composants transversaux incluent :
- **Security Layer** : Filtres JWT, contrôle d'accès par annotations (`@PreAuthorize`)
- **Exception Handling** : `@ControllerAdvice` global avec réponses d'erreur standardisées
- **Validation** : Bean Validation (Jakarta Validation) sur les DTOs
- **Audit** : Champs `createdAt`, `updatedAt`, `createdBy` gérés explicitement dans les requêtes SQL ou via triggers PostgreSQL
- **Events** : Spring Application Events pour découpler la logique métier (ex : envoi d'email après paiement)

---

### 4.3 Modèle de Données (Entités Principales)

Les structures ci-dessous décrivent le **schéma relationnel cible**. Elles sont implémentées en base via **Flyway** et mappées côté Java vers des **POJOs** ; les relations (`ManyToMany`, `OneToMany`, etc.) sont résolues par des **jointures SQL explicites** dans la couche repository, sans ORM.

#### Entités Clés

```
User
 ├── id (UUID)
 ├── email (unique)
 ├── passwordHash
 ├── firstName, lastName
 ├── role (ENUM)
 ├── enabled (boolean)
 ├── emailVerified (boolean)
 └── createdAt, updatedAt

Game
 ├── id (UUID)
 ├── title, slug (unique)
 ├── shortDescription, longDescription
 ├── publisher, developer
 ├── releaseDate
 ├── basePrice (BigDecimal)
 ├── discountedPrice (nullable)
 ├── discountEndDate (nullable)
 ├── platform (ENUM : PC, PS5, XBOX, SWITCH)
 ├── pegiRating (ENUM)
 ├── status (ENUM : ACTIVE, INACTIVE, PRE_ORDER)
 ├── genres (ManyToMany)
 ├── tags (ManyToMany)
 └── images (OneToMany)

LicenseKey
 ├── id (UUID)
 ├── keyValue (encrypted)
 ├── game (ManyToOne)
 ├── status (ENUM : AVAILABLE, SOLD, RESERVED, INVALID)
 ├── order (ManyToOne, nullable)
 └── assignedAt (nullable)

Order
 ├── id (UUID)
 ├── orderNumber (unique, auto-généré)
 ├── user (ManyToOne)
 ├── status (ENUM)
 ├── totalAmount (BigDecimal)
 ├── paymentMethod
 ├── paymentIntentId (Stripe)
 ├── orderItems (OneToMany)
 └── createdAt, updatedAt

OrderItem
 ├── id (UUID)
 ├── order (ManyToOne)
 ├── game (ManyToOne)
 ├── licenseKey (OneToOne)
 ├── unitPrice (BigDecimal)
 └── quantity

Review
 ├── id (UUID)
 ├── user (ManyToOne)
 ├── game (ManyToOne)
 ├── rating (1-5)
 ├── title, content
 ├── status (ENUM : PENDING, APPROVED, REJECTED)
 └── createdAt

CartItem
 ├── id (UUID)
 ├── user (ManyToOne, nullable)
 ├── sessionId (nullable — pour invités)
 ├── game (ManyToOne)
 └── quantity

PromoCode
 ├── id (UUID)
 ├── code (unique)
 ├── discountType (ENUM : PERCENTAGE, FIXED_AMOUNT)
 ├── discountValue (BigDecimal)
 ├── minOrderAmount (nullable)
 ├── maxUsages (nullable)
 ├── usageCount
 ├── expiresAt (nullable)
 └── active (boolean)
```

---

### 4.4 API REST — Principaux Endpoints

#### Authentification

| Méthode | Endpoint | Description | Accès |
|---------|----------|-------------|-------|
| POST | `/api/auth/register` | Inscription | Public |
| POST | `/api/auth/login` | Connexion (JWT) | Public |
| POST | `/api/auth/refresh` | Renouvellement du token | Authentifié |
| POST | `/api/auth/logout` | Déconnexion | Authentifié |
| GET | `/api/auth/verify-email` | Vérification email | Public |
| POST | `/api/auth/forgot-password` | Demande de réinitialisation | Public |
| POST | `/api/auth/reset-password` | Réinitialisation | Public |

#### Catalogue

| Méthode | Endpoint | Description | Accès |
|---------|----------|-------------|-------|
| GET | `/api/games` | Liste paginée des jeux | Public |
| GET | `/api/games/{slug}` | Détail d'un jeu | Public |
| GET | `/api/games/search` | Recherche avec filtres | Public |
| GET | `/api/games/featured` | Jeux en vedette | Public |
| POST | `/api/admin/games` | Créer un jeu | Admin |
| PUT | `/api/admin/games/{id}` | Modifier un jeu | Admin |
| DELETE | `/api/admin/games/{id}` | Désactiver un jeu | Admin |

#### Panier

| Méthode | Endpoint | Description | Accès |
|---------|----------|-------------|-------|
| GET | `/api/cart` | Voir le panier | Authentifié / Session |
| POST | `/api/cart/items` | Ajouter un article | Authentifié / Session |
| PUT | `/api/cart/items/{id}` | Modifier une quantité | Authentifié / Session |
| DELETE | `/api/cart/items/{id}` | Supprimer un article | Authentifié / Session |
| POST | `/api/cart/promo` | Appliquer un code promo | Authentifié |

#### Commandes

| Méthode | Endpoint | Description | Accès |
|---------|----------|-------------|-------|
| POST | `/api/orders/checkout` | Créer une commande | Authentifié |
| GET | `/api/orders` | Historique des commandes | Authentifié |
| GET | `/api/orders/{id}` | Détail d'une commande | Authentifié (propriétaire) |
| POST | `/api/webhooks/stripe` | Webhook Stripe | Interne (signature) |

#### Bibliothèque

| Méthode | Endpoint | Description | Accès |
|---------|----------|-------------|-------|
| GET | `/api/library` | Liste des jeux achetés | Authentifié |
| GET | `/api/library/{gameId}/key` | Voir la clé d'un jeu | Authentifié |

---

### 4.5 Sécurité

- **Authentification** : JWT (Access Token 15min + Refresh Token 7 jours) avec stockage en HttpOnly Cookie
- **Autorisation** : Spring Security avec `@PreAuthorize` basé sur les rôles
- **Protection CSRF** : Activée pour les formulaires Thymeleaf, désactivée pour l'API REST stateless
- **CORS** : Configuration stricte avec liste blanche d'origines autorisées
- **Protection des données** : Chiffrement des clés d'activation en base (AES-256)
- **Injection SQL** : Prévenue via requêtes paramétrées (`JdbcClient`, paramètres nommés) — jamais de concaténation de chaînes utilisateur dans le SQL
- **XSS** : Échappement Thymeleaf automatique, Content Security Policy header
- **Rate Limiting** : Bucket4j pour limiter les tentatives de connexion et les appels API
- **HTTPS** : Obligatoire en production (certificat SSL/TLS via Let's Encrypt)
- **Secrets** : Variables d'environnement (pas de clés en dur dans le code), gestion via Spring Cloud Config ou Vault

---

### 4.6 Performance

- **Cache** : Redis pour le catalogue (TTL 10 minutes), sessions utilisateurs
- **Pagination** : Toutes les listes paginées (max 50 items par page)
- **Chargement ciblé** : Requêtes SQL dédiées par cas d'usage (éviter les `SELECT *` inutiles, pas de chargement automatique des associations)
- **Optimisation des requêtes** : Index sur les colonnes fréquemment filtrées (title, slug, status, platform), jointures SQL explicites et pagination en base (`LIMIT` / `OFFSET`)
- **Compression** : Gzip activé sur Nginx
- **CDN** : Images servies via CDN (Cloudinary ou AWS CloudFront)

---

## 5. Contraintes et Exigences Non-Fonctionnelles

### 5.1 Performances

| Indicateur | Cible |
|------------|-------|
| Temps de réponse API (P95) | < 300 ms |
| Temps de chargement page (LCP) | < 2.5 s |
| Disponibilité (uptime) | ≥ 99.5% |
| Transactions simultanées | 100 TPS minimum |
| Taille base de données initiale | Support jusqu'à 10 000 jeux, 100 000 utilisateurs |

### 5.2 Sécurité

- Conformité RGPD (droit à l'oubli, export des données personnelles)
- Aucune donnée bancaire stockée en clair
- Journaux d'audit pour toutes les actions sensibles (admin, paiements)
- Tests de sécurité (OWASP Top 10) avant mise en production

### 5.3 Maintenabilité

- Code couvert par des tests unitaires et d'intégration (couverture ≥ 80%)
- Documentation technique (JavaDoc, README, diagrammes d'architecture)
- Documentation API via Swagger UI accessible à `/swagger-ui.html`
- Respect des conventions de code (checkstyle, SonarQube)
- Architecture modulaire facilitant l'ajout de nouvelles fonctionnalités

### 5.4 Accessibilité

- Conformité WCAG 2.1 niveau AA
- Support des navigateurs modernes (Chrome, Firefox, Edge, Safari — 2 dernières versions)
- Interface responsive (mobile-first) : smartphones, tablettes, ordinateurs
- Support du mode sombre

---

## 6. Architecture des Modules Spring Boot

### 6.1 Structure des Packages

> **Consignes impératives — architecture et conventions Spring**
>
> L'application suit strictement une **architecture en 3 couches** :
> **controller → service → repository**, conformément aux **conventions et bonnes pratiques de la communauté Spring** (Spring Boot Reference Documentation, guides officiels, idiomes reconnus du framework).
>
> - **Respecter les conventions Spring** : injection par constructeur, annotations au bon endroit (`@RestController`, `@Service`, `@Repository`, `@Configuration`), configuration externalisée (`application.properties` / profils), gestion d'erreurs via `@ControllerAdvice` **uniquement pour les exceptions**, validation Jakarta sur les DTOs, etc.
> - **Ne pas créer** de packages ou dossiers hors de cette organisation (ex. : `viewmodel`, `advisor` dédié aux données de vue, etc.).
> - **Ne pas contourner** l'absence de backend en injectant des valeurs fictives via `@ControllerAdvice` / `@ModelAttribute` globaux : les données affichées dans les vues Thymeleaf doivent provenir du **controller**, alimenté par le **service**, lui-même branché sur le **repository**.
> - Le package **`config/`** sert uniquement à la **configuration Spring** (beans, sécurité, JDBC, cache, CORS, etc.) — notamment pour instancier ou brancher des **composants externes** à Spring. Il ne doit **pas** être utilisé pour préparer le modèle des pages ou simuler des données métier.
> - Tant que la logique métier n'est pas implémentée, les expressions Thymeleaf dynamiques restent **commentées** dans les templates (contenu statique de prévisualisation), en attendant que le flux controller → service → repository les alimente réellement.
> - **Ne pas prendre d'initiative** ajoutant des abstractions, dossiers ou patterns non prévus par ce document ni éloignés des pratiques habituelles de l'écosystème Spring.

```
com.gamestore
├── config/                  # Configurations Spring (Security, CORS, Cache, JDBC, etc.)
├── domain/                  # POJOs du domaine, Enums, Value Objects (sans ORM)
│   ├── model/
│   └── enums/
├── repository/              # DAO JDBC — requêtes SQL manuelles (JdbcClient)
├── service/                 # Logique métier
│   ├── impl/
│   └── interfaces/
├── web/                     # Controllers et DTOs
│   ├── controller/
│   │   ├── api/             # REST Controllers
│   │   └── view/            # Thymeleaf Controllers (si Option A)
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   └── mapper/              # MapStruct ou Mapper manuel
├── infrastructure/          # Services externes
│   ├── email/
│   ├── payment/
│   ├── storage/
│   └── security/
├── event/                   # Spring Application Events
├── exception/               # Custom Exceptions + GlobalExceptionHandler
└── util/                    # Classes utilitaires
```

### 6.2 Dépendances Maven Clés (pom.xml)

```xml
<!-- Spring Boot Starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Base de données -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>

<!-- Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Stripe -->
<dependency>
    <groupId>com.stripe</groupId>
    <artifactId>stripe-java</artifactId>
    <version>26.x.x</version>
</dependency>

<!-- MapStruct -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.6.3</version>
</dependency>

<!-- Swagger -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.7.0</version>
</dependency>

<!-- Tests -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 7. Plan de Développement

### 7.1 Phases du Projet

#### Phase 1 — Initialisation et Architecture (Semaines 1-2)
- Mise en place du projet Spring Boot
- Configuration Docker Compose (PostgreSQL, Redis, Adminer)
- Mise en place de Flyway et des premières migrations SQL
- Configuration Spring JDBC (`JdbcClient`) et premiers DAO avec requêtes SQL manuelles (sans ORM)
- Configuration Spring Security de base (JWT)
- Structure des packages et conventions de code

#### Phase 2 — Gestion des Utilisateurs (Semaines 3-4)
- Module d'authentification complet (inscription, connexion, JWT)
- Vérification email et récupération de mot de passe
- Profil utilisateur
- Tests unitaires du module Auth

#### Phase 3 — Catalogue de Jeux (Semaines 5-7)
- CRUD complet du catalogue (admin)
- Page de listing avec filtres et pagination
- Page de détail d'un jeu
- Système de notation et d'avis
- Upload d'images (Cloudinary)

#### Phase 4 — Panier et Commandes (Semaines 8-10)
- Module panier (invité + connecté)
- Processus de checkout
- Intégration Stripe (Payment Intent + Webhooks)
- Distribution automatique des clés
- Notifications email (confirmation, clés)

#### Phase 5 — Administration (Semaines 11-12)
- Tableau de bord admin
- Gestion du stock de clés (import CSV)
- Gestion des commandes et remboursements
- Gestion des promotions et codes promo
- Rapports et export

#### Phase 6 — Qualité et Déploiement (Semaines 13-14)
- Tests d'intégration (Testcontainers)
- Tests de performance basiques
- Audit de sécurité (OWASP)
- Configuration CI/CD (GitHub Actions)
- Déploiement sur environnement de staging
- Documentation finale

### 7.2 Jalons Clés (Milestones)

| Jalon | Livrable | Semaine |
|-------|----------|---------|
| M1 | Environnement de développement fonctionnel | 2 |
| M2 | Authentification complète (Register/Login/JWT) | 4 |
| M3 | Catalogue navigable avec filtres | 7 |
| M4 | Premier achat de bout en bout (Checkout + Clé reçue) | 10 |
| M5 | Backoffice admin complet | 12 |
| M6 | Mise en production (v1.0) | 14 |

---

## 8. Tests et Qualité

### 8.1 Stratégie de Test

| Type de test | Outil | Couverture cible |
|--------------|-------|------------------|
| Tests unitaires (Services) | JUnit 5 + Mockito | ≥ 80% |
| Tests d'intégration (Repository / DAO) | Testcontainers + PostgreSQL | Requêtes SQL critiques |
| Tests d'intégration (API) | MockMvc / RestAssured | Tous les endpoints |
| Tests de sécurité | Spring Security Test | Contrôle d'accès |
| Tests de performance | JMeter / Gatling | Scénarios critiques |

### 8.2 Scénarios de Test Critiques

- Inscription → Vérification email → Connexion
- Ajout au panier → Checkout → Paiement Stripe (mode test) → Réception des clés
- Tentatives de connexion échouées → Verrouillage du compte
- Accès non autorisé aux routes admin
- Import de clés en masse → Vérification du stock
- Remboursement d'une commande → Invalidation des clés

---

## 9. Livrables Attendus

| Livrable | Description |
|----------|-------------|
| Code source | Repository Git (GitHub/GitLab) avec historique de commits |
| Documentation API | Swagger UI intégré (`/swagger-ui.html`) |
| Docker Compose | Fichier pour lancer l'environnement complet en local |
| Documentation technique | README, diagrammes d'architecture (PlantUML/Draw.io) |
| Base de données | Scripts Flyway de migration + données de démonstration |
| Manuel d'administration | Guide d'utilisation du backoffice |
| Rapport de tests | Rapport de couverture de tests (JaCoCo) |

---

## 10. Risques Identifiés

| Risque | Probabilité | Impact | Mitigation |
|--------|-------------|--------|------------|
| Fraude sur les clés (revente) | Moyenne | Élevé | Limitation d'achat par compte, vérification d'email, détection d'anomalies |
| Panne du prestataire de paiement | Faible | Élevé | Gestion des erreurs robuste, statut PENDING en cas de timeout, réconciliation par webhook |
| Stock de clés insuffisant | Moyenne | Moyen | Alertes de stock faible, blocage de la commande si stock = 0 |
| Violation de données | Faible | Très élevé | Chiffrement, audit, RGPD, tests de sécurité |
| Montée en charge imprévue | Faible | Moyen | Architecture stateless (horizontal scaling), cache Redis |

---

## Annexe A — Glossaire

| Terme | Définition |
|-------|------------|
| **Licence / Clé d'activation** | Code alphanumérique unique permettant d'activer un jeu sur une plateforme (Steam, Epic, etc.) |
| **PEGI** | Pan European Game Information — système européen de classification des jeux par âge |
| **JWT** | JSON Web Token — standard ouvert pour l'authentification stateless |
| **PCI-DSS** | Payment Card Industry Data Security Standard — standard de sécurité pour les paiements par carte |
| **Webhook** | Mécanisme de notification HTTP envoyé par un service tiers (ex : Stripe) vers notre application |
| **Flyway** | Outil de migration de base de données pour Java |
| **JdbcClient** | API fluide Spring (6.1+) pour exécuter des requêtes SQL paramétrées sans ORM |
| **DAO (Repository)** | Couche d'accès aux données : une classe par agrégat, SQL écrit à la main |
| **DTO** | Data Transfer Object — objet utilisé pour transférer des données entre couches |
| **MapStruct** | Framework Java pour la conversion automatique entre entités et DTOs |
| **Testcontainers** | Bibliothèque permettant de démarrer des instances Docker (PostgreSQL, Redis) pendant les tests |

---

*Document rédigé pour le projet GameStore Platform — Spring Boot E-Commerce*  
*Version 1.0 — Juin 2026*
