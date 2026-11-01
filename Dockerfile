FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace
COPY . .

RUN chmod +x mvnw && ./mvnw -B clean package -Pproduction -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /workspace/target/udyogasethu-admin-*.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV PORT=8080

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java -jar /app/app.jar --server.port=${PORT}"]
