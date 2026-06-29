# GameStore Platform

Plateforme e-commerce de vente de **licences de jeux vidéo** (clés d'activation numériques).  
Le projet combine une **interface web** (Thymeleaf) et une **API REST** documentée (OpenAPI / Swagger), construite avec **Spring Boot 4** et **Java 25**.

| | |
|---|---|
| **URL locale** | http://localhost:8083 |
| **Swagger UI** | http://localhost:8083/swagger-ui.html |
| **Adminer (BDD)** | http://localhost:8080 |
| **Fichier de requêtes HTTP** | [`http/gamestore-api.http`](http/gamestore-api.http) |

---

## Table des matières

1. [Fonctionnalités](#fonctionnalités)
2. [Stack technique](#stack-technique)
3. [Prérequis](#prérequis)
4. [Installation et démarrage](#installation-et-démarrage)
5. [Configuration](#configuration)
6. [Comptes et données de démonstration](#comptes-et-données-de-démonstration)
7. [Interface web](#interface-web)
8. [API REST](#api-rest)
9. [Tester l'API](#tester-lapi)
10. [Tests automatisés](#tests-automatisés)
11. [Structure du projet](#structure-du-projet)
12. [Architecture](#architecture)
13. [Sécurité](#sécurité)
14. [Développement](#développement)
15. [Limites connues](#limites-connues)

---

## Fonctionnalités

### Interface web (Thymeleaf)

| Module | Description |
|--------|-------------|
| **Catalogue** | Accueil, liste des jeux, filtres, promotions, fiche produit |
| **Authentification** | Inscription, connexion, mot de passe oublié / réinitialisation |
| **Panier** | Ajout, modification des quantités, codes promo, persistance session |
| **Checkout** | Commande simulée, attribution automatique des clés, e-mail de confirmation |
| **Compte client** | Profil, bibliothèque, historique et détail des commandes |
| **Avis** | Notation et commentaires sur les jeux achetés |
| **Administration** | Dashboard, jeux, clés, commandes, utilisateurs, promos, genres, tags, avis, rapports CSV |

### API REST (`/api/**`)

| Module | Description |
|--------|-------------|
| **Auth** | Inscription, login JWT, refresh, logout, mot de passe oublié |
| **Catalogue** | Liste, recherche, jeux en vedette, détail par slug |
| **Panier** | CRUD panier (invité via `X-Cart-Session` ou connecté via JWT) |
| **Commandes** | Checkout, historique, détail |
| **Bibliothèque** | Jeux achetés, clés d'activation |
| **Admin** | CRUD jeux (création, modification, désactivation) |

---

## Stack technique

| Composant | Technologie |
|-----------|-------------|
| Framework | Spring Boot 4.1 |
| Langage | Java 25 |
| Vue web | Thymeleaf + Tailwind CSS 4 |
| Persistance | Spring JDBC (`JdbcClient`) — **sans ORM** |
| Base de données | PostgreSQL 16 (dev) / H2 en mémoire (tests) |
| Migrations | Flyway |
| Sécurité | Spring Security — formulaire (web) + JWT (API) |
| Documentation API | SpringDoc OpenAPI 2.7 |
| E-mail | Spring Mail (`LoggingEmailService` en dev) |
| Build | Maven |
| Conteneurs | Docker Compose (PostgreSQL, Redis, Adminer) |

> **Redis** est présent dans Docker Compose mais **n'est pas encore utilisé** par l'application Java.

---

## Prérequis

- **JDK 25**
- **Maven 3.9+** (ou `./mvnw`)
- **Docker Desktop** (pour PostgreSQL en développement)
- **Node.js** (optionnel en local — Maven installe Node via `frontend-maven-plugin` pour compiler le CSS)

---

## Installation et démarrage

### 1. Cloner le dépôt

```bash
git clone <url-du-repo>
cd gamestore
```

### 2. Démarrer la base de données

```bash
docker compose up -d
```

Services lancés :

| Service | Port | Identifiants |
|---------|------|--------------|
| PostgreSQL | 5432 | `gamestore` / `gamestore` — base `gamestore` |
| Adminer | 8080 | Système : PostgreSQL, serveur : `postgres`, user/pass : `gamestore` |
| Redis | 6379 | *(non utilisé par l'app pour l'instant)* |

### 3. Lancer l'application

```bash
./mvnw spring-boot:run
```

Au premier démarrage, Maven installe Node/npm et compile Tailwind CSS automatiquement.

L'application écoute sur **http://localhost:8083**.  
Flyway applique les migrations et insère les **données de démonstration**.

### 4. Vérifier que tout fonctionne

- Page d'accueil : http://localhost:8083/
- Swagger : http://localhost:8083/swagger-ui.html
- API catalogue : http://localhost:8083/api/games?page=1

---

## Configuration

Fichiers principaux :

| Fichier | Rôle |
|---------|------|
| `application.properties` | Port, profil actif, JWT, Swagger |
| `application-dev.properties` | Datasource PostgreSQL locale |
| `application-test.properties` | H2 en mémoire pour les tests |

Variables importantes (`application.properties`) :

```properties
server.port=8083
spring.profiles.active=dev

app.jwt.secret=${JWT_SECRET:change-me-in-production-min-256-bits-long-secret-key}
app.jwt.access-token-expiration=15m
app.jwt.refresh-token-expiration=7d
app.auth.auto-verify-email=true
```

En production, définir **`JWT_SECRET`** via variable d'environnement (clé d'au moins 256 bits).

---

## Comptes et données de démonstration

### Utilisateurs (seed Flyway `V3`)

| Rôle | E-mail | Mot de passe |
|------|--------|--------------|
| Admin | `admin@gamestore.local` | `Admin123!` |
| Client | `demo@gamestore.local` | `Demo1234!` |

### Jeux (extrait — seed `V2`)

| Jeu | Slug | ID |
|-----|------|----|
| Cyberpunk 2077 | `cyberpunk-2077` | `c3000001-0000-4000-8000-000000000001` |
| Elden Ring | `elden-ring` | `c3000001-0000-4000-8000-000000000002` |

6 jeux, genres, tags, avis, clés de licence et code promo **`GAME10`** (-10 %) sont également pré-chargés.

---

## Interface web

### Pages publiques

| URL | Description |
|-----|-------------|
| `/` | Accueil (jeux en vedette, bestsellers) |
| `/catalogue` | Catalogue avec filtres et pagination |
| `/promotions` | Jeux en promotion |
| `/jeu/{slug}` | Fiche produit + avis |
| `/panier` | Panier |
| `/login`, `/register` | Authentification |
| `/mot-de-passe-oublie` | Demande de réinitialisation |
| `/a-propos` | Page statique |

### Pages authentifiées (client)

| URL | Description |
|-----|-------------|
| `/checkout` | Paiement (simulé) |
| `/checkout/confirmation` | Confirmation de commande |
| `/compte/profil` | Profil utilisateur |
| `/compte/bibliotheque` | Jeux achetés et clés |
| `/compte/commandes` | Historique des commandes |

### Backoffice (`ROLE_ADMIN` ou `ROLE_SUPERADMIN`)

| URL | Description |
|-----|-------------|
| `/admin/dashboard` | Tableau de bord (KPIs) |
| `/admin/games` | Gestion du catalogue |
| `/admin/licenses` | Import et gestion des clés |
| `/admin/orders` | Commandes |
| `/admin/users` | Utilisateurs |
| `/admin/promos` | Codes promo |
| `/admin/genres`, `/admin/tags` | Taxonomie |
| `/admin/reviews` | Modération des avis |
| `/admin/reports` | Export CSV des commandes |

---

## API REST

Base URL : `http://localhost:8083/api`

### Authentification — `/api/auth`

| Méthode | Endpoint | Accès | Description |
|---------|----------|-------|-------------|
| POST | `/register` | Public | Inscription → `201` + profil |
| POST | `/login` | Public | Connexion → JWT (`accessToken`, `refreshToken`) |
| POST | `/refresh` | Public | Renouvellement du token |
| POST | `/logout` | Public | Révoque le refresh token → `204` |
| POST | `/forgot-password` | Public | Envoi e-mail de réinitialisation → `204` |
| POST | `/reset-password` | Public | Réinitialisation avec token → `204` |
| GET | `/verify-email?token=` | Public | **501** — non implémenté |

**Exemple login :**

```json
POST /api/auth/login
{
  "email": "demo@gamestore.local",
  "password": "Demo1234!"
}
```

**Réponse :**

```json
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ...",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

Utiliser ensuite : `Authorization: Bearer <accessToken>`

### Catalogue — `/api/games` (public)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/games` | Liste paginée (`page`, `pageSize`) |
| GET | `/games/search` | Recherche + filtres (`q`, `genre`, `platform`, `priceMin`, `priceMax`, `sort`, `promoOnly`) |
| GET | `/games/featured` | Jeux en vedette (`limit`) |
| GET | `/games/{slug}` | Détail d'un jeu |

### Panier — `/api/cart`

| Méthode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| GET | `/cart` | Invité ou JWT | Contenu du panier |
| POST | `/cart/items` | Invité ou JWT | Ajouter un jeu `{ "gameId": "uuid" }` |
| PUT | `/cart/items/{id}` | Invité ou JWT | Modifier quantité `{ "quantity": 2 }` |
| DELETE | `/cart/items/{id}` | Invité ou JWT | Supprimer un article |
| POST | `/cart/promo` | JWT | Appliquer un code `{ "code": "GAME10" }` |

**Panier invité :** le serveur renvoie l'en-tête `X-Cart-Session` à conserver sur les requêtes suivantes.

### Commandes — `/api/orders` (JWT obligatoire)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/orders/checkout` | Créer une commande → `201` |
| GET | `/orders` | Historique |
| GET | `/orders/{id}` | Détail (propriétaire uniquement → `404` sinon) |

### Bibliothèque — `/api/library` (JWT obligatoire)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/library` | Jeux achetés |
| GET | `/library/{gameId}/key` | Clé d'activation d'un jeu possédé |

### Admin — `/api/admin/games` (JWT + rôle ADMIN)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/admin/games` | Créer un jeu → `201` |
| PUT | `/admin/games/{id}` | Modifier un jeu |
| DELETE | `/admin/games/{id}` | Désactiver un jeu → `204` |

### Codes de réponse API

Les erreurs API renvoient un JSON structuré (`ApiErrorResponse`) :

```json
{
  "timestamp": "2026-06-29T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Jeu introuvable : ...",
  "path": "/api/games/..."
}
```

---

## Tester l'API

### Option 1 — Fichier HTTP (recommandé)

Le fichier [`http/gamestore-api.http`](http/gamestore-api.http) contient toutes les requêtes prêtes à l'emploi, compatible **VS Code REST Client** ou **Cursor REST Client Plus**.

**Extension recommandée sous Cursor :** [REST Client Plus](https://open-vsx.org/extension/kit1211/rest-client-plus) — l'extension officielle `humao.rest-client` a un bug d'affichage des réponses sous Cursor (panneau qui s'ouvre et se referme).

**Contournement si vous gardez l'extension officielle :**

1. Ouvrir `gamestore-api.http`
2. `Ctrl+\` pour créer un split éditeur à droite
3. Cliquer **Send Request**
4. Consulter l'historique avec `Ctrl+Alt+H` si le panneau ne s'affiche pas

Les requêtes nommées (`# @name loginDemo`, etc.) alimentent les références du type `{{loginDemo.response.body.accessToken}}`. **Exécuter d'abord la requête source**, puis les requêtes dépendantes.

### Option 2 — Swagger UI

http://localhost:8083/swagger-ui.html

1. Appeler `POST /api/auth/login` pour obtenir un token
2. Cliquer **Authorize** et saisir `Bearer <accessToken>`
3. Tester les endpoints protégés

### Option 3 — curl

```bash
# Catalogue
curl http://localhost:8083/api/games?page=1

# Login
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@gamestore.local","password":"Demo1234!"}'
```

### Ordre de test recommandé

#### Phase 1 — Public (sans auth)

1. `GET /api/games` — liste
2. `GET /api/games/search` — filtres
3. `GET /api/games/featured` — vedette
4. `GET /api/games/elden-ring` — détail
5. `GET /api/games/jeu-inexistant` — **404**

#### Phase 2 — Authentification

6. `POST /api/auth/register`
7. `POST /api/auth/login` (`loginDemo`)
8. Login invalide — **401**
9. `POST /api/auth/refresh`
10. `POST /api/auth/forgot-password`
11. `GET /api/auth/verify-email` — **501**
12. `POST /api/auth/logout`

#### Phase 3 — Panier invité

13. `POST /api/cart/items` → récupérer `X-Cart-Session`
14. `GET /api/cart`
15. `PUT /api/cart/items/{id}`
16. `DELETE /api/cart/items/{id}`

#### Phase 4 — Parcours client (après `loginDemo`)

17. Ajouter au panier (connecté)
18. Appliquer promo `GAME10`
19. Préparer le panier (si vide)
20. `POST /api/orders/checkout` — **201**
21. `GET /api/orders` + `GET /api/orders/{id}`
22. `GET /api/library` + clé d'activation
23. Checkout panier vide — **400**

#### Phase 5 — Admin (après `loginAdmin`)

24. `POST /api/admin/games`
25. `PUT /api/admin/games/{id}`
26. `DELETE /api/admin/games/{id}`
27. Création par un non-admin — **403**

> Ne pas exécuter toutes les requêtes d'un coup : certaines vident le panier ou révoquent le token.

---

## Tests automatisés

Les tests d'intégration utilisent **H2 en mémoire** (profil `test`) avec les mêmes migrations Flyway.

```bash
# Tous les tests
./mvnw test

# Tests API uniquement
./mvnw test -Dtest=AuthApiIntegrationTest,GameApiIntegrationTest,CartApiIntegrationTest,OrderApiIntegrationTest,AdminGameApiIntegrationTest
```

| Classe de test | Couverture |
|----------------|------------|
| `AuthApiIntegrationTest` | Register, login, refresh, logout |
| `GameApiIntegrationTest` | Catalogue public |
| `CartApiIntegrationTest` | Panier invité et connecté, promo |
| `OrderApiIntegrationTest` | Checkout, bibliothèque, clés |
| `AdminGameApiIntegrationTest` | CRUD admin, contrôle d'accès |
| `CartServiceTest`, `GameReviewServiceTest`, … | Tests unitaires services |

---

## Structure du projet

```
gamestore/
├── docker-compose.yml          # PostgreSQL, Redis, Adminer
├── http/
│   └── gamestore-api.http      # Collection de requêtes REST Client
├── pom.xml
├── package.json                # Tailwind CSS (build frontend)
└── src/
    ├── main/
    │   ├── java/com/examen/gamestore/
    │   │   ├── config/         # Security, OpenAPI, Thymeleaf
    │   │   ├── domain/         # POJOs et enums (sans ORM)
    │   │   ├── repository/     # JDBC — SQL manuel (JdbcClient)
    │   │   ├── service/        # Logique métier
    │   │   │   ├── impl/
    │   │   │   └── cart/       # Scopes panier (session web / API)
    │   │   ├── web/
    │   │   │   ├── controller/
    │   │   │   │   ├── api/    # REST Controllers
    │   │   │   │   └── view/   # Thymeleaf Controllers
    │   │   │   ├── dto/
    │   │   │   └── mapper/
    │   │   ├── infrastructure/ # E-mail, sécurité JWT
    │   │   ├── exception/      # GlobalExceptionHandler (web) + ApiExceptionHandler (API)
    │   │   └── util/
    │   └── resources/
    │       ├── application*.properties
    │       ├── db/migration/   # Flyway V1 → V6
    │       ├── static/         # CSS, JS
    │       └── templates/      # Vues Thymeleaf
    └── test/
        ├── java/               # Tests JUnit 5 + MockMvc
        └── resources/
            └── application-test.properties
```

---

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│  Présentation                                           │
│  • Controllers view (Thymeleaf + sessions)              │
│  • Controllers api (JSON + JWT stateless)               │
├─────────────────────────────────────────────────────────┤
│  Service (interfaces + impl)                            │
│  • AuthService, CartService, OrderService, GameService… │
├─────────────────────────────────────────────────────────┤
│  Repository (JdbcClient — requêtes SQL paramétrées)     │
├─────────────────────────────────────────────────────────┤
│  Domain (POJOs, enums)                                  │
└─────────────────────────────────────────────────────────┘
         │                              │
    PostgreSQL                      Flyway migrations
```

### Double accès panier

| Contexte | Mécanisme | Classe |
|----------|-----------|--------|
| Site web | Session HTTP | `HttpSessionCartScope` |
| API REST invité | Header `X-Cart-Session` | `ApiCartScope` |
| API REST connecté | JWT → `userId` | `ApiCartScope` |

### Double chaîne de sécurité

| Chaîne | Périmètre | Mode |
|--------|-----------|------|
| `apiSecurityFilterChain` | `/api/**` | Stateless JWT |
| `mvcSecurityFilterChain` | Pages Thymeleaf | Formulaire + session |

---

## Sécurité

| Aspect | Implémentation |
|--------|----------------|
| Mots de passe | BCrypt |
| API | JWT (access 15 min, refresh 7 jours, hashé en BDD) |
| Web | Spring Security form login (email + mot de passe) |
| Rôles | `ROLE_USER`, `ROLE_ADMIN`, `ROLE_SUPERADMIN` |
| CSRF | Activé (web), désactivé (API REST) |
| SQL | Requêtes paramétrées via `JdbcClient` |

Routes API publiques : auth (register/login/refresh/logout/forgot/reset), catalogue (`GET /api/games/**`), panier (`/api/cart/**`).  
Routes protégées : `/api/orders/**`, `/api/library/**`.  
Admin : `/api/admin/**` → rôles `ADMIN` ou `SUPERADMIN`.

---

## Développement

### Compiler le CSS Tailwind manuellement

```bash
npm install
npm run build:css
```

Mode watch pendant le développement front :

```bash
npm run watch:css
```

### Profils Spring

| Profil | Usage |
|--------|-------|
| `dev` (défaut) | PostgreSQL via Docker |
| `test` | H2 en mémoire (tests Maven) |

### E-mails en développement

Les e-mails (inscription, commande, reset password) sont loggés dans la console via `LoggingEmailService`. Surveillez les logs Spring pour récupérer les tokens de réinitialisation.

### Migrations Flyway

| Version | Contenu |
|---------|---------|
| V1 | Schéma initial |
| V2 | Données démo (jeux, genres, tags) |
| V3 | Comptes démo + tokens reset password |
| V4 | Avis sur les jeux |
| V5 | Panier, commandes, clés, promo GAME10 |
| V6 | Refresh tokens JWT |

---

## Limites connues

Fonctionnalités **prévues dans le cahier des charges** mais **non implémentées** ou **partielles** :

| Fonctionnalité | État |
|----------------|------|
| Paiement Stripe réel | Colonne BDD présente, checkout **simulé** |
| Vérification e-mail API | Retourne **501** |
| OAuth2 (Google, Steam…) | Non implémenté |
| Cache Redis | Service Docker présent, non branché |
| Rate limiting | Non implémenté |
| Chiffrement AES des clés en BDD | Clés stockées en clair |
| CAPTCHA à l'inscription | Non implémenté |
| Factures PDF | Non implémenté |

---

## Nettoyage du dépôt

Dossiers **générés automatiquement** — ne pas versionner, supprimables sans impact sur le code source :

| Dossier | Généré par |
|---------|------------|
| `target/` | Maven (compilation, tests) |
| `node_modules/` | npm (Tailwind) |
| `node/` | frontend-maven-plugin (Node embarqué Maven) |

Tous les fichiers source ajoutés pour l'API REST (`AuthApiController`, `JwtService`, `http/gamestore-api.http`, tests d'intégration, etc.) sont **référencés et utilisés** — aucune suppression n'est nécessaire.

---

*GameStore Platform — Spring Boot E-Commerce — Juin 2026*
