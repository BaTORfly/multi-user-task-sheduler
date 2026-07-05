# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml .

RUN mvn dependency:go-offline -DskipTests

COPY src src

RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /workspace/target/config-server-0.0.1-SNAPSHOT.jar app.jar

USER spring

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
