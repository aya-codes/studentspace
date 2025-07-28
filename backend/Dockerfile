FROM ubuntu:latest
LABEL authors="user"

ENTRYPOINT ["top", "-b"]
# Use an official Java runtime as a base image
FROM openjdk:17-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy the Maven wrapper and pom.xml first (for caching dependencies)
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./

# Make Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies (cached if pom.xml hasn't changed)
RUN ./mvnw dependency:go-offline

# Copy the rest of the application code
COPY ../src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose the port your Spring Boot app runs on (usually 8080)
EXPOSE 8080

# Run the app
CMD ["java", "-jar", "target/*.jar"]
