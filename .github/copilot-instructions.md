# Copilot Instructions

## Project context

This is the reference implementation for the Bachelor's thesis in Computer Engineering **"Database relazionali e NoSQL: progettazione e confronto attraverso l'implementazione di un sistema backend"**. It is a purely demonstrative project whose goal is to **highlight the differences between SQL and NoSQL implementations** (data modeling, query strategies, constraints, performance) by exposing the same REST API on top of two persistence backends.

Implications for any change you make:
- The project must exemplify Spring Boot / Java **best practices** — code, tests, and docs are part of the thesis deliverable and are read as such. Prefer idiomatic, textbook-clean solutions over clever shortcuts.
- The two backends (PostgreSQL via JPA, MongoDB via Spring Data MongoDB) are **not switched at runtime in the same running instance**: the datasource is chosen **once at startup** via the `APP_DATASOURCE` environment variable (`postgres` or `mongo`) and the app runs against that backend for its lifetime. Do not introduce hot-swapping, per-request routing, or dual-write patterns — they contradict the thesis narrative.
- Keep the SQL and NoSQL implementations **symmetric and comparable**: same domain, same API contract, same service boundaries. When you add a feature on one side, mirror it on the other so the comparison remains meaningful. Idiomatic differences (embedded documents vs joins, native ids, indexes, etc.) are the point — surface them, don't paper over them.
- Performance is an explicit axis of comparison — preserve the Actuator/cache metrics wiring and AOP timing hooks; don't remove observability code as "unused".

## Overview

Spring Boot 3 / Java 21 backend that exposes the same REST API on top of **either** PostgreSQL (JPA) **or** MongoDB (Spring Data MongoDB), selected at startup via `APP_DATASOURCE`.

## Build, test, run

Use the Maven wrapper (`./mvnw` on Linux, `mvnw.cmd` on Windows).

- Build (runs OpenAPI codegen + Spotless apply as part of the lifecycle): `./mvnw clean package`
- Full verify with coverage gate (JaCoCo requires ≥80% line and ≥70% branch on the bundle): `./mvnw verify`
- Skip tests: `-DskipTests`
- Format only: `./mvnw spotless:apply` (Google Java Format via Spotless is bound to the build; CI-safe check is `spotless:check`)
- Single test class: `./mvnw test -Dtest=ProductMapperTest`
- Single test method: `./mvnw test -Dtest=PostgresTest#contextLoads`
- Run locally in Docker - start only the relevant DB via compose override:
  - PowerShell: `docker compose -f compose.base.yaml -f compose.postgres.yaml up -d --build`
  - Bash: `docker compose -f compose.base.yaml -f compose.postgres.yaml up -d --build` (use `-f compose.mongo.yaml` to switch)
- Add pgAdmin + mongo-express: already included in the selected override (`compose.postgres.yaml` or `compose.mongo.yaml`)
- Newman API tests (app must already be up): `docker compose -f compose.base.yaml -f compose.postgres.yaml --profile test-newman run --rm newman (or docker compose -f compose.base.yaml -f compose.mongo.yaml --profile test-newman run --rm newman)`
- Reset DB volumes: `docker compose down -v`
- Shortcut scripts: use `start-app.ps1` (Windows) and `start-app.sh` (Linux) for postgres/mongo and debug startup (debug exposes port 5005).

## Architecture: dual-persistence pattern

The core convention that spans multiple files:

- Each domain (`user`, `product`, `order`) exposes **one** `XxxService` interface consumed by a single `XxxController` (which implements the generated `XxxApi`).
- Two implementations coexist in the same context: `XxxPostgresService` and `XxxMongoService`. Bean selection is done at startup by the custom meta-annotation `@ConditionalOnDatasource(Datasources.POSTGRES | Datasources.MONGO)` (`common/annotation/`), which wraps `@ConditionalOnProperty(app.datasource=...)`. **When adding a new service/repository/mapper implementation, always provide both variants and annotate each** — otherwise the two sides fall out of sync (which breaks the SQL-vs-NoSQL comparison the thesis is built on) or the context fails to start.
- `DatasourceAutoConfigurationExcluder` (`config/datasource/`) validates `app.datasource` at bootstrap and applies backend-specific Spring autoconfiguration exclusions. Extend `Datasources` constants/list if adding a new backend.
- Persistence models are duplicated per backend: `XxxEntity` (JPA, `model/`) and `XxxDocument` (Mongo, `model/`). Repositories mirror this split: `XxxPostgresRepository` (JpaRepository) and `XxxMongoRepository` (MongoRepository). Mappers in `common/mapper/` convert both sides to/from the shared generated DTOs.
- REST DTOs and controller interfaces are **generated** by `openapi-generator-maven-plugin` from `src/main/resources/openapi/api.yaml` into packages `it.montano.multipersistencebackend.api` (interfaces) and `...dto` (models). Never hand-edit these; change `api.yaml` and rebuild. Note the plugin's `typeMappings` remaps OpenAPI `Double` → `java.math.BigDecimal`.
- Cross-cutting: AOP aspects in `config/logging/` add an MDC `requestId` to every REST call and log cache hit/miss/put/evict. Caffeine caches (`users`, `products`, `orders`, `orders-by-user`) are configured in `config/cache/CaffeineCacheConfig` with per-cache TTL/size.
- Global error format is produced by the handler in `config/exception/`.

## Testing conventions

- Integration tests use backend-specific bases: `AbstractPostgresIntegrationTest` and `AbstractMongoIntegrationTest` (`src/test/java/...`). Each one starts only the required Testcontainer and rewrites the relevant connection properties via `@DynamicPropertySource`. **Docker must be running** for these to pass.
- To pick the backend under test, set the property via `@SpringBootTest(properties = { AbstractPostgresIntegrationTest.PROPERTY_KEY_EQUALS + Datasources.POSTGRES })` or `AbstractMongoIntegrationTest.PROPERTY_KEY_EQUALS + Datasources.MONGO` (see `PostgresTest`, `MongoTest`). Add a new sibling test rather than parameterising.
- Test data uses **Instancio** (`instancio-junit`) — not Mockito fixtures — and assertions use **AssertJ**.
- Coverage exclusions (see `pom.xml` JaCoCo config): `Application*`, `api/**`, `**/dto/**`, `**/config/**`, `**/model/**`, `**/repository/**`, `**/*MapperImpl*`. Focus new tests on `service/`, `controller/`, `common/mapper/`, `common/util/`.

## Code style

- Google Java Format enforced by Spotless (`spotless:apply` runs during `package`). Do not hand-format.
- Lombok is used heavily (`@RequiredArgsConstructor`, `@Slf4j`, `@NonNull`); `lombok.config` sets `lombok.nonNull.exceptionType=JDK` so `@NonNull` throws `NullPointerException`.
- MapStruct is configured with `-Amapstruct.defaultComponentModel=spring` and `-Amapstruct.unmappedTargetPolicy=ERROR` — every target field must be mapped or explicitly ignored, or compilation fails.
- Package layout is domain-first (`user/`, `product/`, `order/`) with `controller/`, `service/`, `repository/`, `model/` subpackages. `common/` holds shared annotations, constants, DTOs helpers, mappers, and utils. `config/` holds all `@Configuration` classes grouped by concern.

## Configuration

- Runtime profile in Docker is `docker` (`SPRING_PROFILES_ACTIVE=docker` → `application-docker.yaml`); local dev uses `application.yaml` with `localhost` hosts.
- Only `app.datasource` switches persistence — do not add profile-based `@Profile` gates for that concern; use `@ConditionalOnDatasource`.
- PostgreSQL schema is versioned with Flyway migrations under `src/main/resources/db/migration`; keep JPA `ddl-auto` on `validate` so entity changes require explicit migration updates.
- MongoDB schema/index evolution is versioned with Mongock `@ChangeUnit`s under `src/main/java/it/montano/multipersistencebackend/config/mongock`;
- Local Docker MongoDB runs as standalone (not replica set), so Mongo URIs include `retryWrites=false` and Mongock is configured with `mongock.transaction-enabled=false`.
