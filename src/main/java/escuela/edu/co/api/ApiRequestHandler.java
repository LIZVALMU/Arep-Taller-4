package escuela.edu.co.api;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import escuela.edu.co.utils.HttpResponseHelper;

/**
 * Manejador para los endpoints de API predefinidos.
 * Aplica el principio de responsabilidad única (Single Responsibility Principle).
 */
public class ApiRequestHandler {
    
    /**
     * Maneja las solicitudes a endpoints de API predefinidos.
     * 
     * @param method el método HTTP
     * @param path el path de la solicitud
     * @param queryParams los parámetros de consulta
     * @param headers los headers HTTP
     * @param out el OutputStream para la respuesta
     * @return true si el endpoint fue manejado, false en caso contrario
     * @throws IOException si ocurre un error al escribir la respuesta
     */
    public boolean handleApiRequest(String method, String path, Map<String, String> queryParams,
                                  Map<String, String> headers, OutputStream out) throws IOException {
        
        String jsonResponse = "";
        
        switch (path) {
            case "/app/hello":
                if ("GET".equals(method) || "POST".equals(method)) {
                    String name = queryParams.getOrDefault("name", "Alison");
                    if (name.isEmpty()) name = "Alison";
                    jsonResponse = String.format("{\"message\": \"¡Hola, %s!\"}",
                            name);
                } else {
                    HttpResponseHelper.send405(out);
                    return true;
                }
                break;
                
            case "/app/time":
                if ("GET".equals(method)) {
                    LocalDateTime now = LocalDateTime.now();
                    jsonResponse = String.format("{\"current\": \"%s\"}",
                            now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } else {
                    HttpResponseHelper.send405(out);
                    return true;
                }
                break;
                
            case "/app/info":
                if ("GET".equals(method)) {
                    String name = queryParams.getOrDefault("name", "Alison");
                    String description = queryParams.getOrDefault("description", "Se realiza el query param para el framework");
                    if (name.isEmpty()) name = "Alison";
                    LocalDateTime now = LocalDateTime.now();
                    jsonResponse = String.format(
                        "{\"message\": \"name: %s, description: %s\", \"current\": \"%s\"}",
                        name, description, now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    );
                } else {
                    HttpResponseHelper.send405(out);
                    return true;
                }
                break;
                
            case "/app/pi":
                if ("GET".equals(method)) {
                    jsonResponse = String.format("{\"pi\": %.10f}", Math.PI);
                } else {
                    HttpResponseHelper.send405(out);
                    return true;
                }
                break;
                
            default:
                return false; // No se manejó el endpoint
        }
        
        HttpResponseHelper.sendJsonResponse(out, jsonResponse);
        return true;
    }
}
