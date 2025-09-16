##########
# Dockerfile multi-stage para construir y ejecutar la app
# - Build con Maven + Temurin 17 (alineado con pom.xml)
# - Runtime con JRE 17 minimal
# - Copia de recursos estáticos a /static para que el handler los sirva
##########

# ---------- Build stage ----------
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app

# Pre-descarga dependencias para aprovechar cache
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copia el código fuente y empaqueta
COPY src ./src
RUN mvn -q -DskipTests package

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar recursos estáticos al filesystem para que SimpleStaticFileHandler funcione dentro del contenedor
# HttpServerApplication configura staticfiles("/static"), y el handler usa rutas de filesystem.
COPY --from=build /app/src/main/resources/static /static

# Copiar el JAR generado (flexible ante cambios de versión)
ARG JAR_FILE=/app/target/*.jar
COPY --from=build ${JAR_FILE} /app/app.jar

# Crear usuario no root (sin paquetes adicionales)
RUN useradd -m -r -s /usr/sbin/nologin appuser \
    && chown -R appuser:appuser /app /static || true
USER appuser

# Exponer puerto por defecto (la app usa 35000 si no se pasan argumentos)
EXPOSE 35000
ENV PORT=35000

# Ejecutar la aplicación (usa el puerto por defecto si no se pasan args)
ENTRYPOINT ["java", "-jar", "/app/app.jar"]