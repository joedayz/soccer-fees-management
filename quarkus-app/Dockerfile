FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -pl quarkus-app -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app/quarkus-app
COPY --from=build /app/quarkus-app/target/quarkus-app/ /app/quarkus-app/
ENV PORT=8083
CMD ["java", "-jar", "/app/quarkus-app/quarkus-run.jar"]
