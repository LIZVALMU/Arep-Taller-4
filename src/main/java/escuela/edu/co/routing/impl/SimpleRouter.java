package escuela.edu.co.routing.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import escuela.edu.co.Request;
import escuela.edu.co.Response;
import escuela.edu.co.RouteHandler;
import escuela.edu.co.routing.Router;

/**
 * Implementación concreta del router para manejar rutas GET.
 * Aplica el principio abierto/cerrado (Open/Closed Principle).
 */
public class SimpleRouter implements Router {
    
    private static final Logger LOGGER = Logger.getLogger(SimpleRouter.class.getName());
    private final Map<String, RouteHandler> routes;
    
    public SimpleRouter() {
        this.routes = new HashMap<>();
    }
    
    @Override
    public void registerRoute(String path, RouteHandler handler) {
        if (path == null || handler == null) {
            throw new IllegalArgumentException("Path y handler no pueden ser null");
        }
        routes.put(path, handler);
        LOGGER.info("Ruta registrada: " + path);
    }
    
    @Override
    public boolean handleRoute(String method, String path, Request request, Response response) throws Exception {
        if (!"GET".equals(method) || !routes.containsKey(path)) {
            return false;
        }
        
        try {
            RouteHandler handler = routes.get(path);
            String result = handler.handle(request, response);
            
            if (result == null) {
                // El handler ya escribió la respuesta usando Response
                return true;
            }
            
            // Decidir tipo de contenido según el resultado
            String trimmed = result.trim();
            if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                response.sendJson(result);
            } else {
                response.sendText(result);
            }
            
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error ejecutando handler para ruta " + path, e);
            throw e;
        }
    }
    
    /**
     * Obtiene todas las rutas registradas.
     * 
     * @return mapa inmutable de rutas
     */
    public Map<String, RouteHandler> getRoutes() {
        return new HashMap<>(routes);
    }
}
