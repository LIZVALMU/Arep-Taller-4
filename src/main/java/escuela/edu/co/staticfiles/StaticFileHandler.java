package escuela.edu.co.staticfiles;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface para el manejo de archivos estáticos.
 * Aplica el principio de responsabilidad única (Single Responsibility Principle).
 */
public interface StaticFileHandler {
    
    /**
     * Configura el directorio raíz para archivos estáticos.
     * 
     * @param path el path del directorio
     */
    void setStaticRoot(String path);
    
    /**
     * Sirve un archivo estático si existe.
     * 
     * @param path el path del archivo solicitado
     * @param out el stream de salida para enviar la respuesta
     * @return 
     * @throws IOException s
     */
    boolean serveStaticFile(String path, OutputStream out) throws IOException;
}
