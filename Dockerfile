# ===========================
# STAGE 1 — BUILD
# ===========================
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy only the files needed for dependency resolution first (better layer caching)
COPY mvnw .
RUN chmod +x mvnw # Make mvn wrapper executable
COPY .mvn .mvn
COPY pom.xml .

# Pre-download maven dependencies in advance (caching optimization)
RUN ./mvnw -q dependency:go-offline -B

# Copy the source code (Avoiding cache break in each code updates)
COPY src src

# Build the application (jar file - skip tests)
RUN ./mvnw clean package -DskipTests

# ===========================
# STAGE 2 — RUNTIME
# ===========================
FROM eclipse-temurin:17-jre-alpine

LABEL version="0.1.0"
LABEL description="Ecomera Backend - E-commerce Platform"
LABEL maintainer="youssef.ammari.795@gmail.com"

# Create user to run app as non-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Create directory and set ownership
WORKDIR /app
RUN mkdir -p /app && chown appuser:appgroup /app

# Copy the jar from the builder stage (not from host's target directory)
COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar

# Switch to non-root user
USER appuser

# Expose the port the app runs on
EXPOSE 8080

# Health check (optional but recommended)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Use shell form to allow environment variable expansion
ENTRYPOINT ["java", "-jar", "app.jar"]