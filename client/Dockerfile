# syntax=docker/dockerfile:1

# Angular 22 needs a Node 24 toolchain (its engines allow ^22.22.3, ^24.15.0 or >=26). Pinning it
# here is what makes the client build reproducible on any machine, whatever Node version happens
# to be installed on the host.
FROM node:24-alpine AS build
WORKDIR /workspace

COPY package.json package-lock.json* ./
RUN if [ -f package-lock.json ]; then \
      npm ci --no-audit --no-fund; \
    else \
      npm install --no-audit --no-fund; \
    fi

COPY . .
RUN npm run build


FROM nginx:1.29-alpine AS runtime

# Where nginx forwards /api, /actuator and the OpenAPI endpoints. Overridable at run time, so the
# same image works against a backend that is not called `server`.
ENV API_UPSTREAM=http://server:8080

COPY nginx/proxy.inc /etc/nginx/snippets/proxy.inc
COPY nginx/security-headers.inc /etc/nginx/snippets/security-headers.inc
# Files in /etc/nginx/templates are rendered with envsubst by the nginx entrypoint on start.
COPY nginx/default.conf.template /etc/nginx/templates/default.conf.template
COPY --from=build /workspace/dist/client /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=15s --timeout=3s --start-period=5s --retries=3 \
  CMD wget -qO /dev/null http://127.0.0.1/index.html || exit 1
