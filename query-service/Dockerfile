# Build stage
FROM maven AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

# Run stage
FROM openjdk:21-jdk-slim

# Install curl (for actuator)
RUN apt-get update && apt-get install -y curl

COPY --from=build /app/target/query-service-0.0.1-SNAPSHOT.jar query-service.jar

EXPOSE 8082

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dblabla", "-jar","/query-service.jar"]