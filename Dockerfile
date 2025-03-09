FROM openjdk:17-jdk-alpine

# Create non-root user with specific UID/GID
RUN addgroup --system -g 1001 appuser && \
    adduser --system -D -G appuser -u 1001 appuser

# Create required directories with appropriate permissions
RUN mkdir -p /pdf-utils/upload-dir && \
    chown -R appuser:appuser /pdf-utils && \
    chmod -R 750 /pdf-utils

# Remove SUID/SGID binaries
RUN find / -perm +6000 -type f -exec rm -rf {} \; 2>/dev/null || true

# Set working directory
WORKDIR /pdf-utils

# Copy JAR file
ARG JAR_FILE=target/*.jar
COPY --chown=appuser:appuser ${JAR_FILE} app.jar

# Security enhancements
RUN apk upgrade --no-cache && \
    apk add --no-cache tzdata && \
    rm -rf /var/cache/apk/*

# Switch to non-root user
USER appuser

# Configure runtime with security flags
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "--add-opens=java.base/java.lang=ALL-UNNAMED", \
    "--add-opens=java.base/java.util=ALL-UNNAMED", \
    "-XX:+UseContainerSupport", \
    "-XX:+UseG1GC", \
    "-Djava.awt.headless=true", \
    "-jar", "app.jar"]
