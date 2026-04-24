# ---------- Etapa de build ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el pom primero para aprovechar la caché de dependencias
COPY pom.xml .

# Descargamos dependencias (sin compilar aún)
RUN mvn -B dependency:go-offline

# Copiamos el código fuente
COPY src ./src

# Construimos el .jar (sin tests para ir más rápido)
RUN mvn -B clean package -DskipTests

# ---------- Etapa de runtime ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiamos el jar generado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Puerto interno que usa Spring Boot
EXPOSE 8080

# Render suele inyectar la variable PORT, le damos un default para local
ENV PORT=8080
ENV JAVA_OPTS=""

# Usamos PORT para que funcione tanto en local como en Render
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar"]