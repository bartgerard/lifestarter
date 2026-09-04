# syntax=docker/dockerfile:1

# The build context is the repository root: the aggregator pom and the Maven wrapper live there,
# next to server/.
FROM maven:3.9-eclipse-temurin-25-alpine AS build
WORKDIR /workspace

# Resolve dependencies in their own layer so a source-only change does not re-download the world.
COPY pom.xml ./
COPY server/pom.xml server/pom.xml
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -pl server -am dependency:go-offline

COPY server/src server/src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -pl server -am package -DskipTests \
 && mv server/target/lifestarter-server-*.jar /workspace/application.jar


FROM eclipse-temurin:25-jre-alpine AS runtime

# A fixed uid/gid so a host bind mount can be chowned to it (see README).
ARG APP_UID=10001
ARG APP_GID=10001

ENV LIFESTARTER_PERSISTENCE_SQLITE_PATH=/var/lib/lifestarter/lifestarter.db \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"

# /var/lib/lifestarter is the mount point for the volume that outlives the container.
RUN addgroup -S -g ${APP_GID} lifestarter \
 && adduser -S -u ${APP_UID} -G lifestarter -H -s /sbin/nologin lifestarter \
 && mkdir -p /var/lib/lifestarter \
 && chown ${APP_UID}:${APP_GID} /var/lib/lifestarter

WORKDIR /opt/lifestarter
COPY --from=build --chown=${APP_UID}:${APP_GID} /workspace/application.jar application.jar

USER lifestarter
EXPOSE 8080
VOLUME ["/var/lib/lifestarter"]

HEALTHCHECK --interval=15s --timeout=5s --start-period=60s --retries=10 \
  CMD wget -qO- http://127.0.0.1:8080/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["java", "-jar", "application.jar"]
