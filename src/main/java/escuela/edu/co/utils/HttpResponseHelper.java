package escuela.edu.co.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Utilidad para generar respuestas HTTP.
 * Aplica el principio de responsabilidad única (Single Responsibility Principle).
 */
public class HttpResponseHelper {
    
    /**
     * Envía una respuesta JSON con código 200.
     * 
     * @param out el OutputStream para enviar la respuesta
     * @param json el contenido JSON
     * @throws IOException si ocurre un error al escribir
     */
    public static void sendJsonResponse(OutputStream out, String json) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + json.getBytes().length + "\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "Connection: close\r\n\r\n" + json;
        out.write(response.getBytes());
    }
    
    /**
     * Envía una respuesta 400 Bad Request.
     * 
     * @param out el OutputStream para enviar la respuesta
     * @throws IOException si ocurre un error al escribir
     */
    public static void send400(OutputStream out) throws IOException {
        String response = "HTTP/1.1 400 Bad Request\r\n" +
                "Content-Type: text/html\r\n\r\n" +
                "<h1>400 - Solicitud Incorrecta</h1>";
        out.write(response.getBytes());
    }
    
    /**
     * Envía una respuesta 404 Not Found.
     * 
     * @param out el OutputStream para enviar la respuesta
     * @param path el path que no fue encontrado
     * @throws IOException si ocurre un error al escribir
     */
    public static void send404(OutputStream out, String path) throws IOException {
        String response = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/html\r\n\r\n" +
                "<h1>404 - No encontrado</h1>" +
                "<p>Recurso no encontrado: " + path + "</p>" +
                "<a href=\"/\">Volver al inicio</a>";
        out.write(response.getBytes());
    }
    
    /**
     * Envía una respuesta 405 Method Not Allowed.
     * 
     * @param out el OutputStream para enviar la respuesta
     * @throws IOException si ocurre un error al escribir
     */
    public static void send405(OutputStream out) throws IOException {
        String response = "HTTP/1.1 405 Method Not Allowed\r\n" +
                "Content-Type: text/html\r\n\r\n" +
                "<h1>405 - Método no permitido</h1>";
        out.write(response.getBytes());
    }
    
    /**
     * Envía una respuesta 500 Internal Server Error.
     * 
     * @param out el OutputStream para enviar la respuesta
     * @throws IOException si ocurre un error al escribir
     */
    public static void send500(OutputStream out) throws IOException {
        String body = "<h1>500 - Error interno</h1>";
        String response = "HTTP/1.1 500 Internal Server Error\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "Content-Length: " + body.getBytes().length + "\r\n" +
                "Connection: close\r\n\r\n" + body;
        out.write(response.getBytes());
    }
}
