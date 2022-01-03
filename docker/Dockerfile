# Build stage
FROM maven:alpine AS build

WORKDIR /app

COPY pom.xml .
COPY src/main/kotlin/META-INF/MANIFEST.MF ./src/main/kotlin/META-INF/MANIFEST.MF
RUN mvn verify --fail-never

COPY src ./src
RUN mvn clean package

# Package stage
FROM openjdk:8-jre-alpine
COPY --from=build /app/target/codewarsbackend-*.jar /app/codewarsbackend.jar
COPY docker/entrypoint.sh /app/

ENV PORT=80

ENTRYPOINT ["/app/entrypoint.sh"]
