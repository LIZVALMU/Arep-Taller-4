package escuela.edu.co;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import escuela.edu.co.api.ApiRequestHandler;
import escuela.edu.co.request.RequestHandler;
import escuela.edu.co.request.impl.HttpRequestHandler;
import escuela.edu.co.routing.Router;
import escuela.edu.co.routing.impl.SimpleRouter;
import escuela.edu.co.server.HttpServerInterface;
import escuela.edu.co.server.impl.SimpleHttpServer;
import escuela.edu.co.staticfiles.StaticFileHandler;
import escuela.edu.co.staticfiles.impl.SimpleStaticFileHandler;

/**
 * Clase principal refactorizada siguiendo principios SOLID.
 * Aplica el principio de inversión de dependencias (Dependency Inversion Principle).
 */
public class HttpServerApplication {
    
    private static final Logger LOGGER = Logger.getLogger(HttpServerApplication.class.getName());
    private static final int DEFAULT_PORT = 35000;
    
    // Instancias compartidas para mantener compatibilidad con API estática
    private static Router globalRouter;
    private static StaticFileHandler globalStaticFileHandler;
    
    public static void main(String[] args) throws IOException {
        // Configurar dependencias
        Router router = new SimpleRouter();
        StaticFileHandler staticFileHandler = new SimpleStaticFileHandler();
        ApiRequestHandler apiRequestHandler = new ApiRequestHandler();
        RequestHandler requestHandler = new HttpRequestHandler(router, staticFileHandler, apiRequestHandler);
        
        // Almacenar referencias globales para compatibilidad
        globalRouter = router;
        globalStaticFileHandler = staticFileHandler;
        
        // Configurar archivos estáticos
        staticfiles("/static");
        
        // Registrar rutas de ejemplo
        registerDefaultRoutes();
        
        // Determinar puerto
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                LOGGER.warning("Puerto inválido, usando puerto por defecto: " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }
        
        // Crear y iniciar servidor
        HttpServerInterface server = new SimpleHttpServer(port, requestHandler);
        server.start();
    }
    
    /**
     * Registra las rutas por defecto para mantener compatibilidad.
     */
    private static void registerDefaultRoutes() {
        get("/app/hello", (req, resp) -> {
            String name = req.getValues("name");
            if (name == null || name.isEmpty()) {
                name = "Mundo";
            }
            return String.format("{\"message\": \"Hello %s\", \"timestamp\": \"%s\"}",
                    name, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        });
        
        get("/app/pi", (req, resp) -> String.valueOf(Math.PI));
    }
    
    /**
     * Método estático para mantener compatibilidad con la API original.
     * Configura el directorio de archivos estáticos.
     * 
     * @param relativeOrAbsolute path relativo o absoluto del directorio
     */
    public static void staticfiles(String relativeOrAbsolute) {
        if (globalStaticFileHandler != null) {
            globalStaticFileHandler.setStaticRoot(relativeOrAbsolute);
        }
    }
    
    /**
     * Método estático para mantener compatibilidad con la API original.
     * Registra una ruta GET con su handler.
     * 
     * @param path el path de la ruta
     * @param handler el handler para procesar la solicitud
     */
    public static void get(String path, RouteHandler handler) {
        if (globalRouter != null) {
            globalRouter.registerRoute(path, handler);
        } else {
            LOGGER.log(Level.WARNING, "Router no inicializado, no se puede registrar ruta: {0}", path);
        }
    }
    
    /**
     * Crea una nueva instancia del servidor con configuración personalizada.
     * 
     * @param port puerto del servidor
     * @param router router personalizado
     * @param staticFileHandler manejador de archivos estáticos personalizado
     * @return instancia del servidor configurada
     */
    public static HttpServerInterface createServer(int port, Router router, StaticFileHandler staticFileHandler) {
        ApiRequestHandler apiRequestHandler = new ApiRequestHandler();
        RequestHandler requestHandler = new HttpRequestHandler(router, staticFileHandler, apiRequestHandler);
        return new SimpleHttpServer(port, requestHandler);
    }
    
    /**
     * Crea una nueva instancia del servidor con configuración por defecto.
     * 
     * @param port puerto del servidor
     * @return instancia del servidor con configuración por defecto
     */
    public static HttpServerInterface createServer(int port) {
        Router router = new SimpleRouter();
        StaticFileHandler staticFileHandler = new SimpleStaticFileHandler();
        return createServer(port, router, staticFileHandler);
    }
}
