# Lifestarter

A wedding RSVP site: an Angular front-end backed by a Kotlin / Spring Boot service.

Guests fill in who is coming, what they eat and how they want to be contacted; the organisers get
aggregate counts on the public page and a spreadsheet export behind a login.

## Stack

| | |
| --- | --- |
| Backend | Kotlin 2.3 · Spring Boot 4.1 · JDK 25 · Maven |
| Persistence | SQLite (default) or MongoDB — one property decides |
| Frontend | Angular 10 · PrimeNG · nginx |
| Running it | Docker Compose — two containers |

## Architecture

The backend is a **hexagon** sliced by feature. Each slice owns its own vertical:

```text
server/src/main/kotlin/be/gerard/lifestarter/<feature>/
  api/         REST controllers (inbound adapter) + *To transfer objects
  mapper/      *To  <->  domain mapping
  domain/      business rules, domain types and the ports they need
  adapter/     outbound adapters (mail, Excel, events, …)
  repository/  persistence adapters + model/*Record row and document types
```

Features: `registration` (the core aggregate), `allergy`, `pledge`, `wave`, `country`, `access`,
`export`, `events`, `user`, plus a `config` package for the framework wiring.

Two rules hold the whole thing together, and `HexagonalArchitectureTest` fails the build when
either is broken:

1. **The domain depends on nothing.** No Spring, no Jackson, no POI, no JDBC — the `domain`
   packages are plain Kotlin. Domain services are registered as beans in `config/DomainConfiguration`
   rather than annotated with `@Service`, precisely so that stays true.
2. **Dependencies point inwards.** Controllers and adapters know the domain; the domain never knows
   them. Everything crossing the boundary does so through a port the domain declares
   (`RegistrationRepository`, `RegistrationExporter`, `CountryCatalog`, …).

Domain types are immutable `data class`es with `val` properties, validated in `init` blocks with
`require`. Cross-slice needs go through ports too — `pledge` counts subscriptions through a
`PledgeSubscriptionCounter` port rather than reaching into `registration`.

## Persistence: one knob, two backends

```yaml
lifestarter:
  persistence:
    type: sqlite        # or: mongodb
    sqlite:
      path: data/lifestarter.db
```

`application.yml` turns that value into a Spring profile
(`spring.profiles.include: "${lifestarter.persistence.type:sqlite}"`), and the profile decides
everything else:

- **`sqlite`** — a hand-declared Hikari `DataSource`, `JdbcClient` repositories with explicit SQL,
  and Flyway migrations from `db/migration`. The MongoDB auto-configurations are excluded.
- **`mongodb`** — `MongoTemplate` repositories over `*Record` documents. The JDBC and Flyway
  auto-configurations are excluded.

The domain never learns which one is running. Reference data (allergies, pledges, waves) is seeded
through the ports by `ReferenceDataInitializer`, so both modes come up with identical content.

Override it however you like:

```bash
./mvnw -pl server spring-boot:run -Dspring-boot.run.jvmArguments=-Dlifestarter.persistence.type=mongodb
LIFESTARTER_PERSISTENCE_TYPE=mongodb java -jar server/target/lifestarter-server-1.0.0-SNAPSHOT.jar
```

SQLite needs no setup at all: the database file and its parent directory are created on first
start. The pool is deliberately capped at one connection — SQLite serialises writes anyway, and a
single connection avoids `SQLITE_BUSY`. In containers the file is externalised onto a volume, see
[Where the data lives](#where-the-data-lives).

## Running it

Everything runs in containers — one for the Angular front-end, one for the API:

```bash
cp .env.example .env          # optional; every value has a default
docker compose up -d --build  # http://localhost:8080
```

That is the whole setup. See [Containers](#containers) for what runs where, and
[Where the data lives](#where-the-data-lives) for the storage.

For backend development the API can of course still run straight from Maven:

```bash
./mvnw -pl server spring-boot:run          # http://localhost:8080, SQLite in ./server/data
./mvnw verify                              # build + tests
```

For MongoDB, point it at your server and switch the mode:

```bash
./mvnw -pl server spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Dlifestarter.persistence.type=mongodb -Dspring.mongodb.uri=mongodb://localhost:27017/lifestarter"
```

The front-end runs separately during development (`cd client && npm start`, port 4200, already in
the CORS allow-list) and talks to `http://localhost:8080/api`. Note that the Angular 10 toolchain
needs **Node 14**; if your machine has something newer, build through the container instead
(`docker compose build web`), which pins the right Node version for you.

API documentation is at `/swagger-ui.html`.

## Containers

| | |
| --- | --- |
| `web` | nginx. Serves the Angular bundle and reverse-proxies `/api`, `/actuator` and the OpenAPI endpoints to `server`. The **only** published port. |
| `server` | The Spring Boot API. Not published: nginx is the only way in. |
| `mongo` | Optional, only with `--profile mongodb`. |

The two apps are deliberately separate: the server is a pure JSON API and no longer bundles or
serves the SPA. Because nginx puts both behind one origin the browser never needs CORS, and the
back-end never has to be exposed.

```bash
docker compose up -d --build      # build and start
docker compose logs -f server     # follow the API log
docker compose ps                 # health of both containers
docker compose down               # stop; the data volume is kept
docker compose build              # rebuild images after a code change
```

`LIFESTARTER_HTTP_PORT` in `.env` moves the site off port 8080. Everything else worth changing is
in [`.env.example`](.env.example).

Two details that are easy to get wrong and are handled in
[`client/nginx/default.conf.template`](client/nginx/default.conf.template): the SSE feed at
`/api/events/stream` has proxy buffering switched off (otherwise the browser sees nothing until
the connection closes), and unknown paths fall back to `index.html` so the Angular router keeps
working on a page refresh.

### Where the data lives

The SQLite database is stored **outside** the container, at `/var/lib/lifestarter/lifestarter.db`
inside it, which is a Docker volume. Rebuilding images, `docker compose down`, upgrading — none of
it touches the data.

By default that is the named volume `lifestarter_lifestarter-data`:

```bash
docker volume inspect lifestarter_lifestarter-data --format '{{.Mountpoint}}'
# /var/lib/docker/volumes/lifestarter_lifestarter-data/_data
```

(On macOS and Windows that path lives inside the Docker VM; reach the file through the container
rather than through Finder.)

Prefer a plain directory you can see? Set a path in `.env` and Compose turns it into a bind mount:

```bash
LIFESTARTER_DATA=./data     # or /srv/lifestarter/data
```

The container runs as uid **10001**, so on Linux the directory has to be writable by it:
`sudo mkdir -p /srv/lifestarter/data && sudo chown -R 10001:10001 /srv/lifestarter/data`.
Docker Desktop on macOS handles this for you.

Backup and restore, whichever mode you use:

```bash
docker compose cp server:/var/lib/lifestarter/lifestarter.db ./lifestarter-backup.db
docker compose cp ./lifestarter-backup.db server:/var/lib/lifestarter/lifestarter.db
docker compose restart server
```

`docker compose down -v` is the one command that *does* delete it.

### MongoDB instead of SQLite

```bash
echo LIFESTARTER_PERSISTENCE_TYPE=mongodb >> .env
docker compose --profile mongodb up -d
```

That starts a `mongo` container alongside the other two, with its own `lifestarter-mongo-data`
volume. Point `LIFESTARTER_MONGODB_URI` at an external server to use one instead.


## Configuration

Everything lives under the `lifestarter` prefix in `application.yml` and is bound to typed
`@ConfigurationProperties`. Nothing is hardcoded and no secret has a default.

| Property | Default | Environment variable | Purpose |
| --- | --- | --- | --- |
| `lifestarter.persistence.type` | `sqlite` | `LIFESTARTER_PERSISTENCE_TYPE` | `sqlite` or `mongodb` |
| `lifestarter.persistence.sqlite.path` | `data/lifestarter.db` | `LIFESTARTER_PERSISTENCE_SQLITE_PATH` | Database file; `/var/lib/lifestarter/lifestarter.db` in the container |
| `lifestarter.reference-data.enabled` | `true` | `LIFESTARTER_REFERENCEDATA_ENABLED` | Seed allergies, pledges and waves on start |
| `lifestarter.web.allowed-origins` | `http://localhost:4200` | `LIFESTARTER_WEB_ALLOWEDORIGINS` | CORS; irrelevant in the container setup, where nginx makes everything same-origin |
| `lifestarter.security.admin.username` | `admin` | `LIFESTARTER_SECURITY_ADMIN_USERNAME` | Account allowed to download the export |
| `lifestarter.security.admin.password` | *(none)* | `LIFESTARTER_SECURITY_ADMIN_PASSWORD` | Set it, or a random one is generated and logged |
| `spring.mongodb.uri` | `mongodb://localhost:27017` | `LIFESTARTER_MONGODB_URI` | MongoDB mode only |
| `lifestarter.mail.*` / `spring.mail.*` | — | `SPRING_MAIL_HOST`, … | Notification mail; skipped unless `spring.mail.host` is set |

In containers these are set through `.env`; see [`.env.example`](.env.example).

## API

Everything is served under `/api` (applied centrally). nginx proxies it through, so from the
browser's point of view the API and the site share one origin.

| Method | Path | |
| --- | --- | --- |
| `GET` | `/api/allergies` | Allergy catalogue |
| `GET` | `/api/diets` | Dietary preferences |
| `GET` | `/api/countries` | ISO countries, named per `Accept-Language` |
| `GET` | `/api/pledges` | Pledge tiers with their subscription counts |
| `GET` | `/api/waves` · `/api/waves/current` | RSVP rounds; `404` once every deadline has passed |
| `POST` | `/api/registrations` | File an RSVP → `201` + `Location` |
| `GET` | `/api/registrations/statistics` | Aggregate guest counters |
| `GET` | `/api/access/roles?first-name=&last-name=` | Extra ceremony roles for a named guest |
| `GET` | `/api/access/activities?pledge=&first-first-name=&…` | What a party may attend |
| `GET` | `/api/exports/registrations` | Streams an `.xlsx` — **ADMIN** |
| `GET` | `/api/events/ping` · `/api/events/stream` | Liveness probe and SSE feed |
| `GET` | `/api/users/me` | The calling user |

Only the export is protected: it is the one endpoint that returns personal data. The counters are
what the public RSVP page displays anyway. Validation failures and errors come back as RFC 9457
`application/problem+json`.

## Testing

```bash
./mvnw -pl server test
```

- `HexagonalArchitectureTest` — ArchUnit rules guarding the dependency direction.
- Domain unit tests with MockK against the ports.
- `RegistrationApiIntegrationTest` — the full stack over HTTP against a throwaway SQLite file.
- `SqliteRegistrationRepositoryTest` — aggregate round-trip through the normalised schema.
- `MongoPersistenceModeTest` — the same hexagon on MongoDB via Testcontainers. **Skipped**, not
  failed, when Docker is unavailable.

## Deployment

The two images are the deployment unit. On the host:

```bash
git clone … && cd lifestarter
cp .env.example .env         # set LIFESTARTER_SECURITY_ADMIN_PASSWORD, LIFESTARTER_HTTP_PORT, …
docker compose up -d --build
```

Upgrading is `git pull && docker compose up -d --build`; the data volume is untouched by it.

Both containers restart automatically (`restart: unless-stopped`) and report health, so an
orchestrator or `docker compose ps` tells you whether the stack is actually serving.

Put TLS in front of it: point a host nginx, Caddy or Traefik at `http://127.0.0.1:${LIFESTARTER_HTTP_PORT}`
and let it terminate HTTPS. [SETUP.md](SETUP.md) has the nginx and Let's Encrypt notes for that
host-level proxy. One thing to carry over into it: `/api/events/stream` is a server-sent event
feed and needs `proxy_buffering off;`, exactly as in
[`client/nginx/default.conf.template`](client/nginx/default.conf.template).
