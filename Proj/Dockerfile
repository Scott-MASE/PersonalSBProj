# Dockerfile

# Use the multi-arch Temurin JDK (no Alpine)
FROM eclipse-temurin:17-jdk

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]