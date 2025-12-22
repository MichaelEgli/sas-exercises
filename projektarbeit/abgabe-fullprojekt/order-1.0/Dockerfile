# Use Eclipse Temurin JRE for runtime
FROM eclipse-temurin:25-jre-alpine

# Create app directory
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the built JAR file
COPY target/order-*.jar app.jar

# Change ownership to non-root user
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring:spring

# Expose port 8080
EXPOSE 8080

# Set default environment variable for server port
ENV SERVER_PORT=8080

# Healthcheck - verify the application is responding
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD sh -c 'wget --no-verbose --tries=1 --spider http://localhost:$(echo ${SERVER_PORT:-8080})/actuator/health || exit 1'

# Set JVM options for container environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

