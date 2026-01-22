# Stage 1: Build
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy gradle wrapper & gradlew
COPY gradlew .
COPY gradle gradle
RUN chmod +x gradlew

# Copy backend code
COPY supermarket-backend/ .  

# Build
RUN ./gradlew build --no-daemon

# Stage 2: Run
FROM eclipse-temurin:17-jdk-focal
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
