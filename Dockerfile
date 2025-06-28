FROM maven:3.9.9-eclipse-temurin-21-alpine AS base
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM gcr.io/distroless/java21-debian12:latest
WORKDIR /app
COPY --from=base /app/target/*.jar memorix.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "memorix.jar"]