# Running Lifestarter

Every step below is a copy-pasteable block. Run them from the repository root.

Two containers make up the site:

| Service  | What it is                                                          | Published        |
| -------- | ------------------------------------------------------------------- | ---------------- |
| `web`    | nginx serving the Angular bundle, reverse-proxying `/api` to `server` | yes, port `8080` |
| `server` | the Spring Boot API                                                   | no               |
| `mongo`  | only with `--profile mongodb`                                         | no               |

The backend no longer serves the frontend. nginx is the only way in, which is why the browser
never needs CORS: it talks to a single origin.

## 1. Prerequisites

Docker is the only thing you need — the Angular and Maven builds happen inside the images, so no
local Node or JDK is required.

```sh
docker --version          # 24 or newer
docker compose version    # v2
```

## 2. Configure

`.env` is git-ignored. The defaults in `.env.example` run the whole site with no editing.

```sh
cp .env.example .env
```

Worth knowing before you start:

- `LIFESTARTER_HTTP_PORT` (default `8080`) — change it if something already owns that port.
- `LIFESTARTER_PERSISTENCE_TYPE` (default `sqlite`) — `sqlite` or `mongodb`.
- `LIFESTARTER_SECURITY_ADMIN_PASSWORD` — leave it empty and the backend generates a random
  password at start-up and logs it. The only protected endpoint is the `.xlsx` export.
- `SPRING_MAIL_HOST` — while it is empty, confirmation e-mail is skipped entirely.

## 3. Start

```sh
docker compose up -d --build
```

The first build takes a few minutes (Maven and npm dependencies). Later builds are cached.

`web` waits for `server` to report healthy, so once the command returns the site is ready.

## 4. Open it

```sh
open "http://localhost:${LIFESTARTER_HTTP_PORT:-8080}"   # xdg-open on Linux, start on Windows
```

| URL                                            | What                              |
| ---------------------------------------------- | --------------------------------- |
| `http://localhost:8080/`                       | the campaign page                 |
| `http://localhost:8080/registration?pledge=…`  | the RSVP wizard for a reward      |
| `http://localhost:8080/api/pledges`            | the API through the nginx proxy   |
| `http://localhost:8080/actuator/health`    | backend health                    |

## 5. Check it is healthy

```sh
docker compose ps
curl -fsS "http://localhost:${LIFESTARTER_HTTP_PORT:-8080}/actuator/health"
curl -fsS "http://localhost:${LIFESTARTER_HTTP_PORT:-8080}/api/pledges" | head -c 400
```

Both containers should read `healthy`, and the health endpoint should answer `{"status":"UP"}`.

## 6. Logs

```sh
docker compose logs -f            # everything
docker compose logs -f server     # the API, including the generated admin password
docker compose logs -f web        # nginx access and error log
```

## 7. Stop

```sh
docker compose stop     # keep the containers
docker compose down     # remove them; the database survives, it is on a volume
```

## Where the data lives

By default the SQLite database is a file on a named Docker volume, so it survives
`docker compose down`, rebuilds and redeploys. Only `docker compose down -v` destroys it.

| | |
| --- | --- |
| Volume | `lifestarter_lifestarter-data` |
| Path inside the container | `/var/lib/lifestarter/lifestarter.db` |
| Host path | ask Docker — see below |

```sh
docker volume inspect lifestarter_lifestarter-data --format '{{ .Mountpoint }}'
```

On Linux that is a real host directory. On macOS and Windows it is a path *inside* Docker's own
VM, so you cannot open it from Finder or Explorer. Copy it out instead:

```sh
docker compose cp server:/var/lib/lifestarter/lifestarter.db ./lifestarter.db
```

### Keeping the database somewhere you choose

Set `LIFESTARTER_DATA` to a path and Compose turns the mount into a bind mount, putting the file
where you can see it — useful for backups.

```sh
mkdir -p ./data
sudo chown -R 10001:10001 ./data      # the server runs as uid 10001 and must be able to write
printf 'LIFESTARTER_DATA=./data\n' >> .env
docker compose up -d
```

The database is then `./data/lifestarter.db`.

### Back up and restore

The image has no `sqlite3` binary, so copy the file out. Stop the server first and the copy is
guaranteed consistent; copying it live is usually fine but can catch a write in progress.

```sh
# Back up
docker compose stop server
docker compose cp server:/var/lib/lifestarter/lifestarter.db "./backup-$(date +%F).db"
docker compose start server

# Restore
docker compose stop server
docker compose cp ./backup-2026-01-01.db server:/var/lib/lifestarter/lifestarter.db
docker compose start server
```

### Starting over

```sh
docker compose down -v    # deletes the volumes, and with them every registration
```

## Running on MongoDB instead

```sh
printf 'LIFESTARTER_PERSISTENCE_TYPE=mongodb\n' >> .env
docker compose --profile mongodb up -d --build
```

Mongo keeps its data on `lifestarter_lifestarter-mongo-data`. Point `LIFESTARTER_MONGODB_URI` at
an external server if you would rather not run the container. Remember `--profile mongodb` on
every later `docker compose` command, or Compose will not know about the `mongo` service.

## Development

### Frontend, with live reload

`ng serve` proxies `/api` to `http://localhost:8080`, so leave the containers running and let the
dev server replace only the frontend.

```sh
docker compose up -d --build server web
cd client
docker run --rm -it -v "$PWD":/app -w /app -p 4200:4200 node:24-alpine \
  npx ng serve --host 0.0.0.0
```

The dev server is on <http://localhost:4200>; the containerised site stays on `:8080`.

> The build runs inside `node:24-alpine` on purpose. Angular 22 refuses to run on odd-numbered
> Node releases, so a host Node 25 will fail before it starts.

### Frontend checks

```sh
cd client
docker run --rm -v "$PWD":/app -w /app node:24-alpine sh -c \
  "npx ng build && npx ng lint && npx ng test"
```

### Backend

```sh
./mvnw -pl server test                       # tests
./mvnw -pl server spring-boot:run            # run on :8080, SQLite in ./server/lifestarter.db
```

To run the backend against the containerised database instead, stop the `server` container first
so the two do not write the same file.

### Rebuilding one service

```sh
docker compose up -d --build server
docker compose up -d --build web
```

## Troubleshooting

**The port is taken.**

```sh
printf 'LIFESTARTER_HTTP_PORT=9090\n' >> .env
docker compose up -d
```

**`web` never starts.** It waits for `server` to be healthy, so the problem is upstream:

```sh
docker compose logs server | tail -50
```

**The site loads but every list is empty.** The proxy is not reaching the backend:

```sh
curl -i "http://localhost:${LIFESTARTER_HTTP_PORT:-8080}/api/pledges"
docker compose exec web wget -qO- http://server:8080/actuator/health
```

**Permission denied on the database** after setting `LIFESTARTER_DATA`. The directory must be
writable by uid 10001:

```sh
sudo chown -R 10001:10001 ./data
docker compose restart server
```

**A stale frontend.** The bundle is baked into the image, so it needs a rebuild, and the browser
needs a hard reload:

```sh
docker compose up -d --build web
```
