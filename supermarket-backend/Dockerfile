# Stage 1: Build
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy the entire backend project
COPY . .

# Make gradlew executable
RUN chmod +x gradlew

# Build the backend
RUN ./gradlew build --no-daemon

# Stage 2: Run
FROM eclipse-temurin:17-jdk-focal
WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose default port
EXPOSE 8080

# Start backend
CMD ["java", "-jar", "app.jar"]
