FROM eclipse-temurin:21-jdk-alpine AS base
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=base /app/target/*.jar memorix.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "memorix.jar"]