# Build stage
FROM gradle:8.5-jdk21 AS builder
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew && ./gradlew :employee-service:build -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/employee-service/build/libs/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
