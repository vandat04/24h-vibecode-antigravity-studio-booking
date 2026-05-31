# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the pom.xml and download dependencies to leverage Docker caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application jar file (skipping tests for faster deployment)
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot's default port
EXPOSE 8080

# Run with custom JVM parameters optimized for Render's Free Tier (512MB RAM limit)
# MaxRAMPercentage=75.0 ensures the JVM doesn't exceed 75% of the container memory (~384MB)
# Xss256k reduces thread stack size to save memory
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-Xss256k", "-jar", "app.jar"]
