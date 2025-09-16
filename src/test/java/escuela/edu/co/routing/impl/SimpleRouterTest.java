package escuela.edu.co.routing.impl;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import escuela.edu.co.Request;
import escuela.edu.co.Response;
import escuela.edu.co.RouteHandler;

/**
 * Tests para SimpleRouter - demuestra la testabilidad de la arquitectura SOLID.
 */
class SimpleRouterTest {
    
    private SimpleRouter router;
    private ByteArrayOutputStream outputStream;
    private Response response;
    
    @BeforeEach
    void setUp() {
        router = new SimpleRouter();
        outputStream = new ByteArrayOutputStream();
        response = new Response(outputStream);
    }
    
    @Test
    @DisplayName("Should register route successfully")
    void shouldRegisterRouteSuccessfully() {
        // Given
        String path = "/test";
        RouteHandler handler = (req, resp) -> "test response";
        
        // When
        router.registerRoute(path, handler);
        
        // Then
        assertTrue(router.getRoutes().containsKey(path));
        assertEquals(handler, router.getRoutes().get(path));
    }
    
    @Test
    @DisplayName("Should handle registered GET route")
    void shouldHandleRegisteredGetRoute() throws Exception {
        // Given
        String path = "/test";
        String expectedResponse = "test response";
        RouteHandler handler = (req, resp) -> expectedResponse;
        router.registerRoute(path, handler);
        
        Request request = new Request("GET", path, new HashMap<>(), new HashMap<>(), null);
        
        // When
        boolean handled = router.handleRoute("GET", path, request, response);
        
        // Then
        assertTrue(handled);
        String actualResponse = outputStream.toString();
        assertTrue(actualResponse.contains(expectedResponse));
    }
    
    @Test
    @DisplayName("Should not handle unregistered route")
    void shouldNotHandleUnregisteredRoute() throws Exception {
        // Given
        Request request = new Request("GET", "/nonexistent", new HashMap<>(), new HashMap<>(), null);
        
        // When
        boolean handled = router.handleRoute("GET", "/nonexistent", request, response);
        
        // Then
        assertFalse(handled);
    }
    
    @Test
    @DisplayName("Should not handle non-GET methods")
    void shouldNotHandleNonGetMethods() throws Exception {
        // Given
        String path = "/test";
        RouteHandler handler = (req, resp) -> "test response";
        router.registerRoute(path, handler);
        
        Request request = new Request("POST", path, new HashMap<>(), new HashMap<>(), null);
        
        // When
        boolean handled = router.handleRoute("POST", path, request, response);
        
        // Then
        assertFalse(handled);
    }
    
    @Test
    @DisplayName("Should throw exception for null path")
    void shouldThrowExceptionForNullPath() {
        // Given
        RouteHandler handler = (req, resp) -> "test";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            router.registerRoute(null, handler);
        });
    }
    
    @Test
    @DisplayName("Should throw exception for null handler")
    void shouldThrowExceptionForNullHandler() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            router.registerRoute("/test", null);
        });
    }
    
    @Test
    @DisplayName("Should handle JSON response correctly")
    void shouldHandleJsonResponseCorrectly() throws Exception {
        // Given
        String path = "/json";
        String jsonResponse = "{\"message\": \"Hello World\"}";
        RouteHandler handler = (req, resp) -> jsonResponse;
        router.registerRoute(path, handler);
        
        Request request = new Request("GET", path, new HashMap<>(), new HashMap<>(), null);
        
        // When
        boolean handled = router.handleRoute("GET", path, request, response);
        
        // Then
        assertTrue(handled);
        String actualResponse = outputStream.toString();
        assertTrue(actualResponse.contains("application/json"));
        assertTrue(actualResponse.contains(jsonResponse));
    }
}
