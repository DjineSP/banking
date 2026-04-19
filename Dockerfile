# ── Stage 1 : Build ──────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2 : Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

# Jar dans /opt/banking pour ne pas interférer avec le disque persistant
RUN mkdir -p /opt/banking
COPY --from=builder /build/target/*.jar /opt/banking/app.jar

# Répertoire de travail = racine du projet = emplacement de banking.db
# Sur Render : monter le Persistent Disk sur /app
WORKDIR /app

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar /opt/banking/app.jar"]
