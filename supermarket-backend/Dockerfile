# Stage 1: Build
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy gradlew & gradle wrapper first
COPY gradlew .
COPY gradle gradle

# Make gradlew executable
RUN chmod +x gradlew

# Copy rest of project
COPY . .

# Build
RUN ./gradlew build --no-daemon


# Stage 2: Run
FROM eclipse-temurin:17-jdk-focal
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]


