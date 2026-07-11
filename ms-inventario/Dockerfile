# --- Etapa 1: compilar el proyecto con Maven ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos primero el pom.xml para aprovechar la cache de Docker:
# si no cambian las dependencias, no se vuelven a descargar en cada build
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Ahora copiamos el codigo fuente y compilamos el .jar
COPY src ./src
RUN mvn clean package -DskipTests

# --- Etapa 2: imagen final, liviana, solo con el .jar ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
