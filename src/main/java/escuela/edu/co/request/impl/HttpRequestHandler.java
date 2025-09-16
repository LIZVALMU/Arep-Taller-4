package escuela.edu.co.request.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import escuela.edu.co.Request;
import escuela.edu.co.Response;
import escuela.edu.co.api.ApiRequestHandler;
import escuela.edu.co.request.RequestHandler;
import escuela.edu.co.routing.Router;
import escuela.edu.co.staticfiles.StaticFileHandler;
import escuela.edu.co.utils.HttpRequestParser;
import escuela.edu.co.utils.HttpResponseHelper;

/**
 * Implementación del manejador de requests HTTP.
 * Aplica el principio de responsabilidad única y dependencia de inversión.
 */
public class HttpRequestHandler implements RequestHandler {
    
    private static final Logger LOGGER = Logger.getLogger(HttpRequestHandler.class.getName());
    
    private final Router router;
    private final StaticFileHandler staticFileHandler;
    private final ApiRequestHandler apiRequestHandler;
    
    /**
     * Constructor que inyecta las dependencias.
     * Aplica el principio de inversión de dependencias (Dependency Inversion Principle).
     */
    public HttpRequestHandler(Router router, StaticFileHandler staticFileHandler, ApiRequestHandler apiRequestHandler) {
        this.router = router;
        this.staticFileHandler = staticFileHandler;
        this.apiRequestHandler = apiRequestHandler;
    }
    
    @Override
    public void handleRequest(Socket clientSocket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {
            
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }
            
            Map<String, String> headers = HttpRequestParser.parseHeaders(in);
            String[] parsedRequest = HttpRequestParser.parseRequestLine(requestLine);
            
            if (parsedRequest == null) {
                HttpResponseHelper.send400(out);
                return;
            }
            
            String method = parsedRequest[0];
            String path = parsedRequest[1];
            String queryString = parsedRequest[2];
            
            Map<String, String> queryParams = HttpRequestParser.parseQueryString(queryString);
            
            // Manejar rutas de API
            if (path.startsWith("/app/")) {
                handleApiRoutes(method, path, queryParams, headers, in, out);
                return;
            }
            
            // Solo GET para archivos estáticos
            if (!"GET".equals(method)) {
                HttpResponseHelper.send405(out);
                return;
            }
            
            // Intentar servir archivo estático
            if (!staticFileHandler.serveStaticFile(path, out)) {
                HttpResponseHelper.send404(out, path);
            }
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error manejando request", e);
            throw e;
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error cerrando socket", e);
            }
        }
    }
    
    private void handleApiRoutes(String method, String path, Map<String, String> queryParams,
                                Map<String, String> headers, BufferedReader in, OutputStream out) throws IOException {
        
        // Primero intentar con rutas registradas dinámicamente
        if ("GET".equals(method)) {
            try {
                Request req = new Request(method, path, queryParams, headers, in);
                Response resp = new Response(out);
                
                if (router.handleRoute(method, path, req, resp)) {
                    return;
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error ejecutando handler registrado para ruta " + path, e);
                HttpResponseHelper.send500(out);
                return;
            }
        }
        
        // Fallback a endpoints predefinidos
        if (!apiRequestHandler.handleApiRequest(method, path, queryParams, headers, out)) {
            HttpResponseHelper.send404(out, path);
        }
    }
}
