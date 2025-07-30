# Use OpenJDK 11 as base image (compatible with Java 9 code)
FROM openjdk:11-jre-slim

# Set working directory
WORKDIR /app

# Create application user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy the built JAR file
COPY target/taskmanagment-0.0.1-SNAPSHOT.jar app.jar

# Create log directory
RUN mkdir -p /var/log/taskmanagement && \
    chown -R appuser:appuser /var/log/taskmanagement && \
    chown appuser:appuser app.jar

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

# Set JVM options for production
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Run the application with production profile
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=prod -jar app.jar"]
