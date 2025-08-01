# Runtime stage - assumes JAR is already built
FROM eclipse-temurin:21-jre-alpine

# Create non-root user for security
RUN addgroup -g 1001 -S kotlinbe && \
    adduser -u 1001 -S kotlinbe -G kotlinbe

# Set working directory
WORKDIR /app

# Copy the pre-built jar file
COPY build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown -R kotlinbe:kotlinbe /app

# Switch to non-root user
USER kotlinbe

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 