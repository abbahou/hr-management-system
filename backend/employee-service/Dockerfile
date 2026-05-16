# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY employee-service/build/libs/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
