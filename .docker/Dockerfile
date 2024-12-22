FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . /app

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-noble

WORKDIR /app

COPY --from=builder /app/bot-server/bot-runners/target/twitch-bot-server.jar /app/twitch-bot-server.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/twitch-bot-server.jar"]
