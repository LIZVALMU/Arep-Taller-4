package escuela.edu.co.routing;

import escuela.edu.co.RouteHandler;

/**
 * Interface para el manejo de rutas.
 * Aplica el principio abierto/cerrado (Open/Closed Principle).
 */
public interface Router {
    
    /**
     * Registra una ruta GET con su correspondiente handler.
     * 
     * @param path el path de la ruta
     * @param handler el handler que procesará las solicitudes
     */
    void registerRoute(String path, RouteHandler handler);
    
    /**
     * Busca y ejecuta el handler correspondiente a la ruta especificada.
     * 
     * @param method el método HTTP
     * @param path el path solicitado
     * @param request la solicitud HTTP
     * @param response la respuesta HTTP
     * @return true si se encontró y ejecutó el handler, false en caso contrario
     * @throws Exception si ocurre un error durante la ejecución
     */
    boolean handleRoute(String method, String path, escuela.edu.co.Request request, escuela.edu.co.Response response) throws Exception;
}
