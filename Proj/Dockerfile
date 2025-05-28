# Use a small JDK base image
FROM eclipse-temurin:17-jdk-alpine

# The JAR produced by Maven
ARG JAR_FILE=target/*.jar

# Copy it into the image
COPY ${JAR_FILE} app.jar

# Launch on container startup
ENTRYPOINT ["java","-jar","/app.jar"]