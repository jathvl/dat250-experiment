FROM node:22 AS build_frontend

RUN corepack enable

RUN mkdir /app
WORKDIR /app

COPY poll-frontend/package.json .
COPY poll-frontend/pnpm*.yaml .

RUN pnpm install --frozen-lockfile

COPY poll-frontend/ .

RUN pnpm run build

FROM gradle:9-jdk21 AS build_backend

RUN mkdir /app
WORKDIR /app

COPY . .
COPY --from=build_frontend /app/dist /app/src/main/resources/static

RUN ./gradlew bootJar

FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app
COPY --from=build_backend /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
