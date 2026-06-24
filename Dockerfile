#Build (stage): compila o projeto usando Maven + JDK
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

#Copia o pom para aproveitar cache de dependência
COPY pom.xml .

#Baixa dependências para o cache do maven
RUN mvn dependency:go-offline -B

#Copia o código-fonte para compilar
COPY src ./src

#compila e gera o JAR em target/
RUN mvn clean package -DskipTests -B


# Etapa 2: executar a aplicação
# Runtime (stage): imagem menor com apenas JRE
FROM eclipse-temurin:21-jre

#Diretorio de trabalho na imagem final
WORKDIR /app

#Copia o JAR gerado no builder e renomeia para app.jar (nome fixo para execução)
#Copia qualquer JAR gerado em target/ e renomeia para `app.jar` para execução
#Atenção: garanta que o packaging do projeto seja "jar" (não "war") ou configure
#"finalName" no "pom.xml" para padronizar o nome do artefato.
COPY --from=build /app/target/*.jar app.jar

#Indica a porta que a aplicação usa
EXPOSE 8080

#RUN addgroup --system spring && adduser --system --ingroup spring spring

#USER spring:spring

# Checa periodicamente se a aplicação está respondendo
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/ || exit 1

# Comando padrão ao iniciar o container
ENTRYPOINT ["java", "-jar", "app.jar"]