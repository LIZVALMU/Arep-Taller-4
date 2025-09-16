package escuela.edu.co.staticfiles.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import escuela.edu.co.staticfiles.StaticFileHandler;

/**
 * Implementación simple del manejador de archivos estáticos.
 * Aplica el principio de responsabilidad única (Single Responsibility Principle).
 */
public class SimpleStaticFileHandler implements StaticFileHandler {
    
    private String staticRoot = "src/main/resources/static";
    
    @Override
    public void setStaticRoot(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path no puede ser null");
        }
        
        // Intenta resolver a carpeta target/classes + path si existe
        String candidate = "target/classes" + path;
        File f = new File(candidate);
        if (f.exists() && f.isDirectory()) {
            this.staticRoot = candidate;
        } else {
            // Si se pasa ruta absoluta, tomarla; sino fallback a la ruta pasada
            this.staticRoot = path.startsWith(File.separator) ? path : "src/main/resources" + path;
        }
        System.out.println("Archivos estáticos en: " + this.staticRoot);
    }
    
    @Override
    public boolean serveStaticFile(String path, OutputStream out) throws IOException {
        if ("/".equals(path)) {
            path = "/index.html";
        }
        
        File file = new File(staticRoot + path);
        
        if (!file.exists() || file.isDirectory()) {
            return false;
        }
        
        sendFileResponse(out, file, path);
        return true;
    }
    
    private void sendFileResponse(OutputStream out, File file, String path) throws IOException {
        String mimeType = determineMimeType(file, path);
        byte[] content = Files.readAllBytes(file.toPath());
        
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + mimeType + "\r\n" +
                "Content-Length: " + content.length + "\r\n" +
                "Connection: close\r\n\r\n";
        out.write(response.getBytes());
        out.write(content);
    }
    
    private String determineMimeType(File file, String path) throws IOException {
        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) {
            if (path.endsWith(".html"))
                mimeType = "text/html";
            else if (path.endsWith(".css"))
                mimeType = "text/css";
            else if (path.endsWith(".js"))
                mimeType = "application/javascript";
            else if (path.endsWith(".png"))
                mimeType = "image/png";
            else if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
                mimeType = "image/jpeg";
            else
                mimeType = "application/octet-stream";
        }
        return mimeType;
    }
}
