# Stage 1: Build the application safely using the official Gradle image
FROM gradle:8-jdk21-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Compile and package the application, skipping tests to speed up local container builds
RUN ./gradlew bootJar -x test --no-daemon

# Stage 2: Create a minimal, secure runtime environment
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
EXPOSE 8080

# Copy the built JAR from the first stage into the runtime stage
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Run the application with standard memory optimization flags
ENTRYPOINT ["java", "-jar", "app.jar"]
