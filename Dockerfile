# Multi-stage Docker build for Event Inquiry Management API
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src src

# Package application using container's pre-installed Maven
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy compiled jar artifact from build stage
COPY --from=build /app/target/event-inquiry-api-1.0.0.jar app.jar

ENV PORT=8080

EXPOSE 8080

USER appuser:appgroup

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
