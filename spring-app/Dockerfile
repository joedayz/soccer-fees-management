FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -pl spring-app -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/spring-app/target/spring-app-0.1.0-SNAPSHOT.jar /app/app.jar
ENV PORT=8081
CMD ["java", "-jar", "/app/app.jar"]
