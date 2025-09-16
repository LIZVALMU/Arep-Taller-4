package escuela.edu.co.framework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import escuela.edu.co.routing.Router;
import escuela.edu.co.routing.impl.SimpleRouter;
import escuela.edu.co.server.HttpServerInterface;
import escuela.edu.co.server.impl.SimpleHttpServer;
import escuela.edu.co.staticfiles.StaticFileHandler;
import escuela.edu.co.staticfiles.impl.SimpleStaticFileHandler;

public class MicroSpringBoot {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Debe especificar el nombre completo del POJO a cargar.");
            System.exit(1);
        }
        String pojoClassName = args[0];
        Class<?> pojoClass = Class.forName(pojoClassName);
        if (!pojoClass.isAnnotationPresent(RestController.class)) {
            System.out.println("La clase no tiene la anotación @RestController");
            System.exit(1);
        }
        Object pojoInstance = pojoClass.getDeclaredConstructor().newInstance();
        Router router = new SimpleRouter();
        StaticFileHandler staticFileHandler = new SimpleStaticFileHandler();
        int port = 35000;
        // Registrar métodos anotados con @GetMapping y soportar @RequestParam
        for (Method method : pojoClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping mapping = method.getAnnotation(GetMapping.class);
                String path = mapping.value();
                router.registerRoute(path, (req, resp) -> {
                    try {
                        // Preparar argumentos para el método
                        Class<?>[] paramTypes = method.getParameterTypes();
                        java.lang.annotation.Annotation[][] paramAnnotations = method.getParameterAnnotations();
                        Object[] argsForMethod = new Object[paramTypes.length];
                        for (int i = 0; i < paramTypes.length; i++) {
                            RequestParam reqParam = null;
                            for (java.lang.annotation.Annotation annotation : paramAnnotations[i]) {
                                if (annotation instanceof RequestParam) {
                                    reqParam = (RequestParam) annotation;
                                    break;
                                }
                            }
                            String value = reqParam != null ? reqParam.value() : null;
                            String defaultValue = reqParam != null ? reqParam.defaultValue() : "";
                            String paramValue = value != null ? req.getValues(value) : null;
                            if (paramValue == null || paramValue.isEmpty()) {
                                paramValue = defaultValue;
                            }
                            argsForMethod[i] = paramValue;
                        }
                        Object result = method.invoke(pojoInstance, argsForMethod);
                        if (result instanceof String) {
                            return (String) result;
                        } else {
                            return "Error: El método debe retornar String";
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        return "Error al invocar método: " + e.getMessage();
                    }
                });
                System.out.println("Ruta registrada: " + path);
            }
        }
        HttpServerInterface server = new SimpleHttpServer(port, new escuela.edu.co.request.impl.HttpRequestHandler(router, staticFileHandler, null));
        server.start();
    }
}
