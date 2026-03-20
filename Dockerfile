#FROM eclipse-temurin:17-jre-jammy
#
#WORKDIR /app
#
#COPY target/user-service-0.0.1-SNAPSHOT.jar /app/user-service-0.0.1-SNAPSHOT.jar
#
#EXPOSE 8081
#
#ENTRYPOINT ["java", "-jar", "user-service-0.0.1-SNAPSHOT.jar"]


# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render uses port 10000 by default for free tier
EXPOSE 10000
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=10000"]