# Build stage
FROM gradle:8.5-jdk21 AS builder
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew && ./gradlew :recruitment-service:build -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/recruitment-service/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
