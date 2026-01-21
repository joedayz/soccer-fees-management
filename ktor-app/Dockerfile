FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -pl ktor-app -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/ktor-app/target/ktor-app-0.1.0-SNAPSHOT.jar /app/app.jar
ENV PORT=8080
CMD ["java", "-jar", "/app/app.jar"]
