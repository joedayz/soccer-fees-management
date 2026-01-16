FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn -pl micronaut-app -am package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/micronaut-app/target/micronaut-app-0.1.0-SNAPSHOT.jar /app/app.jar
ENV PORT=8082
CMD ["java", "-jar", "/app/app.jar"]
