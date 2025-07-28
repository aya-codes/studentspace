FROM ubuntu:latest
LABEL authors="user"

ENTRYPOINT ["top", "-b"]
# Use an official Java 17 image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy the Maven wrapper and project files
COPY . .

# Make mvnw executable
RUN chmod +x mvnw

# Build the project (skipping tests for faster deploys)
RUN ./mvnw clean package -DskipTests

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the built JAR
CMD ["java", "-jar", "target/*.jar"]
