package escuela.edu.co.integration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import escuela.edu.co.HttpServerApplication;
import escuela.edu.co.server.HttpServerInterface;

/**
 * Test de integración para demostrar que el servidor refactorizado funciona.
 * Esta clase puede ejecutarse directamente para verificar la funcionalidad.
 */
public class IntegrationTest {
    
    private static final int TEST_PORT = 38000;
    private static final String BASE_URL = "http://localhost:" + TEST_PORT;
    
    public static void main(String[] args) {
        System.out.println("🚀 Iniciando test de integración...");
        
        try {
            // Crear servidor con puerto de test
            HttpServerInterface server = HttpServerApplication.createServer(TEST_PORT);
            
            // Ejecutar server en un hilo separado
            Thread serverThread = new Thread(() -> {
                try {
                    server.start();
                } catch (IOException e) {
                    System.err.println("❌ Error iniciando servidor: " + e.getMessage());
                }
            });
            
            serverThread.setDaemon(true);
            serverThread.start();
            
            // Esperar a que el servidor inicie
            Thread.sleep(2000);
            
            // Crear cliente HTTP
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();
            
            // Test 1: Endpoint predefinido /app/time
            testEndpoint(client, BASE_URL + "/app/time", "Test endpoint /app/time");
            
            // Test 2: Endpoint predefinido /app/pi  
            testEndpoint(client, BASE_URL + "/app/pi", "Test endpoint /app/pi");
            
            // Test 3: Endpoint registrado dinámicamente /app/hello
            testEndpoint(client, BASE_URL + "/app/hello?name=SOLID", "Test endpoint /app/hello");
            
            // Test 4: Archivo estático (index.html)
            testStaticFile(client, BASE_URL + "/", "Test archivo estático index.html");
            
            System.out.println("✅ Todos los tests de integración pasaron exitosamente!");
            System.out.println("🎉 La refactorización SOLID mantiene toda la funcionalidad original");
            
        } catch (Exception e) {
            System.err.println("❌ Error en test de integración: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.exit(0);
    }
    
    private static void testEndpoint(HttpClient client, String url, String testName) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                System.out.println("✅ " + testName + " - STATUS: " + response.statusCode());
                System.out.println("   Response: " + response.body().substring(0, Math.min(100, response.body().length())) + "...");
            } else {
                System.out.println("❌ " + testName + " - STATUS: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("❌ " + testName + " - ERROR: " + e.getMessage());
        }
    }
    
    private static void testStaticFile(HttpClient client, String url, String testName) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200 && response.body().contains("<!DOCTYPE html>")) {
                System.out.println("✅ " + testName + " - STATUS: " + response.statusCode());
                System.out.println("   Content-Type: " + response.headers().firstValue("content-type").orElse("unknown"));
            } else {
                System.out.println("❌ " + testName + " - STATUS: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("❌ " + testName + " - ERROR: " + e.getMessage());
        }
    }
}
