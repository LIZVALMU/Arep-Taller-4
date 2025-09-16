package escuela.edu.co.request;

import java.io.IOException;
import java.net.Socket;

/**
 * Interface para el manejo de requests HTTP.
 * Aplica el principio de responsabilidad única (Single Responsibility Principle).
 */
public interface RequestHandler {
    
    /**
     * Procesa una solicitud HTTP entrante.
     * 
     * @param clientSocket el socket del cliente que realiza la solicitud
     * @throws IOException si ocurre un error durante el procesamiento
     */
    void handleRequest(Socket clientSocket) throws IOException;
}
