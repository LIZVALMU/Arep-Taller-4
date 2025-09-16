# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Compila y genera el JAR sombreado, luego limpia caché de Maven
RUN mvn -q clean package shade:shade -DskipTests && mvn dependency:purge-local-repository -DactTransitively=false -DreResolve=false

# ---- Run Stage ----
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Puerto configurable
ARG PORT=35000
ENV PORT=${PORT}
EXPOSE ${PORT}

# Copia el JAR compilado desde la etapa de construcción
COPY --from=build /app/target/HttpServer-1.0-SNAPSHOT.jar app.jar

# Da permisos al usuario no root
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/* \
	&& adduser --disabled-password --gecos '' appuser && chown -R appuser:appuser /app
USER appuser

# Ejecuta la aplicación
CMD ["java", "-jar", "app.jar"]