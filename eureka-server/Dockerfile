# Use OpenJDK as the base image
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the JAR file from the target folder to the container
COPY target/eureka-server-0.0.1-SNAPSHOT.jar app.jar

# Expose the application's port
EXPOSE 8761

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
