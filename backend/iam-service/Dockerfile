# Build stage
FROM gradle:8.5-jdk21 AS builder
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew && ./gradlew :iam-service:build -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/iam-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
