FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Descargar dependencias
RUN ./mvnw dependency:go-offline

# Copiar código fuente y archivos de configuración
COPY src ./src
COPY application-docker.properties ./

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests

# Crear el directorio de datos y establecer permisos
RUN mkdir -p /app/data && chmod 777 /app/data

# Crear archivo vacío de base de datos si no existe
RUN touch /app/iwellness_admin_events_api.db && chmod 666 /app/iwellness_admin_events_api.db

EXPOSE 8088

# Ejecutar con el perfil docker
CMD ["java", "-Dspring.profiles.active=docker", "-jar", "target/admin_events_api-0.0.1-SNAPSHOT.jar"]