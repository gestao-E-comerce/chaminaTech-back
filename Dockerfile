# Etapa 1: Build com Maven
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copia pom.xml e baixa dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia todo o código-fonte
COPY src ./src

# Compila e empacota sem testes
RUN mvn clean package -DskipTests -B

# Etapa 2: Runtime Java
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Copia o JAR gerado (wildcard para pegar qualquer versão)
COPY --from=build /app/target/*.jar app.jar

# Copia o keystore.p12 para o container
COPY keystore.p12 /app/keystore.p12
# Expõe porta usada pela aplicação
EXPOSE 8443

# Comando padrão para iniciar a aplicação
ENTRYPOINT ["java","-jar","app.jar"]