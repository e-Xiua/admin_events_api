# API de Administración de Eventos - iWellness

## Descripción
API REST para la administración de eventos en la plataforma iWellness. Permite crear, consultar, actualizar y eliminar eventos con funcionalidades de seguridad y notificaciones por email.

## Tecnologías Utilizadas

### Framework Principal
- **Spring Boot 3.4.3** - Framework principal de Java
- **Java 17** - Versión del lenguaje de programación

### Base de Datos
- **SQLite** - Base de datos embebida
- **Spring Data JPA** - Abstracción de persistencia
- **Hibernate Community Dialects** - Soporte para SQLite

### Dependencias Principales
- **Spring Web** - Para crear APIs REST
- **Spring Boot DevTools** - Herramientas de desarrollo
- **Lombok** - Reducción de código boilerplate
- **OpenFeign** - Cliente HTTP declarativo
- **Spring Mail** - Envío de correos electrónicos

### Testing
- **JUnit 5** - Framework de testing
- **Mockito** - Mocking para pruebas unitarias
- **Spring Boot Test** - Testing integrado

### Containerización
- **Docker** - Containerización de la aplicación
- **Docker Compose** - Orquestación de contenedores

## Estructura del Proyecto

```
src/main/java/com/iwellness/admin_events_api/
├── clientes/           # Clientes Feign para comunicación externa
├── config/             # Configuraciones de la aplicación
├── controladores/      # Controladores REST
├── dto/               # Objetos de transferencia de datos
├── email/             # Servicios de email
├── entidades/         # Entidades JPA
├── exceptions/        # Excepciones personalizadas
├── mapper/            # Mappers entre entidades y DTOs
├── repositorios/      # Repositorios de datos
├── seguridad/         # Servicios de seguridad
└── servicios/         # Lógica de negocio
```

## Configuración

### Puertos
- **Puerto de la aplicación**: 8088

### Base de Datos
- **Archivo de BD**: `iwellness_admin_events_api.db`
- **Dialecto**: SQLite
- **DDL**: Actualización automática

### Email
- **SMTP Host**: smtp.outlook.com (desarrollo) / smtp.gmail.com (docker)
- **Puerto SMTP**: 587
- **Autenticación**: Habilitada
- **STARTTLS**: Habilitado

## API Endpoints

### Base URL
```
http://localhost:8088/evento
```

### Endpoints Disponibles

#### 1. Obtener Todos los Eventos
- **Método**: `GET`
- **Ruta**: `/evento`
- **Descripción**: Obtiene la lista completa de eventos
- **Respuesta**: Array de EventoDTO

#### 2. Obtener Evento por ID
- **Método**: `GET`
- **Ruta**: `/evento/{id}`
- **Parámetros**:
  - `id` (Long) - ID del evento
- **Descripción**: Obtiene un evento específico por su ID
- **Respuesta**: EventoDTO

#### 3. Crear Evento
- **Método**: `POST`
- **Ruta**: `/evento`
- **Body**: EventoDTO
- **Descripción**: Crea un nuevo evento
- **Respuesta**: EventoDTO creado

#### 4. Actualizar Evento Completo
- **Método**: `PUT`
- **Ruta**: `/evento`
- **Body**: EventoDTO
- **Descripción**: Actualiza completamente un evento existente
- **Respuesta**: EventoDTO actualizado

#### 5. Eliminar Evento
- **Método**: `DELETE`
- **Ruta**: `/evento/{id}`
- **Parámetros**:
  - `id` (Long) - ID del evento a eliminar
- **Descripción**: Elimina un evento específico
- **Respuesta**: Vacía

#### 6. Actualización Parcial de Evento
- **Método**: `PATCH`
- **Ruta**: `/evento/{id}`
- **Parámetros**:
  - `id` (Long) - ID del evento
- **Body**: Map<String, Object> con campos a actualizar
- **Descripción**: Actualiza parcialmente un evento
- **Respuesta**: EventoDTO actualizado

## Modelo de Datos

### EventoDTO
```json
{
  "id": "Long",
  "titulo": "String",
  "descripcion": "String", 
  "fecha": "Date",
  "duracion": "Long",
  "costo": "Long",
  "asistentes": ["String"],
  "tipo": "TipoEvento",
  "color": "String",
  "activo": "Boolean"
}
```

### Campos del Evento
- **id**: Identificador único del evento
- **titulo**: Nombre del evento
- **descripcion**: Descripción detallada del evento
- **fecha**: Fecha y hora del evento
- **duracion**: Duración en minutos
- **costo**: Costo del evento
- **asistentes**: Lista de asistentes al evento
- **tipo**: Tipo de evento (enum TipoEvento)
- **color**: Color asociado al evento para visualización
- **activo**: Estado del evento (activo/inactivo)

## Seguridad

### Validaciones Implementadas
- **Autenticación de Usuario**: Validación de usuario autenticado
- **Autorización por Rol**: Validación de permisos según rol del usuario
- **Validación de Datos**: Validación de integridad de datos de entrada

### Excepciones
- `UsuarioNoAutenticadoException`: Usuario no autenticado
- `UsuarioNoAutorizadoPorRolException`: Usuario sin permisos suficientes
- `EventoNotFoundException`: Evento no encontrado

## Ejecución

### Desarrollo Local
```bash
mvn spring-boot:run
```

### Con Docker
```bash
docker-compose up --build
```

### Configuración Docker
- **Puerto expuesto**: 8088:8088
- **Volumen de BD**: Montaje de base de datos SQLite
- **Perfil**: docker (application-docker.properties)

## Testing

### Ejecutar Pruebas
```bash
mvn test
```

### Cobertura de Testing
- Pruebas unitarias para controladores
- Pruebas unitarias para servicios

## Comunicación Externa

### Feign Clients
- **UsuarioFeignCliente**: Cliente para comunicación con servicios de usuario
- **Configuración personalizada**: Interceptores y decodificadores de error

### Servicios de Email
- **ServicioEmail**: Envío de notificaciones por correo electrónico
- **Configuración SMTP**: Soporte para Outlook y Gmail

## Notas Adicionales

- **CORS**: Habilitado para todos los orígenes (*)
- **Profiles**: Soporte para perfiles de desarrollo y docker
- **Logging**: Configuración estándar de Spring Boot
- **Build Tool**: Maven con plugins de Spring Boot
- **Resolver problemas con maven**: ejecutar  mvn -N wrapper:wrapper  