# Use a single stage with Gradle and Java
FROM gradle:8.5-jdk17
WORKDIR /app

# Set environment variables for Gradle
ENV GRADLE_OPTS="-Xmx2048m -XX:MaxMetaspaceSize=512m"
ENV GRADLE_USER_HOME=/app/.gradle

# Copy the entire backend project
COPY . .

# Make gradlew executable
RUN chmod +x gradlew

# Clean and build the backend with increased memory
RUN ./gradlew clean shadowJar --no-daemon --stacktrace

# Expose default port
EXPOSE 8080

# Start backend with proper memory settings
CMD ["java", "-Xmx1024m", "-jar", "build/libs/supermarket-backend-1.0-SNAPSHOT-all.jar"]
