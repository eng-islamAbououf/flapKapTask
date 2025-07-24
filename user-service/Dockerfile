# ====== Stage 1: Build JAR ======
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Set work directory
WORKDIR /build

# Copy Maven project files
COPY pom.xml .
COPY src ./src

# Build the JAR
RUN mvn clean package -DskipTests

# ====== Stage 2: Run the app ======
FROM eclipse-temurin:21-jdk-alpine

# Set work directory
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /build/target/*.jar app.jar

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
