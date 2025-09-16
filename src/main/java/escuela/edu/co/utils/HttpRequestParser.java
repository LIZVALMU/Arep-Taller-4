package escuela.edu.co.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilidad para parsear requests HTTP.
 * Aplica el principio de responsabilidad única (Single Responsibility Principle).
 */
public class HttpRequestParser {
    
    private static final Logger LOGGER = Logger.getLogger(HttpRequestParser.class.getName());
    
    /**
     * Lee los headers HTTP del request.
     * 
     * @param in el BufferedReader para leer los headers
     * @return un mapa con los headers parseados
     * @throws IOException si ocurre un error al leer
     */
    public static Map<String, String> parseHeaders(BufferedReader in) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
            int sep = headerLine.indexOf(": ");
            if (sep != -1) {
                String key = headerLine.substring(0, sep).toLowerCase();
                String value = headerLine.substring(sep + 2);
                headers.put(key, value);
            }
        }
        return headers;
    }
    
    /**
     * Parsea una query string en un mapa de parámetros.
     * 
     * @param queryString la query string a parsear
     * @return un mapa con los parámetros parseados
     */
    public static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return params;
        }

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                try {
                    String key = URLDecoder.decode(keyValue[0], "UTF-8");
                    String value = URLDecoder.decode(keyValue[1], "UTF-8");
                    params.put(key, value);
                } catch (UnsupportedEncodingException e) {
                    LOGGER.log(Level.WARNING, "Error decoding query parameter: " + pair, e);
                }
            }
        }
        return params;
    }
    
    /**
     * Parsea la línea de request HTTP para extraer método, path y query string.
     * 
     * @param requestLine la línea de request HTTP
     * @return un array con [método, path, queryString]
     */
    public static String[] parseRequestLine(String requestLine) {
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }
        
        String[] parts = requestLine.split(" ");
        if (parts.length < 2) {
            return null;
        }
        
        String method = parts[0];
        String fullPath = parts[1];
        
        String path;
        String queryString = "";
        int queryIndex = fullPath.indexOf('?');
        if (queryIndex != -1) {
            path = fullPath.substring(0, queryIndex);
            queryString = fullPath.substring(queryIndex + 1);
        } else {
            path = fullPath;
        }
        
        return new String[]{method, path, queryString};
    }
}
