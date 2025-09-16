package escuela.edu.co.server;

import java.io.IOException;

/**
 * Interface que define las operaciones básicas de un servidor HTTP.
 * Implementa el principio de segregación de interfaces (Interface Segregation Principle).
 */
public interface HttpServerInterface {
    
    /**
     * Inicia el servidor en el puerto especificado.
     * 
     * @throws IOException si ocurre un error al iniciar el servidor
     */
    void start() throws IOException;
    
    /**
     * Detiene el servidor de manera controlada.
     */
    void stop();
    
    /**
     * Verifica si el servidor está ejecutándose.
     * 
     * @return true si el servidor está activo, false en caso contrario
     */
    boolean isRunning();
}
