# Gamestore — Projet Spring Framework (Module 01)

Application minimale de gestion de jeux vidéo : interface web Thymeleaf + API REST JSON, CRUD sur trois entités liées par des jointures SQL.

---

## Spécifications d'examen

Le projet respecte les exigences du module Spring Framework :

| Exigence | Implémentation |
|----------|----------------|
| Application Web + API | Pages Thymeleaf (`/`, `/genres`, `/tags`) et endpoints REST (`/api/*`) |
| Architecture 3 couches | `@Controller` → `@Service` → `@Repository` |
| Base relationnelle (≥ 3 tables + jointures) | `games`, `genres`, `tags` + tables de liaison `game_genres`, `game_tags` |
| `JdbcClient` | Repositories basés sur `JdbcClient` |
| **simpleflatmapper** | Mapping via `JdbcTemplateMapperFactory` et `DomainRowMappers` |
| CRUD complet | Create, Read, Update, Delete sur jeux, genres et tags |

---

## Prérequis

- **Java 25**
- **Maven 3.9+**
- **Docker** (MySQL en développement)
- **Node.js 22+** (compilation Tailwind CSS, optionnel en dev si le CSS est déjà généré)

---

## Démarrage rapide

### 1. Base de données MySQL

```bash
docker compose up -d
```

Services disponibles :

| Service | URL | Identifiants |
|---------|-----|--------------|
| MySQL | `localhost:3306` | base `gamestore`, user `gamestore` / `gamestore` |
| Adminer | http://localhost:8080 | serveur `mysql`, user `gamestore` / `gamestore` |

Les migrations Flyway (`V1`, `V2`) créent le schéma et insèrent des données de démonstration (Star Quest, Dungeon Tales, genres, tags).

### 2. Lancer l'application

```bash
mvn spring-boot:run
```

L'application écoute sur **http://localhost:8083**.

Le profil actif par défaut est `dev` (`application-dev.properties` → MySQL local).

### 3. Interface web

| Page | URL |
|------|-----|
| Liste des jeux | http://localhost:8083/ |
| Genres | http://localhost:8083/genres |
| Tags | http://localhost:8083/tags |

Navigation, tableaux et formulaires sont stylisés avec **Tailwind CSS v4**.

### 4. API REST

Fichier de requêtes prêt à l'emploi : [`http/gamestore-api.http`](http/gamestore-api.http) (extension REST Client pour VS Code).

Exemples :

```http
GET  http://localhost:8083/api/games
POST http://localhost:8083/api/games
GET  http://localhost:8083/api/genres
GET  http://localhost:8083/api/tags
```

Aucune authentification requise.

---

## Modèle de données

```
genres ──< game_genres >── games ──< game_tags >── tags
```

| Table | Description |
|-------|-------------|
| `games` | Jeu (titre, slug, description, prix, plateforme) |
| `genres` | Genre (slug, label) |
| `tags` | Tag (slug, label) |
| `game_genres` | Association N-N jeu ↔ genre |
| `game_tags` | Association N-N jeu ↔ tag |

Les jeux chargés via `GameRepository` incluent les labels de genres et tags (requêtes avec jointures).

---

## Architecture

```
src/main/java/com/examen/gamestore/
├── config/              JdbcMapperConfig (bean SFM)
├── domain/model/        Game, Genre, Tag
├── repository/          GameRepository, GenreRepository, TagRepository
│   └── mapping/         DomainRowMappers (simpleflatmapper)
├── service/             GameService, GenreService, TagService + impl
├── util/                JdbcUuid, SlugUtils
└── web/
    ├── controller/      GameWebController, GenreWebController, TagWebController
    │                      GameApiController
    ├── dto/               GameForm, GenreForm, TagForm
    └── mapper/            GameMapper, GenreMapper, TagMapper
```

**Flux typique :** le contrôleur reçoit un DTO → le service applique la logique métier → le repository exécute du SQL via `JdbcClient` → SFM mappe les lignes vers les entités.

---

## Endpoints

### Web (Thymeleaf)

| Méthode | URL | Action |
|---------|-----|--------|
| GET | `/` | Liste des jeux |
| GET/POST | `/games/new`, `/games`, `/games/{id}/edit`, `/games/{id}/delete` | CRUD jeux |
| GET/POST | `/genres`, `/genres/new`, `/genres/{id}/edit`, `/genres/{id}/delete` | CRUD genres |
| GET/POST | `/tags`, `/tags/new`, `/tags/{id}/edit`, `/tags/{id}/delete` | CRUD tags |

### API REST (JSON)

| Ressource | Endpoints |
|-----------|-----------|
| Jeux | `GET/POST /api/games`, `GET/PUT/DELETE /api/games/{id}` |
| Genres | `GET/POST /api/genres`, `PUT/DELETE /api/genres/{id}` |
| Tags | `GET/POST /api/tags`, `PUT/DELETE /api/tags/{id}` |

Corps JSON d'un jeu (exemple) :

```json
{
  "title": "Mon Jeu",
  "slug": "mon-jeu",
  "description": "Description du jeu.",
  "price": 19.99,
  "platform": "PC",
  "genreSlugs": ["action", "rpg"],
  "tagSlugs": ["indie"]
}
```

---

## Données de démonstration

Identifiants fixes (seed Flyway V2), utiles pour tester l'API :

| Entité | ID |
|--------|----|
| Star Quest | `33333333-3333-3333-3333-333333333301` |
| Dungeon Tales | `33333333-3333-3333-3333-333333333302` |
| Genre Action | `11111111-1111-1111-1111-111111111101` |
| Genre RPG | `11111111-1111-1111-1111-111111111102` |
| Tag Indie | `22222222-2222-2222-2222-222222222202` |

---

## Tests

Les tests utilisent **H2** en mode MySQL (profil `test`), sans Docker :

```bash
mvn test
```

| Test | Rôle |
|------|------|
| `GamestoreApplicationTests` | Chargement du contexte Spring |
| `GameCrudIntegrationTest` | Cycle CRUD complet via l'API REST |

---

## Frontend (Tailwind CSS)

Le CSS est compilé depuis `src/main/resources/static/css/input.css` vers `styles.css`.

```bash
npm install
npm run build:css    # build unique
npm run watch:css    # recompilation à chaque modification
```

Au build Maven, le plugin `frontend-maven-plugin` exécute automatiquement `npm install` et `npm run build:css`.

---

## Stack technique

| Composant | Version / outil |
|-----------|-------------------|
| Spring Boot | 4.1.0 |
| Java | 25 |
| Base de données (dev) | MySQL 8.4 (Docker) |
| Base de données (test) | H2 |
| Migrations | Flyway |
| Mapping JDBC | simpleflatmapper 9.0.2 |
| Vues | Thymeleaf |
| CSS | Tailwind CSS 4 |

---

## Configuration

| Fichier | Rôle |
|---------|------|
| `application.properties` | Port 8083, profil `dev` |
| `application-dev.properties` | Connexion MySQL locale |
| `application-test.properties` | H2 en mémoire pour les tests |

---

## Commandes utiles

```bash
docker compose up -d          # Démarrer MySQL + Adminer
mvn spring-boot:run           # Lancer l'application
mvn test                      # Exécuter les tests
npm run watch:css             # Surveiller les changements CSS
docker compose down -v        # Arrêter et supprimer les volumes
```
