# Servidor HTTP con Arquitectura SOLID – Framework Web en Java

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Maven](https://img.shields.io/badge/Maven-3.9+-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

---

## Descripción

Servidor HTTP ligero y extensible escrito en **Java 17**, diseñado siguiendo los principios **SOLID** para lograr un código mantenible, escalable y testeable. Puede usarse como base para microservicios, APIs REST, servir contenido estático o como framework tipo “MicroSpringBoot”.

Características principales:

* Soporta **HTTP/1.1** completo
* Enrutamiento dinámico y modular
* Manejo eficiente de archivos estáticos
* API REST con JSON
* Sistema de anotaciones para declarar rutas (controladores, RequestParam, etc.)
* Configuración flexible (puertos, rutas estáticas, etc.)
* Preparado para testing (JUnit 5)
* Compatibilidad para migraciones graduales manteniendo API original

---

## Tecnologías

| Tecnología                          | Versión | Propósito                                                              |
| ----------------------------------- | ------- | ---------------------------------------------------------------------- |
| **Java**                            | 17      | Lenguaje base, uso de características modernas (records, sealed, etc.) |
| **Maven**                           | 3.9+    | Gestión de dependencias, construcción, empaquetado                     |
| **JUnit 5**                         | 5.9.3   | Framework de testing unitario y de integración                         |
| **Sockets nativos (Java IO / NIO)** | –       | Comunicación de red de bajo nivel para manejo HTTP                     |
| **HTTP/1.1**                        | –       | Protocolo cliente-servidor estándar implementado                       |

---

## Estructura de proyecto

```
src/
 ├── main/
 │    ├── java/escuela/edu/co/
 │    │     │ HttpServerApplication.java
 │    │     │ Request.java
 │    │     │ Response.java
 │    │     │ RouteHandler.java
 │    │     ├── api/
 │    │     ├── framework/
 │    │     ├── request/
 │    │     ├── routing/
 │    │     ├── server/
 │    │     ├── staticfiles/
 │    │     └── utils/
 │    └── resources/
 │         └── static/
 │             ├ index.html
 │             ├ style.css
 │             ├ logo.png
 │             └ app.js
 └── test/
       └── java/escuela/edu/co/
           ├── integration/
           └── routing/impl/
```

* **framework**: clases para anotaciones personalizadas, controladores, RequestParam, RestController, etc.
* **routing / server / staticfiles**: lógica central de manejo de rutas, servidor HTTP, archivos estáticos.
* **utils**: parsers, ayudantes para response, request, etc.
* **tests**: unitarios e integración.

---

## Instalación y uso

### Prerrequisitos

* Java 17+ instalado y configurado (`java --version`)
* Maven 3.9+ (`mvn --version`)
* Git

### Clonar el proyecto

```bash
git clone https://github.com/LIZVALMU/TALLER-3_-Arquitecturas-de-Servidores-de-Aplicaciones.git
cd TALLER-3_-Arquitecturas-de-Servidores-de-Aplicaciones
```

### Construir el proyecto

```bash
mvn clean compile
```

### Empaquetar

```bash
mvn package
```

### Ejecutar localmente

Hay varias formas de iniciar el servidor:

| Opción                                | Comando                                                             | Descripción                            |
| ------------------------------------- | ------------------------------------------------------------------- | -------------------------------------- |
| **Principal recomendada**             | `java -cp target/classes escuela.edu.co.HttpServerApplication`      | Usa puerto y configuración por defecto |
| **Puerto personalizado**              | `java -cp target/classes escuela.edu.co.HttpServerApplication 8080` | Cambia el puerto (ejemplo 8080)        |
| **Compatibilidad con clase original** | `java -cp target/classes escuela.edu.co.HttpServer`                 | Si dependes de la clase antigua        |
| **Usar JAR ejecutable**               | `java -jar target/HttpServer-1.0-SNAPSHOT.jar`                      | Ejecuta el artefacto empaquetado       |

### Usando Docker

Se incluye un `Dockerfile` multi-stage para construir una imagen liviana.

1. Construir la imagen Docker:

   ```bash
   docker build -t arep-taller-4:latest .
   ```

2. Ejecutar contenedor usando puerto por defecto (35000):

   ```bash
   docker run --rm -p 35000:35000 arep-taller-4:latest
   ```

3. Ejecutar en otro puerto:

   ```bash
   docker run --rm -p 8080:8080 arep-taller-4:latest 8080
   ```

4. Probar con `curl` u navegador:

   ```bash
   curl http://localhost:35000/
   curl "http://localhost:35000/app/hello?name=Alison"
   ```

---

## Ejemplos de uso

### Endpoints disponibles

```bash
# Saludo personalizado
curl "http://localhost:35000/app/hello?name=Desarrollador"
# → {"message":"¡Hola, Desarrollador!"}

# Hora actual
curl "http://localhost:35000/app/time"
# → {"current":"2025-08-30T15:30:45"}

# Valor de PI
curl "http://localhost:35000/app/pi"
# → {"pi":3.1415926536}

# Información con parámetros
curl "http://localhost:35000/app/info?name=Juan&description=Prueba"
# → {"message":"name: Juan, description: Prueba","current":"2025-08-30T15:30:45"}
```

### Uso del framework tipo microSpringBoot

Con anotaciones personalizadas para controladores:

```java
@RestController
public class FirstWebService {
    @GetMapping("/hello")
    public String hello() {
        return "¡Hola desde el primer servicio web!";
    }
}
```

Y otro que usa parámetros:

```java
@RestController
public class GreetingController {
    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }
}
```

Cómo arrancar:

```bash
java -cp target/classes escuela.edu.co.framework.MicroSpringBoot escuela.edu.co.framework.FirstWebService
```

Ejemplos de `curl`:

```bash
curl "http://localhost:35000/greeting?name=Alison"
# → Hola Alison

curl "http://localhost:35000/greeting"
# → Hola World
```

---

## Cómo extender el framework

Ejemplos de extensibilidad:

* Crear tu propio router: implementar interfaz `Router`
* Crear manejador de archivos estáticos personalizado: implementar `StaticFileHandler`
* Añadir nuevos tipos de Request / Response, filtros, middleware, etc.

---

## Testing

Se incluyen pruebas unitarias e de integración usando **JUnit 5**.

Ejemplo de prueba:

```java
@Test
void testRouterRegistration() {
    Router router = new SimpleRouter();
    RouteHandler handler = (req, resp) -> "test";
    router.registerRoute("/test", handler);
    // Assert que al manejar "/test" retorne lo esperado
}
```

También hay pruebas de integración para simular peticiones reales al servidor.

---

## Despliegue en AWS

Aquí los pasos detallados para desplegar esta aplicación en **AWS**, incluyendo opciones que puedes elegir según tu escala y presupuesto.

### Opciones de infraestructura

Algunas alternativas comunes:

| Opción                          | Ventajas                                                                | Consideraciones                                                                                   |
| ------------------------------- | ----------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------- |
| **EC2** (instancia virtual)     | Control total, fácil de configurar, buen rendimiento para cargas medias | Debes encargarte del escalamiento, balanceo, backups, seguridad                                   |
| **Elastic Beanstalk**           | AWS gestiona infraestructura, despliegue automático, escalado, balanceo | Menos control de bajo nivel, puede costar un poco más en recursos                                 |
| **ECS / EKS** (contenedores)    | Usar Docker, orquestación, alta disponibilidad, escalado automático     | Requiere configuración extra, learning curve                                                      |
| **AWS Lambda (con contenedor)** | Pago por uso, escala automática, sin servidor                           | Fronteras en tiempo de ejecución, frío inicial, configuración de red/ALB si usas HTTP persistente |

### Despliegue con EC2

Aquí una guía paso a paso usando EC2:

1. Crear una instancia EC2:

   * Elegir un AMI que tenga Java 17 preinstalado, o instalarlo luego. Ejemplo: Amazon Linux 2, Ubuntu 22.04 LTS.
   * Elegir tipo de instancia (t2.micro, t3.small, según carga).
   * Configurar grupo de seguridad: permitir tráfico entrante HTTP (puerto que uses, ej. 35000 o 80) y acceso SSH (22) para mantenimiento.

2. Subir la aplicación

   * Empaqueta el jar o usa Docker.
   * Subir via SCP/SFTP o usar repositorio de contenedores (Docker Hub / ECR).

3. Instalar Java:

   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk -y
   ```

4. Ejecutar la aplicación

   * Si es jar:

     ```bash
     java -jar HttpServer-1.0-SNAPSHOT.jar 35000
     ```

   * Si es Docker:

     ```bash
     docker run -d -p 35000:35000 arep-taller-4:latest
     ```

5. Configurar como servicio (opcional), para que inicie automáticamente al reiniciar:

   Crear un archivo systemd, por ejemplo `/etc/systemd/system/myhttpserver.service`:

   ```
   [Unit]
   Description=Servidor HTTP SOLID Java
   After=network.target

   [Service]
   User=ec2-user
   WorkingDirectory=/home/ec2-user/app
   ExecStart=/usr/bin/java -jar /home/ec2-user/app/HttpServer-1.0-SNAPSHOT.jar 35000
   Restart=always

   [Install]
   WantedBy=multi-user.target
   ```

   Luego:

   ```bash
   sudo systemctl daemon-reload
   sudo systemctl enable myhttpserver.service
   sudo systemctl start myhttpserver.service
   ```

6. Configurar dominio / DNS (opcional)

   * Si tienes un dominio personalizado, apuntar A record al IP público de la instancia EC2.
   * Considerar usar **Amazon Route 53**.

7. HTTPS / SSL

   * Para producción, instalar certificado SSL.
   * Puedes usar **Let’s Encrypt** + `certbot` si tienes dominio; o AWS ACM si usas Load Balancer.

8. Escalabilidad

   * Si el tráfico crece, considera usar **Load Balancer** (ELB) frente a varias instancias EC2.
   * Automatizar despliegue con **Auto Scaling Groups**.
   * Monitorizar con **CloudWatch** para métricas (uso CPU, memoria, latencia).

### Despliegue con Elastic Beanstalk

Si prefieres no manejar infraestructura:

1. Empaquetar tu aplicación como jar o Docker.

2. Crear aplicación en Elastic Beanstalk:

   * Si es jar: usar plataforma **Java 17**.
   * Si usas Docker: usar plataforma Docker con un `Dockerrun.aws.json` o Dockerfile.

3. Subir artefacto, configurar entorno:

   * Variables de entorno si las necesitas.
   * Puerto: configurar el puerto donde la aplicación escucha (Beanstalk debe saberlo).

4. Elastic Beanstalk se encarga del balanceo, escalado horizontal en función de demanda.

5. Si necesitas dominio, usa Route 53 + CNAME que Beanstalk te da.

6. SSL: Beanstalk soporta cargar certificados o usar ACM + load balancer.

### Despliegue con ECS + ECR (contenedores)

Construir la imagen Docker localmente:

```bash
docker build -t mi-servidor-solid .
```

Subir la imagen a Amazon ECR:

```bash
aws ecr create-repository --repository-name mi-servidor-solid
# Autenticar, taggear y push de la imagen
```

Configurar las diferentes variables para la instancai de aws

![](/img/setting-aws-ec2.png)

![](/img/securiti-group.png)

Iniciamos con ssh en la maquina de AWS, y hacemos el pull de la imagen subida en docker hub

![](/img/docker-pull.png)

Despues hacemos el run para crear el contenedor de la imagen docker 

![](/img/docker-run.png)

Y verificamos con el siguiente comando para verificar que la instancia del contenedor funcione correctamente.

![](/img/docker-ps.png)

## Demo Funcional
-[Video de explicación] (https://youtu.be/fS7-y5AM65g)

### Consideraciones de seguridad y buenas prácticas

* Asegurar puertos abiertos solo los necesarios.
* Mantener Java actualizado con parches de seguridad.
* Cifrar comunicaciones con SSL/TLS.
* Limitar acceso SSH; usar claves, no contraseñas.
* Hacer backups de logs / datos críticos.
* Revisar permisos de archivos y directorios.
* Límite de tamaño de peticiones, evitar ataques de carga excesiva.

---

## Configuración

Variables / ajustes que se pueden parametrizar:

| Parámetro                      | Qué controla                                                                                          |
| ------------------------------ | ----------------------------------------------------------------------------------------------------- |
| **Puerto de escucha**          | Por defecto (ej. 35000), pero puede sobrescribirse vía argumento al arrancar o configuración externa. |
| **Ruta de archivos estáticos** | Carpeta `resources/static` por defecto; se puede cambiar.                                             |
| **Logging**                    | Nivel de log, salida a consola o archivo. (Se puede agregar si no está)                               |
| **Timeouts, headers**          | Control de tiempo de espera de peticiones, manejo de encabezados HTTP. (extensible)                   |
| **Anotaciones personalizadas** | @RestController, @GetMapping, @RequestParam; puedes extender con POST, PUT, etc.                      |

---

## Licencia

Este proyecto está bajo la **Licencia MIT**. Ver el archivo `LICENSE.md` para más detalles.

---

## Autor

**Alison Geraldine Valderrama Munar**
GitHub: [lizvalmu](https://github.com/LIZVALMU)
