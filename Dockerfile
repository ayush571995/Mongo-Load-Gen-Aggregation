# Use the official Gradle image with JDK 8 to create a build artifact
FROM gradle:7.2-jdk8 as builder
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN gradle build -x test

# Use OpenJDK 8 for running the application
FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
