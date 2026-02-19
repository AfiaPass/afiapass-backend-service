# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy the parent pom and all modules
COPY pom.xml .
COPY afiapass-sdk/ afiapass-sdk/
COPY afiapass-api/ afiapass-api/

# Build the entire project
RUN ./mvnw clean install -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the executable jar from the api module
COPY --from=builder /app/afiapass-api/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]