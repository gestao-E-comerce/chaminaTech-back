# Etapa 1: Construção do JAR com Maven
FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

# Copie o pom.xml e baixe as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copie o restante do código
COPY src /app/src

# Construa o JAR
RUN mvn clean package -DskipTests

# Etapa 2: Imagem final com o JAR
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copie o JAR correto
COPY --from=build /app/target/ChaminaTech-back-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
