# Lifestarter

A wedding RSVP site: an Angular front-end backed by a Kotlin / Spring Boot service.

Guests fill in who is coming, what they eat and how they want to be contacted; the organisers get
aggregate counts on the public page and a spreadsheet export behind a login.

## Stack

| | |
| --- | --- |
| Backend | Kotlin 2.3 · Spring Boot 4.1 · JDK 25 · Maven |
| Persistence | SQLite (default) or MongoDB — one property decides |
| Frontend | Angular 22 · standalone · zoneless · signal forms · nginx |
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

## Front-end

The client is a standalone, **zoneless** Angular 22 application. No NgModules, no zone.js, no UI
framework: every widget is a native HTML control styled by hand, which is both smaller and more
accessible than the PrimeNG/Bootstrap stack it replaces.

```text
client/
  public/
    assets/…              images, fonts and icons, served verbatim
    i18n/{nl,en,fr}.json  every label and every piece of page prose
  src/app/
    core/       API base-URL token, resource helpers, i18n (LanguageService)
    ui/         presentational primitives (float label, phone mask)
    layout/     site header/footer, campaign nav, language switcher, skip link
    campaign/   the landing page and the pledge tiers
    registration/  the four-step RSVP wizard
    reference/  read-only catalogues (allergies, diets, countries, pledges, waves)
    updates/ special/  the two content pages
```

- **State is signals.** Read-only endpoints are `httpResource()`s exposed as signals; there is no
  `ngOnInit` + `subscribe` anywhere. Note that a resource's `value()` *rethrows* the loader's
  error, so everything goes through the `valueOr()` helper in `core/api.ts` — one unreachable
  endpoint must not take a page down with it. Those resources deliberately declare **no**
  `defaultValue`: it would make `value()` always defined and `hasValue()` always true, which
  quietly disables the fallback and hides the difference between a real answer and one still in
  flight. That mistake cost real data — see bug #8.
- **The RSVP wizard uses signal forms** (`@angular/forms/signals`, stable since v22): one
  immutable draft model, one schema mirroring the backend's Bean Validation constraints, and
  `applyEach`/`applyWhen` for the dynamic guest rows and the conditional contact fields.
- **The live guest counter** is fed by the `/api/events/stream` SSE endpoint.
- **Three languages, switched without a reload.** `$localize` is still compile-time in v22 (one
  bundle per locale), so runtime switching goes through `@ngx-translate/core`. The language is
  detected from the browser, overridable from the header, and remembered only once it has been
  chosen explicitly. `<html lang>` follows it.
- **Accessibility**: skip link, landmarks, a `<label>` for every control, `aria-describedby`
  error text, `aria-invalid`, `<fieldset>`/`<legend>` groups, `aria-current="step"` in the wizard,
  focus moved to the step heading on advance, and `prefers-reduced-motion` honoured. The
  Kickstarter green is kept for decoration only; text uses a tone that clears WCAG AA.
- **The phone mask** (`ui/phone-mask.ts`) replaces PrimeNG's `p-inputMask`, reproducing the
  original `+32 999 999 9?99`: a fixed prefix, digits grouped 3-3-3 with the last two optional,
  the caret held in place while typing and pasting, and a partial number kept rather than wiped.
  The field stays empty until focused so the float label is not pinned above an untouched
  optional field. An incomplete number is reported through the usual field error.
- **Float labels** (`ui/float-label.ts`) replace PrimeNG's `ui-float-label`. The label rests
  inside its control and rises above it on focus or once the field is filled. No JavaScript
  tracks that state — `:placeholder-shown` and `:focus-within` do, so it cannot drift out of sync
  the way a script-toggled class can, and browser autofill floats the label for free. The label
  stays a real, always-visible `<label for>`; it is not a placeholder standing in for one.

The photos under `assets/images/we/` are deliberately not in the repository. They degrade to a
neutral placeholder block rather than breaking the layout.

[BUGS.md](BUGS.md) records the bugs the rewrite's browser pass turned up, why the build did not
catch them, and how each is guarded against now.

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

That is the whole setup. [RUN.md](RUN.md) has every step as a copy-pasteable block, including
health checks, logs, backups and troubleshooting. See [Containers](#containers) for what runs
where, and [Where the data lives](#where-the-data-lives) for the storage.

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
the CORS allow-list) and proxies `/api` to `http://localhost:8080` via `proxy.conf.json`. The
Angular 22 toolchain needs **Node 22.22+, 24.15+ or 26+**; if your machine has something else,
build through the container instead (`docker compose build web`), which pins the right version:

```bash
docker run --rm -v "$PWD/client":/app -w /app node:24-alpine npx ng build
```

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
./mvnw -pl server test                                                    # backend
docker run --rm -v "$PWD/client":/app -w /app node:24-alpine npx ng test  # front-end (vitest)
docker run --rm -v "$PWD/client":/app -w /app node:24-alpine npx ng lint
```

Front-end tests cover the pure logic that is easy to get subtly wrong: locale negotiation, the
lookup-URL builders, the draft → request mapping, and a check that the three dictionaries have
identical keys and identical interpolation parameters.

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
