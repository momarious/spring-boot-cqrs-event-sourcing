# Build stage
FROM maven AS build

WORKDIR /app

# COPY pom.xml .
COPY . .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

# Run stage
FROM openjdk:21-jdk-slim

# Install curl (for actuator)
RUN apt-get update && apt-get install -y curl

COPY --from=build /app/target/command-service-0.0.1-SNAPSHOT.jar command-service.jar

EXPOSE 8081
# EXPOSE 8096

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dblabla", "-jar","/command-service.jar"]