FROM gradle:9-jdk-21-and-24 AS builder

WORKDIR /app
COPY . .

RUN gradle --no-daemon :frontend:copyWebApp
RUN gradle --no-daemon :backend:bootJar

FROM eclipse-temurin:21-alpine

RUN addgroup -g 1000 app
RUN adduser -G app -D -u 1000 -h /app app
USER app:app

WORKDIR /app
COPY --from=builder --chown=app:app /app/backend/build/libs/*.jar app.jar
# solve db persistance

ENV PORT=8080
EXPOSE 8080

CMD [ "java", "-jar", "app.jar" ]
