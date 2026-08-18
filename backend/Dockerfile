# syntax=docker/dockerfile:1.7

FROM amazoncorretto:25.0.4-alpine3.24 AS build

WORKDIR /workspace

COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src

RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon bootJar

FROM amazoncorretto:25.0.4-alpine3.24

RUN addgroup -S cleany && adduser -S -G cleany cleany

WORKDIR /app

COPY --from=build --chown=cleany:cleany /workspace/build/libs/cleany-backend-*.jar /app/app.jar

USER cleany

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
