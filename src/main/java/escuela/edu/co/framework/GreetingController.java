package escuela.edu.co.framework;

@RestController
public class GreetingController {
    private static final String template = "Hello, %s!";
    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }
}
