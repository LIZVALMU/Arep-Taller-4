package escuela.edu.co.server.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import escuela.edu.co.request.RequestHandler;
import escuela.edu.co.server.HttpServerInterface;

/**
 * Implementación simple del servidor HTTP.
 * Aplica el principio de responsabilidad única y dependencia de inversión.
 */
public class SimpleHttpServer implements HttpServerInterface {
    
    private static final Logger LOGGER = Logger.getLogger(SimpleHttpServer.class.getName());
    private static final int DEFAULT_PORT = 35000;
    
    private final int port;
    private final RequestHandler requestHandler;
    private boolean running = false;
    private ServerSocket serverSocket;
    
    /**
     * Constructor que inyecta las dependencias.
     * Aplica el principio de inversión de dependencias (Dependency Inversion Principle).
     */
    public SimpleHttpServer(RequestHandler requestHandler) {
        this(DEFAULT_PORT, requestHandler);
    }
    
    /**
     * Constructor con puerto personalizado.
     */
    public SimpleHttpServer(int port, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
    }
    
    @Override
    public void start() throws IOException {
        if (running) {
            LOGGER.warning("El servidor ya está ejecutándose");
            return;
        }
        
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            
            System.out.println("Servidor HTTP iniciado en http://localhost:" + port);
            LOGGER.info("Servidor iniciado en puerto: " + port);
            
            while (running) {
                Socket clientSocket = serverSocket.accept();
                // Manejo secuencial (sin crear hilos) según requisito del taller
                requestHandler.handleRequest(clientSocket);
            }
        } catch (IOException e) {
            if (running) {
                LOGGER.log(Level.SEVERE, "Error en el servidor", e);
                throw e;
            }
            // Si no está running, es porque se detuvo intencionalmente
        } finally {
            cleanup();
        }
    }
    
    @Override
    public void stop() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error cerrando ServerSocket", e);
            }
        }
        LOGGER.info("Servidor detenido");
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
    
    private void cleanup() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error durante cleanup", e);
            }
        }
    }
}
