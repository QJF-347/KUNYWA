COPY gradlew .
COPY gradle gradle
RUN chmod +x gradlew

COPY supermarket-backend/ .
RUN ./gradlew build --no-daemon
