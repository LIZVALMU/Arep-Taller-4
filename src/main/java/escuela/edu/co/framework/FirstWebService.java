package escuela.edu.co.framework;

@RestController
public class FirstWebService {
    @GetMapping("/hello")
    public String hello() {
        return "¡Hola desde el primer servicio web!";
    }
}
