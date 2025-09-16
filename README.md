# Servidor HTTP con Arquitectura SOLID - Framework Web Java

Un servidor de aplicaciones web ligero y extensible construido en Java, siguiendo los principios SOLID para lograr un código mantenible, escalable y testeable.

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Maven](https://img.shields.io/badge/Maven-3.9+-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

##Características Principales

- **Servidor HTTP/1.1**: Implementación completa del protocolo
- **Enrutamiento Dinámico**: Sistema extensible de rutas
- **Archivos Estáticos**: Servicio eficiente de contenido estático
- **API REST**: Endpoints JSON listos para usar
- **Sistema de Anotaciones**: Mapeo declarativo de rutas
- **Configuración Flexible**: Múltiples configuraciones de servidor
- **Testeable**: Arquitectura preparada para testing
- **Compatibilidad**: Mantiene API original para migración gradual

---

## Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|:-----------|:--------|:----------|
| **Java** | 17 | Lenguaje principal con características modernas |
| **Maven** | 3.9+ | Gestión de dependencias y build |
| **JUnit 5** | 5.9.3 | Framework de testing (preparado) |
| **Sockets Nativos** | Java Native | Comunicación de red de bajo nivel |
| **HTTP/1.1** | - | Protocolo estándar implementado |

---

## Instalación y Ejecución

### Prerrequisitos

- **Java 17** o superior
- **Maven 3.9** o superior
- **Git**

### Instalación

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/LIZVALMU/TALLER-3_-Arquitecturas-de-Servidores-de-Aplicaciones.git
   cd TALLER-3_-Arquitecturas-de-Servidores-de-Aplicaciones
   ```

2. **Compilar el proyecto:**
   ```bash
   mvn clean compile
   ```

3. **Empaquetar la aplicación:**
   ```bash
   mvn package
   ```

### Ejecución

#### Opción 1: Usando la nueva clase principal (Recomendado)
```bash
java -cp target/classes escuela.edu.co.HttpServerApplication
```

#### Opción 2: Con puerto personalizado
```bash
java -cp target/classes escuela.edu.co.HttpServerApplication 8080
```

#### Opción 3: Usando compatibilidad con clase original
```bash
java -cp target/classes escuela.edu.co.HttpServer
```

#### Opción 4: Usando JAR ejecutable
```bash
java -jar target/HttpServer-1.0-SNAPSHOT.jar
```

### Acceso a la Aplicación

Una vez iniciado el servidor, accede en tu navegador:
- **Aplicación Web**: `http://localhost:35000/`
- **API REST**: `http://localhost:35000/app/`

---

## Ejemplos de Uso

### API REST Endpoints

#### Endpoints Predefinidos
```bash
# Saludo personalizado
curl "http://localhost:35000/app/hello?name=Desarrollador"
# Response: {"message": "¡Hola, Desarrollador!"}

# Hora actual
curl "http://localhost:35000/app/time"
# Response: {"current": "2025-08-30T15:30:45"}

# Valor de PI
curl "http://localhost:35000/app/pi"
# Response: {"pi": 3.1415926536}

# Información con parámetros
curl "http://localhost:35000/app/info?name=Juan&description=Prueba"
# Response: {"message": "name: Juan, description: Prueba", "current": "2025-08-30T15:30:45"}
```

#### Rutas Dinámicas Registradas
```bash
# Rutas registradas en HttpServerApplication
curl "http://localhost:35000/app/hello?name=SOLID"
# Response: {"message": "Hello SOLID", "timestamp": "2025-08-30T15:30:45"}

curl "http://localhost:35000/app/pi"
# Response: "3.141592653589793"
```

### Extensión del Framework

#### Crear Servidor Personalizado
```java
import escuela.edu.co.config.ServerConfiguration;
import escuela.edu.co.server.HttpServerInterface;

public class CustomServer {
    public static void main(String[] args) throws Exception {
        // Servidor de desarrollo
        HttpServerInterface devServer = ServerConfiguration.createDevelopmentServer();
        
        // Servidor personalizado
        HttpServerInterface customServer = ServerConfiguration
            .createCustomServer(9090, "custom/static/path");
        
        devServer.start();
    }
}
```

#### Usar el Sistema de Configuración
```java
import escuela.edu.co.HttpServerApplication;

// Crear servidor con configuración personalizada
HttpServerInterface server = HttpServerApplication.createServer(8080);

// Registrar rutas adicionales
HttpServerApplication.get("/api/custom", (req, resp) -> {
    return "{\"message\": \"Custom endpoint\"}";
});

server.start();
```

---

## Extensibilidad

### Implementar Router Personalizado
```java
public class CachedRouter implements Router {
    private final Map<String, RouteHandler> cache = new ConcurrentHashMap<>();
    
    @Override
    public void registerRoute(String path, RouteHandler handler) {
        cache.put(path, handler);
    }
    
    @Override
    public boolean handleRoute(String method, String path, Request request, Response response) {
        // Implementación con caché
        return false;
    }
}
```

### Implementar Manejador de Archivos Personalizado
```java
public class CachedStaticFileHandler implements StaticFileHandler {
    private final Map<String, byte[]> fileCache = new ConcurrentHashMap<>();
    
    @Override
    public void setStaticRoot(String path) {
        // Configurar con caché
    }
    
    @Override
    public boolean serveStaticFile(String path, OutputStream out) throws IOException {
        // Implementación con caché de archivos
        return false;
    }
}
```

---

## Testing

El proyecto está preparado para testing con JUnit 5:

```java
@Test
void testRouterRegistration() {
    Router router = new SimpleRouter();
    RouteHandler handler = (req, resp) -> "test";
    
    router.registerRoute("/test", handler);
    
    // Assertions...
}
```

---

## 📜 Licencia

Este proyecto está bajo la **Licencia MIT**. Ver el archivo `LICENSE` para más detalles.

---

## 👤 Autor

**Alison Geraldine Valderrama Munar**
- GitHub: [@lizvalmu](https://github.com/lizvalmu)
- Proyecto: [TALLER-3_-Arquitecturas-de-Servidores-de-Aplicaciones](https://github.com/LIZVALMU/TALLER-3_-Arquitecturas-de-Servidores-de-Aplicaciones)

---

## Ejemplo: Framework Minimalista tipo MicroSpringBoot

Puedes crear un servicio web básico usando POJOs y anotaciones personalizadas:

```java
package escuela.edu.co.framework;

@RestController
public class FirstWebService {
    @GetMapping("/hello")
    public String hello() {
        return "¡Hola desde el primer servicio web!";
    }
}
```

Para iniciar el servidor y publicar el POJO:

```bash
java -cp target/classes escuela.edu.co.framework.MicroSpringBoot escuela.edu.co.framework.FirstWebService
```

Accede en tu navegador o con curl:

```
curl http://localhost:35000/hello
# Respuesta: ¡Hola desde el primer servicio web!
```

---

## Ejemplo avanzado: Uso de @RequestParam

Puedes crear un controlador que reciba parámetros desde la URL:

```java
package escuela.edu.co.framework;

@RestController
public class GreetingController {
    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }
}
```

Para iniciar el servidor y publicar el controlador:

```bash
java -cp target/classes escuela.edu.co.framework.MicroSpringBoot escuela.edu.co.framework.GreetingController
```

Accede en tu navegador o con curl:

```
curl "http://localhost:35000/greeting?name=Alison"
# Respuesta: Hola Alison

curl "http://localhost:35000/greeting"
# Respuesta: Hola World
```

---
* Este proyecto demuestra cómo aplicar principios de ingeniería de software sólidos para crear código mantenible y escalable.*

